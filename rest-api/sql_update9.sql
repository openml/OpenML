DROP TABLE `task_set`;
DROP TABLE `set`;
DROP TABLE `task`;

CREATE TABLE IF NOT EXISTS `task_type` (
  `ttid` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `description` TEXT NOT NULL,
  `author` varchar(128) NOT NULL,
  `contributors` text NULL DEFAULT NULL,
  `date` date NOT NULL,
  PRIMARY KEY (`ttid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `task_type_input` (
  `ttid` int(10) NOT NULL,
  `name` varchar(128) NOT NULL,
  `description` text NOT NULL,
  `data_type` varchar(128) NOT NULL,
  `format` varchar(128) NOT NULL,
  PRIMARY KEY (`ttid`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `task_type_parameter` (
  `ttid` int(10) NOT NULL,
  `name` varchar(128) NOT NULL,
  `description` text NOT NULL,
  `data_type` varchar(128) NOT NULL,
  `allowed_values` text NULL DEFAULT NULL,
  `required` enum('true','false') NOT NULL,
  `dependency` text NULL DEFAULT NULL,
  PRIMARY KEY (`ttid`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `task_type_output` (
  `ttid` int(10) NOT NULL,
  `name` varchar(128) NOT NULL,
  `description` text NOT NULL,
  `data_type` varchar(128) NOT NULL,
  `format` varchar(128) NOT NULL,
  `should_upload` enum('true','false') NOT NULL,
  PRIMARY KEY (`ttid`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS`task` (
  `tid` int(10) NOT NULL AUTO_INCREMENT,
  `ttid` int(10) NOT NULL,
  PRIMARY KEY (`tid`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=latin1; 


CREATE TABLE IF NOT EXISTS `estimation_procedure` (
  `epid` int(11) NOT NULL AUTO_INCREMENT,
  `did` int(11) NOT NULL,
  `type` varchar(128) NOT NULL,
  `url` varchar(128) NOT NULL,
  PRIMARY KEY (`epid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;


CREATE TABLE IF NOT EXISTS `estimation_procedure_parameter` (
  `epid` int(10) NOT NULL,
  `name` varchar(128) NOT NULL,
  `value` varchar(128) NOT NULL,
  PRIMARY KEY (`epid`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE IF NOT EXISTS `prediction` (
  `pid` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `format` varchar(128) NOT NULL,
  PRIMARY KEY (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;


CREATE TABLE IF NOT EXISTS `prediction_feature` (
  `pid` int(10) NOT NULL,
  `name` varchar(128) NOT NULL,
  `type` varchar(128) NOT NULL,
  PRIMARY KEY (`pid`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE IF NOT EXISTS `task_dataset` (
  `task_id` int(10) NOT NULL,
  `did` int(10) NOT NULL,
  `target_features` TEXT NOT NULL,
  PRIMARY KEY (`task_id`,`did`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE IF NOT EXISTS `task_estimation_procedure` (
  `task_id` int(10) NOT NULL,
  `epid` int(10) NOT NULL,
  PRIMARY KEY (`task_id`,`epid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE IF NOT EXISTS `task_prediction` (
  `task_id` int(10) NOT NULL,
  `pid` int(10) NOT NULL,
  PRIMARY KEY (`task_id`,`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
