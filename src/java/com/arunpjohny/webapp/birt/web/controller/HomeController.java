package com.arunpjohny.webapp.birt.web.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.birt.core.exception.BirtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.arunpjohny.webapp.birt.service.OfflineReportContext;
import com.arunpjohny.webapp.birt.service.OfflineReportContext.ReportType;
import com.arunpjohny.webapp.birt.service.OfflineReportGenerator;
import com.arunpjohny.webapp.birt.utils.ControllerUtils;

import freemarker.template.TemplateException;

@Controller
public class HomeController {

	@Autowired
	private OfflineReportGenerator offlineReportGenerator;
	
	@RequestMapping(value = { "/", "/home" })
	public ModelAndView home(HttpServletRequest request) throws IOException,
			TemplateException {
		Map<String, Object> model = new HashMap<>();
		return new ModelAndView("/WEB-INF/views/home", model);
	}

	@RequestMapping(value = {"/report" })
	public void report(HttpServletRequest request, HttpServletResponse response) throws IOException,
			TemplateException, BirtException {
		File file = new File("E:/my-workspace/birt-sample/src/report","report1.rptdesign");
		OfflineReportContext context = new OfflineReportContext(file.getAbsolutePath(), ReportType.PDF, null, null);
		File report = offlineReportGenerator.generateReportFile(context);
		ControllerUtils.sendFile(response, report, "Report 1.pdf");
	}

}

