ALTER TABLE dataset
	DROP `classIndex`,
	ADD COLUMN `uploader` int(10) NOT NULL DEFAULT -1 AFTER `did`,
	CHANGE COLUMN `name` `name` VARCHAR(128) NOT NULL AFTER `source`,
	CHANGE COLUMN `version` `version` VARCHAR(64) NOT NULL AFTER `name`,
	CHANGE COLUMN `description` `description` TEXT NOT NULL AFTER `version`,
	CHANGE COLUMN `format` `format` VARCHAR(64) NOT NULL DEFAULT 'arff' AFTER `description`,
	ADD COLUMN `collection_date` VARCHAR(128) NULL DEFAULT NULL AFTER `contributor`,
	CHANGE COLUMN `date` `uploadDate` DATETIME NOT NULL AFTER `collection_date`,
	CHANGE COLUMN `licence` `licence` VARCHAR(64) NULL DEFAULT NULL AFTER `language`;
	
ALTER TABLE `implementation`
	ADD COLUMN `uploader` int(10) NOT NULL DEFAULT -1 AFTER `fullName`,
	CHANGE COLUMN `date` `upload_date` DATETIME NOT NULL,
	DROP `programmingLanguage`,
	DROP `operatingSystem`;
	
ALTER TABLE `bibliographical_reference`
	ADD COLUMN `citation` TEXT NOT NULL AFTER `implementation`,
	DROP `title`,
	DROP `authors`,
	DROP `year`,
	DROP `doi`;
	
#------------------- COMMUNITY DATABASE -------------------#

CREATE TABLE IF NOT EXISTS `api_session` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `author_id` int(10) NOT NULL,
  `creation_date` datetime NOT NULL,
  `hash` varchar(64) NOT NULL,
  `calls` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

# username = api_tester@openml.org; password = md5(R_Weka_Knime_RapidMiner); md5(password) = '532b66c0ce362509c26ad66ac4ed8cc4'
INSERT INTO `expdb_community`.`author` (`id`, `activated`, `deleted`, `name`, `email`, `password`, `country`, `affiliation`, `source`, `external_id`, `image`, `image_update`, `verification_hash`) 
VALUES (NULL, 'y', 'n', 'ApiTester', 'apiTester@openml.org', MD5('R_Weka_Knime_RapidMiner'), 'Various', 'Various Universities', '', '', '', '2013-01-01 00:00:00', NULL);