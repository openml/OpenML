package openml.xml;

import java.io.IOException;
import java.io.Serializable;

import openml.algorithms.InstancesHelper;
import openml.algorithms.TaskInformation;
import openml.io.ApiConnector;
import openml.settings.Constants;
import weka.core.Instances;

public class Task implements Serializable {
	private static final long serialVersionUID = 987612341009L;

	private final String oml = Constants.OPENML_XMLNS;
	
	private Integer task_id;
	private String task_type;
	private Input[] inputs;
	private Output[] outputs;
	
	// for quick initialization. 
	public Task(int id) {
		this.task_id = id;
	}
	
	@Override
	public String toString() {
		String source_data = "Unknown dataset";
		try {
			source_data = TaskInformation.getSourceData(this).getDataSetDescription().getName();
		} catch (Exception e) {}
		return "Task " + getTask_id() + ": " + source_data + " - " + getTask_type();
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof Task) {
			if( ((Task)other).getTask_id() == getTask_id() )
				return true;
		}
		return false;
	}
	
	public String getOml() {
		return oml;
	}

	public Integer getTask_id() {
		return task_id;
	}

	public String getTask_type() {
		return task_type;
	}

	public Input[] getInputs() {
		return inputs;
	}

	public Output[] getOutputs() {
		return outputs;
	}

	public class Input implements Serializable {
		private static final long serialVersionUID = 987612341019L;
		private String name;
		private Data_set data_set;
		private Estimation_procedure estimation_procedure;
		private Evaluation_measures evaluation_measures;
		
		public String getName() {
			return name;
		}

		public Data_set getData_set() {
			return data_set;
		}
		
		public Estimation_procedure getEstimation_procedure() {
			return estimation_procedure;
		}

		public Evaluation_measures getEvaluation_measures() {
			return evaluation_measures;
		}
		
		public class Data_set implements Serializable {
			private static final long serialVersionUID = 987612341029L;
			private Integer data_set_id;
			private String target_feature;
			private DataSetDescription dsdCache;
			
			public Integer getData_set_id() {
				return data_set_id;
			}
			public String getTarget_feature() {
				return target_feature;
			}
			public DataSetDescription getDataSetDescription() throws Exception {
				if(dsdCache == null) {
					dsdCache = ApiConnector.openmlDataDescription(data_set_id);
				}
				return dsdCache;
			}
		}
		
		public class Estimation_procedure implements Serializable {
			private static final long serialVersionUID = 987612341039L;
			private String type;
			private String data_splits_url;
			private Parameter[] parameters;
			private Instances dsCache;
			
			public String getType() {
				return type;
			}

			public String getData_splits_url() {
				return data_splits_url;
			}

			public Parameter[] getParameters() {
				return parameters;
			}
			
			public Instances getData_splits() throws IOException {
				if(dsCache == null) {
					String serverMd5 = ApiConnector.getStringFromUrl( getData_splits_url().replace("/get/", "/md5/") );
					String identifier = getData_splits_url().substring( getData_splits_url().lastIndexOf('/') + 1 );
					
					dsCache = InstancesHelper.downloadAndCache( "splits", identifier, getData_splits_url(), serverMd5 );
				}
				return dsCache;
			}

			public class Parameter implements Serializable {
				private static final long serialVersionUID = 987612341099L;
				private String name;
				private String value;
				
				public String getName() {
					return name;
				}
				public String getValue() {
					return value;
				}
			}
		}
		
		public class Evaluation_measures implements Serializable {
			private static final long serialVersionUID = 987612341049L;
			private String[] evaluation_measure;

			public String[] getEvaluation_measure() {
				return evaluation_measure;
			}
		}
	}
	
	public class Output implements Serializable {
		private static final long serialVersionUID = 987612341059L;
		private String name;
		private Predictions predictions;
		
		public String getName() {
			return name;
		}

		public Predictions getPredictions() {
			return predictions;
		}
		
		public class Predictions implements Serializable {
			private static final long serialVersionUID = 987612341069L;
			private String format;
			private Feature[] features;
			
			public String getFormat() {
				return format;
			}

			public Feature[] getFeatures() {
				return features;
			}

			public class Feature implements Serializable {
				private static final long serialVersionUID = 987612341079L;
				private String name;
				private String type;
				
				public String getName() {
					return name;
				}
				public String getType() {
					return type;
				}
			}
		}
	}
}
