package org.openml.cortana;

import org.openml.cortana.AutoRun.Experiment.Table.*;

import com.thoughtworks.xstream.annotations.*;

@XStreamAlias("autorun")
public class AutoRun {
	
	@XStreamAsAttribute
	private int nr_experiments = 1;
	
	private Experiment experiment;
	
	public AutoRun(String targetName, String targetValue, String quality_measure, Integer search_depth, Integer minimum_coverage, Double maximum_time, String search_strategy, Boolean use_nominal_sets, Integer search_strategy_width, String numeric_operators, String numeric_strategy, int nr_bins, int nr_threads, String table_name, String source, Column[] column) {
		experiment = new Experiment(targetName, targetValue, quality_measure, search_depth, minimum_coverage, maximum_time, search_strategy, use_nominal_sets, search_strategy_width, numeric_operators, numeric_strategy, nr_bins, nr_threads, table_name, source, column);
	}
	
	public Experiment getExperiment() {
		return experiment;
	}
	
	public static class Experiment {
		@XStreamAsAttribute
		private int id = 0;
		
		private TargetConcept target_concept;
		private SearchParameters search_parameters;
		private Table table;
		
		public Experiment(String target_name, String target_value, String quality_measure, Integer search_depth, Integer minimum_coverage, Double maximum_time, String search_strategy, Boolean use_nominal_sets, Integer search_strategy_width, String numeric_operators, String numeric_strategy, int nr_bins, int nr_threads, String table_name, String source, Column[] column) {
			target_concept = new TargetConcept(1, "single nominal", target_name, target_value, "", "");
			search_parameters = new SearchParameters(quality_measure, 0.0, search_depth, minimum_coverage, 1.0, 0, maximum_time, search_strategy, use_nominal_sets, search_strategy_width, numeric_operators, numeric_strategy, nr_bins, nr_threads);
			table = new Table(table_name, source, column);
		}
		
		public TargetConcept getTargetConcept() {
			return target_concept;
		}
		
		public SearchParameters getSearchParameters() {
			return search_parameters;
		}
		
		public Table getTable() {
			return table;
		}
		
		public static class TargetConcept {
			@XStreamAlias("nr_target_attributes")
			private int nr_target_attributes;
			private String target_type;
			private String primary_target; 
			private String target_value; 
			private String secondary_target; 
			private String multi_targets;
			
			public TargetConcept(int nr_target_attributes, String target_type,
					String primary_target, String target_value,
					String secondary_target, String multi_targets) {
				super();
				this.nr_target_attributes = nr_target_attributes;
				this.target_type = target_type;
				this.primary_target = primary_target;
				this.target_value = target_value;
				this.secondary_target = secondary_target;
				this.multi_targets = multi_targets;
			}
			
			public int getNr_target_attributes() {
				return nr_target_attributes;
			}
			public String getTarget_type() {
				return target_type;
			}
			public String getPrimary_target() {
				return primary_target;
			}
			public String getTarget_value() {
				return target_value;
			}
			public String getSecondary_target() {
				return secondary_target;
			}
			public String getMulti_targets() {
				return multi_targets;
			}
		}
		
		public static class SearchParameters {
			private String quality_measure;
			private Double quality_measure_minimum;
			private Integer search_depth;
			private Integer minimum_coverage;
			private Double maximum_coverage_fraction;
			private Integer maximum_subgroups;
			private Double maximum_time;
			private String search_strategy;
			private Boolean use_nominal_sets;
			private Integer search_strategy_width;
			private String numeric_operators;
			private String numeric_strategy;
			private Integer nr_bins;
			private Integer nr_threads;
			private Double alpha = 0.5;
			private Double beta = 1.0;
			private Boolean post_processing_do_autorun = true;
			private Integer post_processing_count = 20;
			private String beam_seed = "";
			private Double overall_ranking_loss = 0.0;
			public SearchParameters(String quality_measure,
					Double quality_measure_minimum, Integer search_depth,
					Integer minimum_coverage, Double maximum_coverage_fraction,
					Integer maximum_subgroups, Double maximum_time,
					String search_strategy, Boolean use_nominal_sets,
					Integer search_strategy_width, String numeric_operators,
					String numeric_strategy, Integer nr_bins, Integer nr_threads) {
				super();
				this.quality_measure = quality_measure;
				this.quality_measure_minimum = quality_measure_minimum;
				this.search_depth = search_depth;
				this.minimum_coverage = minimum_coverage;
				this.maximum_coverage_fraction = maximum_coverage_fraction;
				this.maximum_subgroups = maximum_subgroups;
				this.maximum_time = maximum_time;
				this.search_strategy = search_strategy;
				this.use_nominal_sets = use_nominal_sets;
				this.search_strategy_width = search_strategy_width;
				this.numeric_operators = numeric_operators;
				this.numeric_strategy = numeric_strategy;
				this.nr_bins = nr_bins;
				this.nr_threads = nr_threads;
			}
			public String getQuality_measure() {
				return quality_measure;
			}
			public Double getQuality_measure_minimum() {
				return quality_measure_minimum;
			}
			public Integer getSearch_depth() {
				return search_depth;
			}
			public Integer getMinimum_coverage() {
				return minimum_coverage;
			}
			public Double getMaximum_coverage_fraction() {
				return maximum_coverage_fraction;
			}
			public Integer getMaximum_subgroups() {
				return maximum_subgroups;
			}
			public Double getMaximum_time() {
				return maximum_time;
			}
			public String getSearch_strategy() {
				return search_strategy;
			}
			public Boolean getUse_nominal_sets() {
				return use_nominal_sets;
			}
			public Integer getSearch_strategy_width() {
				return search_strategy_width;
			}
			public String getNumeric_operators() {
				return numeric_operators;
			}
			public String getNumeric_strategy() {
				return numeric_strategy;
			}
			public Integer getNr_bins() {
				return nr_bins;
			}
			public Integer getNr_threads() {
				return nr_threads;
			}
			public Double getAlpha() {
				return alpha;
			}
			public Double getBeta() {
				return beta;
			}
			public Boolean getPost_processing_do_autorun() {
				return post_processing_do_autorun;
			}
			public Integer getPost_processing_count() {
				return post_processing_count;
			}
			public String getBeam_seed() {
				return beam_seed;
			}
			public Double getOverall_ranking_loss() {
				return overall_ranking_loss;
			}
		}
		
		public static class Table {
			private String table_name;
			private String source;

			@XStreamImplicit
			private Column[] column;
			
			public Table(String table_name, String source, Column[] column) {
				this.table_name = table_name;
				this.source = source;
				this.column = column;
			}
			
			public String getTable_name() {
				return table_name;
			}
			public String getSource() {
				return source;
			}
			public Column[] getColumn() {
				return column;
			}

			@XStreamAlias("column")
			public static class Column {
				private String type;
				private String name;
				@XStreamAlias("short")
				private String shortname = "";
				private Integer index; 
		        private String missing_value; 
		        private Boolean enabled;
		        
				public Column(String type, String name, Integer index,
						String missing_value, Boolean enabled) {
					super();
					this.type = type;
					this.name = name;
					this.index = index;
					this.missing_value = missing_value;
					this.enabled = enabled;
				}
				
				public String getType() {
					return type;
				}
				public String getName() {
					return name;
				}
				public String getShortname() {
					return shortname;
				}
				public Integer getIndex() {
					return index;
				}
				public String getMissing_value() {
					return missing_value;
				}
				public Boolean getEnabled() {
					return enabled;
				}
			}
		}
	}
}
