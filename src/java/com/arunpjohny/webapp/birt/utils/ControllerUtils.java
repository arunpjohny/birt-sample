package com.arunpjohny.webapp.birt.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ControllerUtils {
	private static Log log = LogFactory.getLog(ControllerUtils.class);

	public static void sendFile(HttpServletResponse response, File file,
			String name) throws IOException {
		if (file.exists()) {
			sendStream(response, name, new FileInputStream(file),
					(int) file.length());
		} else {
			log.error("File not found:" + file.getAbsolutePath());
			throw new FileNotFoundException(
					"Unable to find the requested resource!");
		}
	}

	public static void sendStream(HttpServletResponse response,
			String fileName, InputStream fis, int length) throws IOException {
		writeFileUploadHeaders(response, fileName, length);
		ServletOutputStream ouputStream = response.getOutputStream();
		try {
			int b;
			while ((b = fis.read()) != -1) {
				ouputStream.write(b);
			}
		} finally {
			ouputStream.flush();
			ouputStream.close();
			fis.close();
		}
	}

	public static void writeFileUploadHeaders(HttpServletResponse response,
			String fileName, int length) {
		String contentType = getContentType(fileName);
		String disHeader = "Attachment;Filename=\"" + fileName + "\"";
		response.setContentType(contentType);
		response.setHeader("Content-Disposition", disHeader);
		response.setHeader("Cache-Control", "private, max-age=5");

		/*
		 * The Pragma header value no-cache will cause download of files in IE
		 * to fail if we are using SSL. If we set the security constraint as
		 * CONFIDENTIAL then Apache Tomcat will add the cache headers
		 * `Cache-Control` and `Pragma` if they are not present in the response
		 * headers. This will cause download in IE to fail. In order to solve
		 * this issue we are setting the Pragma values as an empty string("").
		 * This seems to be working fine in the given test cases.
		 * 
		 * 
		 * Ref: http://geekcredential.wordpress.com/2009/04/21/grails-file
		 * -serving-controller-and-tomcat-ssl-problem/
		 */
		response.setHeader("Pragma", "");
		if (length != -1) {
			response.setContentLength(length);
		}
	}

	public static String getContentType(String fileName) {
		String contentType = "application/";
		String fileExtension = fileName
				.substring(fileName.lastIndexOf('.') + 1).toUpperCase();
		if ("XLS".equals(fileExtension) || "XLSX".equals(fileExtension)) {
			contentType = contentType + "vnd.ms-excel";
		} else if ("DOC".equals(fileExtension) || "DOCX".equals(fileExtension)) {
			contentType = contentType + "msword";
		} else if ("PDF".equals(fileExtension)) {
			contentType = contentType + "pdf";
		} else if ("RTF".equals(fileExtension)) {
			contentType = contentType + "msword";
		} else if ("PPS".equals(fileExtension)) {
			contentType = contentType + "vnd.ms-powerpoint";
		} else if ("PPT".equals(fileExtension) || "PPTX".equals(fileExtension)) {
			contentType = contentType + "vnd.ms-powerpoint";
		} else if ("PNG".equals(fileExtension)) {
			contentType = "image/png";
		} else if ("JPG".equals(fileExtension)) {
			contentType = "image/jpeg";
		} else if ("GIF".equals(fileExtension) || "BMP".equals(fileExtension)) {
			contentType = "image/" + fileExtension;
		} else if ("ZIP".equals(fileExtension)) {
			contentType += "zip";
		} else {
			contentType = "text/HTML";
		}
		return contentType;
	}
}
