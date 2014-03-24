package com.arunpjohny.webapp.birt.service.model;

import org.eclipse.birt.report.engine.api.IScalarParameterDefn;

public class ParameterTypeDef {
	public static final String SCALAR_PARAM_TYPE_SIMPLE = "simple";
	public static final String SCALAR_PARAM_TYPE_MULTI_VALUE = "multi-value";

	public enum DataType {
		ANY(IScalarParameterDefn.TYPE_ANY, "Any"), BOOLEAN(
				IScalarParameterDefn.TYPE_BOOLEAN, "Boolean"), DATE(
				IScalarParameterDefn.TYPE_DATE, "Date"), DATE_TIME(
				IScalarParameterDefn.TYPE_DATE_TIME, "Date Time"), DECIMAL(
				IScalarParameterDefn.TYPE_DECIMAL, "Decimal"), FLOAT(
				IScalarParameterDefn.TYPE_FLOAT, "Float"), INTEGER(
				IScalarParameterDefn.TYPE_INTEGER, "Integer"), STRING(
				IScalarParameterDefn.TYPE_STRING, "String"), TIME(
				IScalarParameterDefn.TYPE_TIME, "Time");

		private int id;
		private String description;

		DataType(int id, String desc) {
			this.id = id;
			this.description = desc;
		}

		public String getDescription() {
			return description;
		}

		public int getId() {
			return id;
		}

		public static DataType getType(int type) {
			DataType pType = ANY;

			switch (type) {
			case IScalarParameterDefn.TYPE_BOOLEAN:
				pType = BOOLEAN;
				break;
			case IScalarParameterDefn.TYPE_DATE:
				pType = DATE;
				break;
			case IScalarParameterDefn.TYPE_DATE_TIME:
				pType = DATE_TIME;
				break;
			case IScalarParameterDefn.TYPE_DECIMAL:
				pType = DECIMAL;
				break;
			case IScalarParameterDefn.TYPE_FLOAT:
				pType = FLOAT;
				break;
			case IScalarParameterDefn.TYPE_INTEGER:
				pType = INTEGER;
				break;
			case IScalarParameterDefn.TYPE_STRING:
				pType = STRING;
				break;
			case IScalarParameterDefn.TYPE_TIME:
				pType = TIME;
				break;
			}

			return pType;
		}

		@Override
		public String toString() {
			return this.description;
		}
	};

	public enum ControlType {
		TEXT_BOX(IScalarParameterDefn.TEXT_BOX, "Text Box"), LIST_BOX(
				IScalarParameterDefn.LIST_BOX, "List Box"), RADIO_BUTTON(
				IScalarParameterDefn.RADIO_BUTTON, "Check Box"), CHECK_BOX(
				IScalarParameterDefn.CHECK_BOX, "Check Box");

		private int id;
		private String description;

		ControlType(int id, String desc) {
			this.id = id;
			this.description = desc;
		}

		public String getDescription() {
			return description;
		}

		public int getId() {
			return id;
		}

		public static ControlType getType(int type) {
			ControlType pType = TEXT_BOX;

			switch (type) {
			case IScalarParameterDefn.TEXT_BOX:
				pType = TEXT_BOX;
				;
				break;
			case IScalarParameterDefn.LIST_BOX:
				pType = LIST_BOX;
				break;
			case IScalarParameterDefn.RADIO_BUTTON:
				pType = RADIO_BUTTON;
				break;
			case IScalarParameterDefn.CHECK_BOX:
				pType = CHECK_BOX;
				break;
			default:
				pType = TEXT_BOX;
				break;
			}

			return pType;
		}

		@Override
		public String toString() {
			return this.description;
		}
	};

	public enum ParameterType {
		CASCADING_PARAMETER_GROUP(
				IScalarParameterDefn.CASCADING_PARAMETER_GROUP,
				"Cascading Parameter Group"), FILTER_PARAMETER(
				IScalarParameterDefn.FILTER_PARAMETER, "Filter Parameter"), LIST_PARAMETER(
				IScalarParameterDefn.LIST_PARAMETER, "List Parameter"), PARAMETER_GROUP(
				IScalarParameterDefn.PARAMETER_GROUP, "Parameter Group"), SCALAR_PARAMETER(
				IScalarParameterDefn.SCALAR_PARAMETER, "Scalar Parameter"), TABLE_PARAMETER(
				IScalarParameterDefn.TABLE_PARAMETER, "Table Parameter");

		private int id;
		private String description;

		ParameterType(int id, String desc) {
			this.id = id;
			this.description = desc;
		}

		public String getDescription() {
			return description;
		}

		public int getId() {
			return id;
		}

		public static ParameterType getType(int type) {
			ParameterType pType = SCALAR_PARAMETER;

			switch (type) {
			case IScalarParameterDefn.CASCADING_PARAMETER_GROUP:
				pType = CASCADING_PARAMETER_GROUP;
				break;
			case IScalarParameterDefn.FILTER_PARAMETER:
				pType = FILTER_PARAMETER;
				break;
			case IScalarParameterDefn.LIST_PARAMETER:
				pType = LIST_PARAMETER;
				break;
			case IScalarParameterDefn.PARAMETER_GROUP:
				pType = PARAMETER_GROUP;
				break;
			case IScalarParameterDefn.SCALAR_PARAMETER:
				pType = SCALAR_PARAMETER;
				break;
			case IScalarParameterDefn.TABLE_PARAMETER:
				pType = TABLE_PARAMETER;
				break;
			default:
				pType = SCALAR_PARAMETER;
				break;
			}

			return pType;
		}

		@Override
		public String toString() {
			return this.description;
		}
	};
}
