INSERT INTO `task_type` (`ttid`, `name`, `description`, `creator`, `contributors`, `date`) 
VALUES ('4', 'Supervised Data Stream Classification', 'Given a dataset with a nominal target, various data samples of increasing size are defined. A model is build for each individual data sample; from this a learning curve can be drawn.', '"Geoffrey Holmes","Bernhard Pfahringer","Jan van Rijn","Joaquin Vanschoren"', NULL, '2014-03-01');

ALTER TABLE `estimation_procedure` CHANGE  `type`  `type` ENUM(  'crossvalidation',  'leaveoneout',  'holdout',  'bootstrapping',  'subsampling',  'learningcurve',  'testthentrain' ) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL ;
ALTER TABLE `estimation_procedure` CHANGE `stratified_sampling` `stratified_sampling` ENUM( 'true', 'false' ) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL ;
INSERT INTO `estimation_procedure` (
 `id` ,`ttid` ,`name`,`type`,`repeats`,`folds`,`percentage`,`stratified_sampling`
) VALUES ( 
 NULL ,  '4',  'Interleaved Test then Train',  'testthentrain', NULL , NULL , NULL ,  NULL 
);


INSERT INTO `task_type_io` (`ttid`, `name`, `io`, `description`, `template`) VALUES
(4, 'estimation_procedure', 'input', 'The estimation procedure used to validate the generated models', '<oml:estimation_procedure>\r\n<oml:type>[INPUT:3]</oml:type></oml:estimation_procedure>'),
(4, 'evaluation_measures', 'input', 'The evaluation measures to optimize for, e.g., cpu time, accurancy', '<oml:evaluation_measures>\r\n<oml:evaluation_measure>[INPUT:4]</oml:evaluation_measure>\r\n</oml:evaluation_measures>'),
(4, 'predictions', 'output', 'The desired output format', '<oml:predictions>\r\n<oml:format>ARFF</oml:format>\r\n<oml:feature name="repeat" type="integer"/>\r\n<oml:feature name="fold" type="integer"/>\r\n<oml:feature name="row_id" type="integer"/>\r\n<oml:feature name="confidence.classname" type="numeric"/>\r\n<oml:feature name="prediction" type="string"/>\r\n</oml:predictions>'),
(4, 'source_data', 'input', 'The dataset and target feature of a task', '<oml:data_set>\r\n<oml:data_set_id>[INPUT:1]</oml:data_set_id>\r\n<oml:target_feature>[INPUT:2]</oml:target_feature>\r\n</oml:data_set>');

INSERT INTO `task_type_function` (`ttid`, `math_function`) VALUES
(4, 'kappa'),
(4, 'kb_relative_information_score'),
(4, 'mean_absolute_error'),
(4, 'mean_prior_absolute_error'),
(4, 'predictive_accuracy'),
(4, 'prior_entropy'),
(4, 'relative_absolute_error'),
(4, 'root_mean_prior_squared_error'),
(4, 'root_mean_squared_error'),
(4, 'root_relative_squared_error');