package com.arunpjohny.webapp.birt.service.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OfflineReportInfo {
	private String key;
	private Date lastModified;
	private Map<String, ParameterInfo> parameters = new HashMap<String, ParameterInfo>();

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Map<String, ParameterInfo> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, ParameterInfo> parameters) {
		this.parameters = parameters;
	}

	public void addParameter(String name, ParameterInfo parameter) {
		this.parameters.put(name, parameter);
	}

	public ParameterInfo getParameter(String name) {
		return parameters.get(name);
	}
}
