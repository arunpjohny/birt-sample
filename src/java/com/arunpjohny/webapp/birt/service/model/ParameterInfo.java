package com.arunpjohny.webapp.birt.service.model;

import com.arunpjohny.webapp.birt.service.model.ParameterTypeDef.*;

public class ParameterInfo {

	private String name;

	private DataType dataType;

	private ControlType contolType;

	private ParameterType parameterType;

	private boolean allowMultipleValues;

	public ParameterInfo(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public ControlType getContolType() {
		return contolType;
	}

	public void setContolType(ControlType contolType) {
		this.contolType = contolType;
	}

	public ParameterType getParameterType() {
		return parameterType;
	}

	public void setParameterType(ParameterType parameterType) {
		this.parameterType = parameterType;
	}

	public boolean isAllowMultipleValues() {
		return allowMultipleValues;
	}

	public void setAllowMultipleValues(boolean allowMultipleValues) {
		this.allowMultipleValues = allowMultipleValues;
	}

}
