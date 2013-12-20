ALTER TABLE `dataset` ADD `default_target_attribute` VARCHAR( 128 ) NULL DEFAULT NULL AFTER `isOriginal`;

CREATE TABLE IF NOT EXISTS `task_type_io` (
  `ttid` int(10) NOT NULL,
  `name` varchar(64) NOT NULL,
  `io` enum('input','output') NOT NULL,
  `description` varchar(256) NOT NULL,
  `template` text NOT NULL,
  PRIMARY KEY (`ttid`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `task_type_io`
--

INSERT INTO `task_type_io` (`ttid`, `name`, `io`, `description`, `template`) VALUES
(1, 'estimation_procedure', 'input', 'The estimation procedure used to validate the generated models', '<oml:estimation_procedure>\r\n<oml:type>[INPUT:3]</oml:type>\r\n<oml:data_splits_url>[INPUT:4]</oml:data_splits_url>\r\n<oml:parameter name="number_repeats">[INPUT:5]</oml:parameter>\r\n<oml:parameter name="number_folds">[INPUT:6]</oml:parameter>\r\n<oml:parameter name="percentage">[INPUT:7]</oml:parameter>\r\n<oml:parameter name="stratified_sampling">[INPUT:8]</oml:parameter>\r\n</oml:estimation_procedure>'),
(1, 'evaluation_measures', 'input', 'The evaluation measures to optimize for, e.g., cpu time, accurancy', '<oml:evaluation_measures>\r\n<oml:evaluation_measure>[INPUT:9]</oml:evaluation_measure>\r\n</oml:evaluation_measures>'),
(1, 'predictions', 'output', 'The desired output format', '<oml:predictions>\r\n<oml:format>ARFF</oml:format>\r\n<oml:feature name="confidence.classname" type="numeric"/>\r\n<oml:feature name="fold" type="integer"/>\r\n<oml:feature name="prediction" type="string"/>\r\n<oml:feature name="repeat" type="integer"/>\r\n<oml:feature name="row_id" type="integer"/>\r\n</oml:predictions>'),
(1, 'source_data', 'input', 'The dataset and target feature of a task', '<oml:data_set>\r\n<oml:data_set_id>[INPUT:1]</oml:data_set_id>\r\n<oml:target_feature>[INPUT:2]</oml:target_feature>\r\n</oml:data_set>'),
(2, 'estimation_procedure', 'input', 'The estimation procedure used to validate the generated models', '<oml:estimation_procedure>\r\n<oml:type>[INPUT:3]</oml:type>\r\n<oml:data_splits_url>[INPUT:4]</oml:data_splits_url>\r\n<oml:parameter name="number_repeats">[INPUT:5]</oml:parameter>\r\n<oml:parameter name="number_folds">[INPUT:6]</oml:parameter>\r\n<oml:parameter name="percentage">[INPUT:7]</oml:parameter>\r\n<oml:parameter name="stratified_sampling">[INPUT:8]</oml:parameter>\r\n</oml:estimation_procedure>'),
(2, 'evaluation_measures', 'input', 'The evaluation measures to optimize for, e.g., cpu time, accurancy', '<oml:evaluation_measures>\r\n<oml:evaluation_measure>[INPUT:9]</oml:evaluation_measure>\r\n</oml:evaluation_measures>'),
(2, 'predictions', 'output', 'The desired output format', '<oml:predictions>\r\n<oml:format>ARFF</oml:format>\r\n<oml:feature name="fold" type="integer"/>\r\n<oml:feature name="prediction" type="string"/>\r\n<oml:feature name="repeat" type="integer"/>\r\n<oml:feature name="row_id" type="integer"/>\r\n</oml:predictions>'),
(2, 'source_data', 'input', 'The dataset and target feature of a task', '<oml:data_set>\r\n<oml:data_set_id>[INPUT:1]</oml:data_set_id>\r\n<oml:target_feature>[INPUT:2]</oml:target_feature>\r\n</oml:data_set>');


DROP TABLE `task_type_data_set`, `task_type_estimation_procedure`, `task_type_estimation_procedure_parameter`, `task_type_evaluation_measures`, `task_type_parameter`, `task_type_prediction`, `task_type_prediction_feature`;

ALTER TABLE `task_values` CHANGE `value` `value` TEXT CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL;


CREATE TABLE IF NOT EXISTS `estimation_procedure` (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `ttid` int(11) NOT NULL,
  `name` varchar(128) NOT NULL,
  `type` enum('crossvalidation','leaveoneout','holdout','bootstrapping','subsampling') NOT NULL,
  `repeats` int(8) DEFAULT NULL,
  `folds` int(8) DEFAULT NULL,
  `percentage` int(8) DEFAULT NULL,
  `stratified_sampling` enum('true','false') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=7 ;

--
-- Dumping data for table `estimation_procedure`
--

INSERT INTO `estimation_procedure` (`id`, `ttid`, `name`, `type`, `repeats`, `folds`, `percentage`, `stratified_sampling`) VALUES
(1, 1, '10-fold Crossvalidation', 'crossvalidation', 1, 10, NULL, 'true'),
(2, 1, '5 times 2-fold Crossvalidation', 'crossvalidation', 5, 2, NULL, 'true'),
(3, 1, '10 times 10-fold Crossvalidation', 'crossvalidation', 10, 10, NULL, 'true'),
(4, 1, 'Leave one out', 'leaveoneout', 1, NULL, NULL, 'false'),
(5, 1, '10% Holdout set', 'holdout', 1, NULL, 33, 'true'),
(6, 1, '33% Holdout set', 'holdout', 1, NULL, 33, 'true'),
(7, 2, '10-fold Crossvalidation', 'crossvalidation', 1, 10, NULL, 'false'),
(8, 2, '5 times 2-fold Crossvalidation', 'crossvalidation', 5, 2, NULL, 'false'),
(9, 2, '10 times 10-fold Crossvalidation', 'crossvalidation', 10, 10, NULL, 'false'),
(10, 2, 'Leave one out', 'leaveoneout', 1, NULL, NULL, 'false'),
(11, 2, '10% Holdout set', 'holdout', 1, NULL, 33, 'false'),
(12, 2, '33% Holdout set', 'holdout', 1, NULL, 33, 'false');

CREATE TABLE IF NOT EXISTS `task_type_function` (
  `ttid` int(10) NOT NULL,
  `math_function` varchar(64) NOT NULL,
  PRIMARY KEY (`ttid`,`math_function`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `task_type_function`
--

INSERT INTO `task_type_function` (`ttid`, `math_function`) VALUES
(1, 'kappa'),
(1, 'kb_relative_information_score'),
(1, 'mean_absolute_error'),
(1, 'mean_prior_absolute_error'),
(1, 'predictive_accuracy'),
(1, 'prior_entropy'),
(1, 'relative_absolute_error'),
(1, 'root_mean_prior_squared_error'),
(1, 'root_mean_squared_error'),
(1, 'root_relative_squared_error'),
(2, 'mean_absolute_error'),
(2, 'mean_prior_absolute_error'),
(2, 'relative_absolute_error'),
(2, 'root_mean_prior_squared_error'),
(2, 'root_mean_squared_error'),
(2, 'root_relative_squared_error');
