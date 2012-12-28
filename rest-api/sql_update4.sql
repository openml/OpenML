DROP TABLE `task`;

CREATE TABLE IF NOT EXISTS `task` (
  `task_id` int(10) NOT NULL AUTO_INCREMENT,
  `task_type` varchar(255) NOT NULL DEFAULT 'prediction',
  `prediction_type` varchar(255) NOT NULL DEFAULT 'classification',
  `description` text,
  `method` varchar(255) NOT NULL DEFAULT 'cross-validation',
  `repeats` int(10) NOT NULL DEFAULT '1',
  `folds` int(10) NOT NULL DEFAULT '1',
  `dataset_description_id` int(10) NOT NULL,
  `target_feature` varchar(255) NOT NULL,
  PRIMARY KEY (`task_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;


INSERT INTO `task` (`task_id`, `task_type`, `prediction_type`, `description`, `method`, `repeats`, `folds`, `dataset_description_id`, `target_feature`) VALUES
(1, 'prediction', 'classification', 'crossvalidation on iris', 'cross-validation', 2, 10, 61, 'class');
