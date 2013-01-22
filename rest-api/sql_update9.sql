DROP TABLE `task_set`;
DROP TABLE `set`;

CREATE TABLE IF NOT EXISTS `task_type` (
  `ttid` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `author` varchar(128) NOT NULL,
  `contributors` text,
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

CREATE TABLE IF NOT EXISTS `task_type_output` (
  `ttid` int(10) NOT NULL,
  `name` varchar(128) NOT NULL,
  `description` text NOT NULL,
  `data_type` varchar(128) NOT NULL,
  `format` varchar(128) NOT NULL,
  `should_upload` enum('true','false') NOT NULL,
  PRIMARY KEY (`ttid`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;