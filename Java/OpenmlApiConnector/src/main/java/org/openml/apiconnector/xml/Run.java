/*
 *  OpenmlApiConnector - Java integration of the OpenML Web API
 *  Copyright (C) 2014 
 *  @author Jan N. van Rijn (j.n.van.rijn@liacs.leidenuniv.nl)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */
package org.openml.apiconnector.xml;

import org.openml.apiconnector.settings.Constants;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class Run {

	private final String oml = Constants.OPENML_XMLNS;
	private int task_id;
	private int implementation_id;
	private String error_message;
	private String setup_string;
	private Parameter_setting[] parameter_settings;
	private Data input_data;
	private Data output_data;
	
	public Run( int task_id, String error_message, int implementation_id, String setup_string, Parameter_setting[] parameter_settings ) {
		this.task_id = task_id;
		this.implementation_id = implementation_id;
		this.error_message = error_message;
		this.setup_string = setup_string;
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
	
	public String getSetup_string() {
		return setup_string;
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
	
	public void addOutputEvaluation( String name, Integer repeat, Integer fold, 
			Integer sample, String implementation, Double value ) {
		output_data.addEvaluation(name, repeat, fold, sample, implementation, value);
	}
	
	public void addOutputEvaluation( String name, String implementation,
			Double value, String[] array_data ) {
		output_data.addEvaluation( name, implementation, value, array_data);
	}
	
	public void addOutputEvaluation( String name, String implementation,
			Double value, String array_data ) {
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
		
		public void addEvaluation( String name, Integer repeat, Integer fold, 
					Integer sample, String implementation, Double value ) {
			Evaluation e = new Evaluation(name, repeat, fold, sample, implementation, value);
			evaluation = ArrayUtils.addAll( evaluation, e );
		}
		
		public void addEvaluation( String name, String implementation,
				Double value, String[] array_data ) {
			Evaluation e = new Evaluation( name, implementation, value, array_data);
			evaluation = ArrayUtils.addAll( evaluation, e );
		}
		
		public void addEvaluation( String name, String implementation,
				Double value, String array_data ) {
			Evaluation e = new Evaluation( name, implementation, value, array_data);
			evaluation = ArrayUtils.addAll( evaluation, e );
		}

		public static class Dataset {
			private Integer did;
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
			private Integer did;
			private Integer repeat;
			private Integer fold;
			private Integer sample;
			private String name;
			private String implementation;
			private Double value;
			private String array_data;
			
			public Evaluation( String name, Integer repeat, Integer fold, 
					Integer sample, String implementation, Double value ) {
				super();
				this.name = name;
				this.implementation = implementation;
				this.value = value;
				this.repeat = repeat;
				this.fold = fold;
				this.sample = sample;
			}
			
			public Evaluation( String name, String implementation,
					Double value, String[] array_data) {
				super();
				this.name = name;
				this.implementation = implementation;
				this.value = value;
				this.array_data = "[ " + StringUtils.join( array_data, ", " ) + " ]";
			}
			
			public Evaluation( String name, String implementation,
					Double value, String array_data ) {
				super();
				this.name = name;
				this.implementation = implementation;
				this.value = value;
				this.array_data = array_data;
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

			public Integer getRepeat() {
				return repeat;
			}

			public Integer getFold() {
				return fold;
			}

			public Integer getSample() {
				return sample;
			}
		}
	}
}
