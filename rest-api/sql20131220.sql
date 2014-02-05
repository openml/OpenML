INSERT INTO `quality` (
`name` ,
`type` ,
`formula` ,
`description` ,
`min` ,
`max` ,
`unit`
)
VALUES (
'MajorityClassSize', 'DataQuality', NULL , 'The number of instances that have the majority (most occurring) class', '0', 'n', 'instances'
), (
'MinorityClassSize', 'DataQuality', NULL , 'The number of instances that have the minority (least occurring) class', '0', 'n', 'instances'
);

ALTER TABLE `dataset` CHANGE `processed` `processed` DATETIME NULL DEFAULT NULL ;
UPDATE dataset SET processed = NULL, error = "false";

ALTER TABLE `implementation_component` ADD `identifier` VARCHAR( 16 ) NULL DEFAULT NULL ;
ALTER TABLE `estimation_procedure` CHANGE `type` `type` ENUM( 'crossvalidation', 'leaveoneout', 'holdout', 'bootstrapping', 'subsampling', 'learningcurve' );

DROP TABLE confusion_matrix;
ALTER TABLE `evaluation` ADD `array_data` TEXT NULL DEFAULT NULL ;
ALTER TABLE `evaluation_fold` ADD `array_data` TEXT NULL DEFAULT NULL ;
ALTER TABLE `evaluation` CHANGE `value` `value` DOUBLE NULL DEFAULT NULL ;
ALTER TABLE `evaluation_fold` CHANGE `value` `value` DOUBLE NULL DEFAULT NULL;

CREATE TABLE IF NOT EXISTS `evaluation_sample` (
  `did` int(10) unsigned NOT NULL DEFAULT '0',
  `source` int(10) unsigned NOT NULL,
  `parent` int(10) unsigned NOT NULL DEFAULT '0',
  `implementation_id` int(16) NOT NULL,
  `function` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `label` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `repeat` int(10) unsigned NOT NULL DEFAULT '0',
  `fold` int(10) unsigned NOT NULL DEFAULT '0',
  `sample` int(10) unsigned NULL DEFAULT NULL,
  `sample_size` INT( 10 ) NOT NULL,
  `value` double NOT NULL,
  `array_data` text COLLATE utf8_unicode_ci,
  UNIQUE KEY `did` (`did`,`function`,`label`),
  KEY `function` (`function`,`source`,`label`(32)),
  KEY `func` (`function`),
  KEY `impl` (`implementation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

 ALTER TABLE `input`
  DROP `generalName`,
  DROP `suggestedDistribution`,
  DROP `lowThreshold`,
  DROP `highThreshold`,
  DROP `min`,
  DROP `max`,
  DROP `setsComponent`;
ALTER TABLE `input` CHANGE `suggestedValues` `recommendedRange` VARCHAR( 256 ) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL;
ALTER TABLE `implementation` ADD `external_version` VARCHAR( 128 ) NOT NULL AFTER `version`;
