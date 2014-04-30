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

ALTER TABLE `quality` ADD `showonline` ENUM( 'true', 'false' ) NOT NULL DEFAULT 'true';


CREATE TABLE IF NOT EXISTS `evaluation_interval` (
  `did` int(10) unsigned NOT NULL DEFAULT '0',
  `source` int(10) unsigned NOT NULL,
  `parent` int(10) unsigned NOT NULL DEFAULT '0',
  `implementation_id` int(16) NOT NULL,
  `function` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `label` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `interval_start` int(10) unsigned NOT NULL DEFAULT '0',
  `interval_end` int(10) unsigned NOT NULL DEFAULT '0',
  `value` double DEFAULT NULL,
  `array_data` text COLLATE utf8_unicode_ci,
  UNIQUE KEY `did` (`did`,`function`,`label`),
  KEY `function` (`function`,`source`,`label`(32)),
  KEY `func` (`function`),
  KEY `impl` (`implementation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `data_quality_interval` (
  `data` int(10) unsigned NOT NULL DEFAULT '0',
  `quality` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `implementation_id` int(16) NOT NULL,
  `interval_start` int(16) NOT NULL,
  `interval_end` int(16) NOT NULL,
  `value` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`data`,`quality`,`interval_start`,`interval_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;