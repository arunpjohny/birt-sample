package com.arunpjohny.webapp.birt.service.impl;

import static com.arunpjohny.webapp.birt.service.model.ParameterTypeDef.SCALAR_PARAM_TYPE_MULTI_VALUE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IRenderTask;
import org.eclipse.birt.report.engine.api.IReportDocument;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunTask;
import org.eclipse.birt.report.engine.api.IScalarParameterDefn;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

import com.arunpjohny.webapp.birt.service.OfflineReportContext;
import com.arunpjohny.webapp.birt.service.OfflineReportContext.ReportType;
import com.arunpjohny.webapp.birt.service.OfflineReportGenerator;
import com.arunpjohny.webapp.birt.service.model.OfflineReportInfo;
import com.arunpjohny.webapp.birt.service.model.ParameterInfo;
import com.arunpjohny.webapp.birt.service.model.ParameterTypeDef.ControlType;
import com.arunpjohny.webapp.birt.service.model.ParameterTypeDef.DataType;
import com.arunpjohny.webapp.birt.service.model.ParameterTypeDef.ParameterType;
import com.arunpjohny.webapp.birt.utils.DateUtils;

@Service
public class OfflineReportGeneratorImpl implements OfflineReportGenerator,
		DisposableBean {
	private Log log = LogFactory.getLog(getClass());

	@Value("${report.temp}")
	private Resource temp;

	@Value("${report.log}")
	private Resource reportLog;

	@Value("${birt.home}")
	private Resource engineHome;

	private IReportEngine engine;

	@Autowired
	private DataSource dataSource;

	private Map<String, OfflineReportInfo> reportInfoMap = new HashMap<>();

	public File generateReportFile(OfflineReportContext context)
			throws IOException, BirtException {
		if (log.isDebugEnabled()) {
			log.debug("Creating PDF.");
		}

		File outputFile = getTempFile(context);
		FileOutputStream fout = new FileOutputStream(outputFile);

		boolean isGenerated = generate(context, fout);

		if (log.isDebugEnabled()) {
			if (isGenerated == false) {
				log.debug("Unable to create document "
						+ outputFile.getAbsolutePath() + " .");
				outputFile = null;
			} else {
				log.debug("PDF " + outputFile.getAbsolutePath() + " created.");
			}
		}

		return outputFile;
	}

	@SuppressWarnings("unchecked")
	public boolean generate(OfflineReportContext context, OutputStream out)
			throws IOException, BirtException {
		File designFile = new File(context.getReportDesign());
		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			if (engine == null) {
				engine = createReportEngine();
			}

			if (designFile.exists() == false) {
				log.warn("Unable to create report using desing file "
						+ context.getFileName() + ".");
				return false;
			}

			IReportRunnable design = engine.openReportDesign(context
					.getReportDesign());

			updateReportInfo(designFile, design);

			IRunTask runTask = engine.createRunTask(design);

			runTask.getAppContext().put("OdaJDBCDriverPassInConnection", conn);

			if (context.getParams() != null) {
				OfflineReportInfo offlineReportInfo = reportInfoMap
						.get(designFile.getAbsolutePath());
				for (Iterator<Map.Entry<String, Object>> iterator = context
						.getParams().entrySet().iterator(); iterator.hasNext();) {
					Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iterator
							.next();
					setParameter(runTask, offlineReportInfo, entry.getKey(),
							entry.getValue());
				}
			}

			runTask.validateParameters();

			File file = getReportDocumentFile();
			runTask.run(file.getAbsolutePath());
			runTask.close();

			IReportDocument iReportDocument = engine.openReportDocument(file
					.getAbsolutePath());
			IRenderTask renderTask = engine.createRenderTask(iReportDocument);
			renderTask
					.getAppContext()
					.put("org.eclipse.birt.report.data.oda.subjdbc.SubOdaJdbcDriver",
							conn);
			renderTask.setRenderOption(getRenderOption(context, out));
			renderTask.render();
			renderTask.close();
			iReportDocument.close();
			file.delete();
		} finally {
			out.close();
			try {
				DataSourceUtils.releaseConnection(conn, dataSource);
			} catch (Exception e) {
				log.error("Error while closing the connection object. ERROR: ",
						e);
			}
		}
		log.debug("Report createdusing desing file " + context.getFileName()
				+ ".");
		return true;
	}

	private File getReportDocumentFile() throws IOException {
		File file = null;
		synchronized (this) {
			do {
				file = new File(getTempFolder(), UUID.randomUUID().toString());
			} while (file.exists());
		}
		return file;
	}

	private void setParameter(IRunTask task,
			OfflineReportInfo offlineReportInfo, String paramName,
			Object paramValue) {
		if (log.isDebugEnabled()) {
			log.debug("Setting parameter: " + paramName + ". Value: "
					+ paramValue);
		}

		ParameterInfo paramInfo = offlineReportInfo.getParameter(paramName);

		if (paramInfo != null && paramValue != null) {
			DataType dataType = paramInfo.getDataType();
			String paramString = paramValue.toString();
			if (paramValue instanceof Date) {
				paramString = DateUtils.formatDate((Date) paramValue);
			}

			if (dataType == DataType.BOOLEAN) {
				task.setParameterValue(paramName,
						BooleanUtils.toBoolean(paramString));
			} else if (dataType == DataType.DATE) {
				if (paramValue instanceof Date) {
					task.setParameterValue(paramName, paramString);
				} else {
					task.setParameterValue(paramName,
							DateUtils.parseDate(paramString));
				}
			} else if (dataType == DataType.DATE_TIME
					|| dataType == DataType.TIME) {
				if (paramValue instanceof Date) {
					task.setParameterValue(paramName, paramString);
				} else {
					task.setParameterValue(paramName,
							DateUtils.parseDateTime(paramString));
				}
			} else if (dataType == DataType.DECIMAL
					|| dataType == DataType.FLOAT) {
				task.setParameterValue(paramName,
						NumberUtils.toDouble(paramString));
			} else if (dataType == DataType.INTEGER) {
				task.setParameterValue(paramName,
						NumberUtils.toInt(paramString));
			} else if (dataType == DataType.STRING) {
				if (paramInfo.isAllowMultipleValues()) {
					if (paramValue instanceof String) {
						task.setParameterValue(paramName,
								paramString.split(","));
					} else {
						task.setParameterValue(paramName, paramValue);
					}
				} else {
					task.setParameterValue(paramName, paramString);
				}
			} else {
				task.setParameterValue(paramName, paramValue);
			}
		} else {
			task.setParameterValue(paramName, paramValue);
		}
	}

	private void updateReportInfo(File designFile, IReportRunnable design) {
		boolean dirty = true;
		OfflineReportInfo offlineReportInfo = null;
		if (reportInfoMap.containsKey(designFile.getAbsolutePath())) {
			offlineReportInfo = reportInfoMap.get(designFile.getAbsolutePath());
			if (offlineReportInfo.getLastModified() != null
					&& designFile.lastModified() <= offlineReportInfo
							.getLastModified().getTime()) {
				dirty = false;
			}
		}

		if (dirty) {
			if (offlineReportInfo == null) {
				offlineReportInfo = new OfflineReportInfo();
				offlineReportInfo.setKey(designFile.getAbsolutePath());
				reportInfoMap.put(designFile.getAbsolutePath(),
						offlineReportInfo);
			}
			offlineReportInfo.setLastModified(new Date(designFile
					.lastModified()));

			IGetParameterDefinitionTask ptask = engine
					.createGetParameterDefinitionTask(design);
			Collection<IScalarParameterDefn> params = ptask
					.getParameterDefns(false);
			for (IScalarParameterDefn scalar : params) {
				ParameterInfo paramInfo = new ParameterInfo(scalar.getName());
				paramInfo.setDataType(DataType.getType(scalar.getDataType()));
				paramInfo.setContolType(ControlType.getType(scalar
						.getControlType()));
				paramInfo.setParameterType(ParameterType.getType(scalar
						.getParameterType()));
				paramInfo.setAllowMultipleValues(StringUtils.equals(
						scalar.getScalarParameterType(),
						SCALAR_PARAM_TYPE_MULTI_VALUE));
				offlineReportInfo.addParameter(scalar.getName(), paramInfo);
			}
		}
	}

	private IRenderOption getRenderOption(OfflineReportContext context,
			OutputStream out) {
		IRenderOption options = null;

		if (log.isDebugEnabled()) {
			log.debug("Setting options for " + context.getReportType());
		}
		if (context.getReportType().equals(ReportType.PDF)) {
			options = getPdfOptions();
		} else if (context.getReportType().equals(ReportType.WORD)) {
			options = getWordOptions();
		} else if (context.getReportType().equals(ReportType.EXCEL)) {
			options = getExcellOptions();
		} else if (context.getReportType().equals(ReportType.HTML)) {
			options = getHtmlOptions();
		}

		if (options != null) {
			options.setOutputStream(out);
		}
		return options;
	}

	private IRenderOption getPdfOptions() {
		PDFRenderOption options = new PDFRenderOption();
		options.setOutputFormat("pdf");
		return options;
	}

	private IRenderOption getWordOptions() {
		EXCELRenderOption options = new EXCELRenderOption();
		options.setOutputFormat("doc");
		return options;
	}

	private IRenderOption getExcellOptions() {
		EXCELRenderOption options = new EXCELRenderOption();
		options.setWrappingText(false);
		options.setOutputFormat("xls");
		return options;
	}

	private IRenderOption getHtmlOptions() {
		HTMLRenderOption options = new HTMLRenderOption();
		options.setOutputFormat("html");
		options.setHtmlPagination(false);
		options.setHtmlRtLFlag(false);
		options.setEmbeddable(false);
		return options;
	}

	private File getTempFile(OfflineReportContext context) throws IOException {
		Long time = new Long(0);
		synchronized (this) {
			time = (new Date()).getTime();
		}

		String fileName = getTempFolder().getAbsolutePath() + "/"
				+ context.getFileName() + "-" + time + "."
				+ context.getReportType().getExtenstion();

		if (log.isDebugEnabled()) {
			log.debug("Creating temp file" + fileName + ".");
		}
		return new File(fileName);
	}

	private File getTempFolder() throws IOException {
		File file = temp.getFile();
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	private IReportEngine createReportEngine() throws BirtException,
			IOException {
		if (engine == null) {
			synchronized (this) {
				if (engine == null) {
					if (log.isDebugEnabled()) {
						log.debug("Creating Report engine.");
					}

					EngineConfig config = new EngineConfig();
					HashMap<Object, Object> appContext = new HashMap<Object, Object>();
					appContext.put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY,
							this.getClass().getClassLoader());
					config.setAppContext(appContext);
					config.setEngineHome(engineHome.getFile().getAbsolutePath());
					config.setLogConfig(reportLog.getFile().getAbsolutePath(),
							Level.FINEST);

					Platform.startup();

					IReportEngineFactory factory = (IReportEngineFactory) Platform
							.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
					engine = factory.createReportEngine(config);
					engine.changeLogLevel(Level.SEVERE);
					if (log.isDebugEnabled()) {
						log.debug("Report engine created.");
					}
				}
			}
		}
		return engine;
	}

	public void destroy() throws Exception {
		if (engine != null) {
			engine.destroy();
			Platform.shutdown();
		}
	}
}
