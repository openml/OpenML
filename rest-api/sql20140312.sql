ALTER TABLE `dataset` CHANGE `processed` `processed` DATETIME NULL DEFAULT NULL;
ALTER TABLE `output_data` ADD `field` VARCHAR( 128 ) NULL DEFAULT NULL;
ALTER TABLE `run` ADD `processed` DATETIME NULL DEFAULT NULL AFTER `error`;

ALTER TABLE `algorithm_setup` ADD `setup_string` VARCHAR( 256 ) NULL DEFAULT NULL;

CREATE TABLE `runfile` (
  `did` int(16) NOT NULL,
  `source` int(16) NOT NULL,
  `field` varchar(128) NOT NULL,
  `name` varchar(128) NOT NULL,
  `format` varchar(128) NOT NULL,
  `file_id` int(16) NOT NULL,
  PRIMARY KEY (`did`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `schedule` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `sid` int(8) NOT NULL,
  `ttid` int(10) NOT NULL,
  `workbench` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 ;


-- INSERT INTO `schedule` (`id`, `learner`, `setup_id`, `workbench`, `ttid`) VALUES
-- (1, 12, 'moa', 4),
-- (2, 11, 'moa', 4),
-- (3, 13, 'moa', 4),
-- (4,  6, 'moa', 4),
-- (5,  9, 'moa', 4),
-- (6,  7, 'moa', 4),
-- (7, 10, 'moa', 4);

ALTER TABLE `data_feature` CHANGE `NumberOfDistinctValues` `NumberOfDistinctValues` INT( 11 ) NULL DEFAULT NULL ,
CHANGE `NumberOfUniqueValues` `NumberOfUniqueValues` INT( 11 ) NULL DEFAULT NULL ,
CHANGE `NumberOfIntegerValues` `NumberOfIntegerValues` INT( 11 ) NULL DEFAULT NULL ,
CHANGE `NumberOfRealValues` `NumberOfRealValues` INT( 11 ) NULL DEFAULT NULL ;

ALTER TABLE `schedule` ADD `active` ENUM( 'true', 'false' ) NOT NULL DEFAULT 'true';