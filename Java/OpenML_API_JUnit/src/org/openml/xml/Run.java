package org.openml.xml;

import org.openml.constants.Constants;

public class Run {

	private final String oml = Constants.OPENML_XMLNS;
	private int task_id;
	private String implementation_id;
	private String error_message;
	private Parameter_setting[] parameter_settings;
	
	public String getOml() {
		return oml;
	}

	public int getTask_id() {
		return task_id;
	}

	public String getImplementation_id() {
		return implementation_id;
	}
	
	public String getError_message() {
		return error_message;
	}
	
	public Parameter_setting[] getParameter_settings() {
		return parameter_settings;
	}

	public static class Parameter_setting {
		private String name;
		private String value;
		private String component;
		
		public Parameter_setting(String component, String name, String value) {
			this.name = name;
			this.component = component;
			this.value = value;
		}
		
		public String getName() {
			return name;
		}
		public String getComponent() {
			return component;
		}
		public String getValue() {
			return value;
		}
		
		@Override
		public String toString() {
			return component + "_" + name + ": " + value;
		}
	}
}
