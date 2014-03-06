package openml.xml;

import openml.settings.Constants;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class Run {

	private final String oml = Constants.OPENML_XMLNS;
	private int task_id;
	private int implementation_id;
	private String error_message;
	private Parameter_setting[] parameter_settings;
	private Data input_data;
	private Data output_data;
	
	public Run( int task_id, String error_message, int implementation_id, Parameter_setting[] parameter_settings ) {
		this.task_id = task_id;
		this.implementation_id = implementation_id;
		this.error_message = error_message;
		this.parameter_settings = parameter_settings;
		
		this.output_data = new Data();
		this.input_data = new Data();
	}
	
	public String getOml() {
		return oml;
	}

	public int getTask_id() {
		return task_id;
	}

	public int getImplementation_id() {
		return implementation_id;
	}
	
	public String getError_message() {
		return error_message;
	}
	
	public Parameter_setting[] getParameter_settings() {
		return parameter_settings;
	}
	
	public void addInputData( String name, String url  ) {
		input_data.addDataset( name, url );
	}
	
	public void addOutputData( String name, String url  ) {
		output_data.addDataset( name, url );
	}
	
	public void addOutputEvaluation( String name, String implementation,
			Double value, String[] array_data ) {
		output_data.addEvaluation( name, implementation, value, array_data);
	}

	public static class Parameter_setting {
		private String name;
		private String value;
		private int component;
		
		public Parameter_setting(int component, String name, String value) {
			this.name = name;
			this.component = component;
			this.value = value;
		}
		
		public String getName() {
			return name;
		}
		public int getComponent() {
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
	
	public static class Data {
		private Dataset[] dataset;
		private Evaluation[] evaluation;
		
		public Data( ) {
			dataset = new Dataset[0];
			evaluation = new Evaluation[0];
		}
		
		public Dataset[] getDataset() {
			return dataset;
		}

		public Evaluation[] getEvaluation() {
			return evaluation;
		}
		
		public void addDataset( String name, String url ) {
			Dataset d = new Dataset( name, url);
			dataset = ArrayUtils.addAll( dataset, d );
		}
		
		public void addEvaluation( String name, String implementation,
				Double value, String[] array_data ) {
			Evaluation e = new Evaluation( name, implementation, value, array_data);
			evaluation = ArrayUtils.addAll( evaluation, e );
		}

		public static class Dataset {
			private int did;
			private String name;
			private String url;
			
			public Dataset( String name, String url ) {
				this.name = name;
				this.url = url;
			}
			
			public int getDid() {
				return did;
			}
			public String getName() {
				return name;
			}
			public String getUrl() {
				return url;
			}
		}
		
		public static class Evaluation {
			private int did;
			private String name;
			private String implementation;
			private Double value;
			private String array_data;
			
			public Evaluation( String name, String implementation,
					Double value, String[] array_data) {
				super();
				this.name = name;
				this.implementation = implementation;
				this.value = value;
				this.array_data = "[ " + StringUtils.join( array_data, ", " ) + " ]";
			}
			
			public int getDid() {
				return did;
			}
			public String getName() {
				return name;
			}
			public String getImplementation() {
				return implementation;
			}
			public Double getValue() {
				return value;
			}
			public String getArray_data() {
				return array_data;
			}
		}
	}
}
