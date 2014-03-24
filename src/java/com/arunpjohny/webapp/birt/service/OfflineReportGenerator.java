package com.arunpjohny.webapp.birt.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.birt.core.exception.BirtException;

public interface OfflineReportGenerator {

	public File generateReportFile(OfflineReportContext context) throws IOException, BirtException;

	public boolean generate(OfflineReportContext context, OutputStream out) throws IOException, BirtException;
}
