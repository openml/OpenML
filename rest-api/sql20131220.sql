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

DROP TABLE confussion_matrix;
ALTER TABLE `evaluation` ADD `array_data` TEXT NULL DEFAULT NULL ;
ALTER TABLE `evaluation_fold` ADD `array_data` TEXT NULL DEFAULT NULL ;
ALTER TABLE `evaluation` CHANGE `value` `value` DOUBLE NULL DEFAULT NULL ;