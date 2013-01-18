# dataset
ALTER TABLE `dataset` CHANGE `author` `creator` VARCHAR( 128 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL;
ALTER TABLE `dataset` ADD `date` DATE NOT NULL AFTER `licence`;
ALTER TABLE `dataset` CHANGE `row_id_attribute` `row_id_attribute` VARCHAR( 128 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL ,
CHANGE `md5_checksum` `md5_checksum` VARCHAR( 128 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL;
ALTER TABLE `dataset` ADD `language` VARCHAR( 128 ) NULL DEFAULT NULL AFTER `description`;
ALTER TABLE `dataset` ADD `contributor` TEXT NULL AFTER `creator`;
ALTER TABLE `dataset` CHANGE `licence` `licence` VARCHAR( 64 ) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL;

# implementation
ALTER TABLE `implementation` 
CHANGE `name` `name` VARCHAR(128) NOT NULL,
CHANGE `version` `version` VARCHAR(128) NOT NULL,
ADD `creator` VARCHAR( 128 ) NULL DEFAULT NULL AFTER `version` ,
ADD `contributor` TEXT NULL DEFAULT NULL AFTER `creator` ,
ADD `date` DATE NULL DEFAULT NULL AFTER `contributor` ,
ADD `language` VARCHAR( 128 ) NULL DEFAULT NULL AFTER `date` ,
ADD `format` VARCHAR( 64 ) NULL DEFAULT NULL AFTER `language` ,
CHANGE `licence` `licence` VARCHAR(64) NULL DEFAULT NULL AFTER `format`,
ADD `description` TEXT NULL DEFAULT NULL AFTER `licence`,
ADD `readme` TEXT NULL DEFAULT NULL AFTER `description`,
ADD `summary` TEXT NULL DEFAULT NULL AFTER `readme`,
ADD `fullDescription` TEXT NULL DEFAULT NULL AFTER `summary`,
ADD `technicalManual` TEXT NULL DEFAULT NULL AFTER `fullDescription`,
CHANGE `library` `dependency` VARCHAR(128) NULL DEFAULT NULL AFTER `technicalManual`,
CHANGE `programmingLanguage` `programmingLanguage` VARCHAR( 128 ) NULL DEFAULT NULL AFTER `dependency`,
CHANGE `operatingSystem` `operatingSystem` VARCHAR( 128 ) NULL DEFAULT NULL AFTER `programmingLanguage`,
CHANGE `implements` `implements` VARCHAR(128) NULL DEFAULT NULL AFTER `operatingSystem`,
CHANGE `type` `type` VARCHAR(64) NOT NULL DEFAULT "workflow" AFTER `implements`,
CHANGE `url` `binaryUrl` VARCHAR(256) NULL DEFAULT NULL  AFTER `type`,
ADD `binaryMd5` VARCHAR(64) NULL DEFAULT NULL AFTER `binaryUrl`,
CHANGE `sourceCodeUrl` `sourceCodeUrl` VARCHAR(256) NULL DEFAULT NULL AFTER `binaryMd5`,
ADD `sourceCodeMd5` VARCHAR(64) NULL DEFAULT NULL AFTER `sourceCodeUrl`,
DROP classPath,
DROP methodToCall;

CREATE TABLE IF NOT EXISTS `bibliographical_reference` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `implementation_id` int(10) NOT NULL,
  `title` varchar(128) NOT NULL,
  `url` varchar(128) NOT NULL,
  `authors` varchar(128) NOT NULL,
  `year` int(10) DEFAULT NULL,
  `doi` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `implementation_component` (
  `parent` varchar(128) NOT NULL,
  `child` varchar(128) NOT NULL,
  PRIMARY KEY (`parent`,`child`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;