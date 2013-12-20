ALTER TABLE `algorithm_quality` ADD `implementation_id` INT( 16 ) NOT NULL AFTER `implementation`;
ALTER TABLE `algorithm_setup` ADD `implementation_id` INT( 16 ) NOT NULL AFTER `implementation`;
ALTER TABLE `bibliographical_reference` ADD `implementation_id` INT( 16 ) NOT NULL AFTER `implementation`;
ALTER TABLE `data_quality` ADD `implementation_id` INT( 16 ) NOT NULL AFTER `implementation`;
ALTER TABLE `evaluation` ADD `implementation_id` INT( 16 ) NOT NULL AFTER `implementation`;
ALTER TABLE `evaluation_fold` ADD `implementation_id` INT( 16 ) NOT NULL AFTER `implementation`;
ALTER TABLE `input` ADD `implementation_id` INT( 16 ) NOT NULL AFTER `implementation`;

TRUNCATE TABLE `implementation_component`;
ALTER TABLE `implementation_component` CHANGE `parent` `parent` INT( 16 ) NOT NULL ,
CHANGE `child` `child` INT( 16 ) NOT NULL;

# after the script has been run: 

ALTER TABLE `algorithm_quality`
  DROP PRIMARY KEY,
   ADD PRIMARY KEY(
     `implementation_id`,
     `quality`,
     `label`);
ALTER TABLE `algorithm_quality` DROP `implementation`;

ALTER TABLE `algorithm_setup` DROP INDEX `implementation` , ADD INDEX `implementation_id` ( `implementation_id` );
ALTER TABLE `algorithm_setup` DROP `implementation`;

ALTER TABLE `bibliographical_reference` DROP `implementation`;

ALTER TABLE `data_quality` DROP `implementation`;

ALTER TABLE `evaluation_fold` DROP INDEX `impl` , ADD INDEX `impl` ( `implementation_id` );
ALTER TABLE `evaluation_fold` DROP `implementation`;

ALTER TABLE `input` DROP `implementation`;

ALTER TABLE `evaluation` DROP INDEX `implementation` , ADD INDEX `implementation_id` ( `implementation_id` );
ALTER TABLE `evaluation` DROP `implementation`;

#####
ALTER TABLE `implementation` ADD `binary_file_id` INT( 10 ) NULL ,
ADD `source_file_id` INT( 10 ) NULL ;

ALTER TABLE `implementation` DROP `binaryUrl` ,
DROP `binaryFormat` ,
DROP `binaryMd5` ,
DROP `sourceUrl` ,
DROP `sourceFormat` ,
DROP `sourceMd5` ;
