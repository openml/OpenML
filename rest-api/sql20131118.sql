 DROP TABLE `task_type_data_set`, `task_type_estimation_procedure`, `task_type_estimation_procedure_parameter`, `task_type_evaluation_measures`, `task_type_function`, `task_type_parameter`, `task_type_prediction`, `task_type_prediction_feature`;

ALTER TABLE `algorithm` CHANGE `class` `algorithm_class` VARCHAR( 32 ) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL;
ALTER TABLE `math_function` CHANGE `type` `functionType` VARCHAR( 128 ) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'EvaluationFunction';
ALTER TABLE `cvrun` CHANGE `type` `runType` VARCHAR( 32 ) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL;

ALTER TABLE math_function DROP INDEX `name`;
