# DATA PROCESSED

CREATE TABLE `data_processed` (
`did` INT( 10 ) UNSIGNED NOT NULL ,
`evaluation_engine_id` INT( 16 ) NOT NULL ,
`user_id` INT( 16 ) NOT NULL ,
`processing_date` DATETIME NOT NULL ,
`error` TEXT DEFAULT NULL ,
`warning` TEXT DEFAULT NULL ,
PRIMARY KEY (`did`, `evaluation_engine_id`)
) ENGINE = InnoDB;
ALTER TABLE `data_processed` ADD FOREIGN KEY (`did`) REFERENCES `openml_expdb`.`dataset` (`did`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `data_processed` ADD FOREIGN KEY (`evaluation_engine_id`) REFERENCES `openml_expdb`.`evaluation_engine` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

# FILL DATA PROCESSED

INSERT INTO data_processed (`did`,`evaluation_engine_id`,`user_id`,`processing_date`,`error`) SELECT `did`, "1", "1", `processed`, `error_message` FROM `dataset`;

# DATA_FEATURE
ALTER TABLE `data_feature` CHANGE `did` `did` INT( 10 ) UNSIGNED NOT NULL;
ALTER TABLE `data_feature` CHANGE `index` `index` INT( 10 ) UNSIGNED NOT NULL DEFAULT '0';

ALTER TABLE `data_feature` ADD `evaluation_engine_id` INT( 16 ) NOT NULL AFTER `index` ;
ALTER TABLE `data_feature` ADD INDEX ( `evaluation_engine_id` ) ;
UPDATE `data_feature` SET `evaluation_engine_id` = 1;

ALTER TABLE `data_feature` ADD FOREIGN KEY ( `did` ) REFERENCES `openml_expdb`.`dataset` (`did`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `data_feature` ADD FOREIGN KEY ( `evaluation_engine_id` ) REFERENCES `openml_expdb`.`evaluation_engine` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE `data_feature` ADD FOREIGN KEY (`did`, `evaluation_engine_id`) REFERENCES `openml_expdb`.`data_processed` (`did`, `evaluation_engine_id`) ON DELETE CASCADE ON UPDATE CASCADE;


# DATA_QUALITY
ALTER TABLE `data_quality` CHANGE `implementation_id` `evaluation_engine_id` INT(16) NOT NULL;
UPDATE data_quality SET evaluation_engine_id = 1;

ALTER TABLE `data_quality` DROP `label`;
ALTER TABLE `data_quality`
  DROP PRIMARY KEY,
   ADD PRIMARY KEY(
     `data`,
     `quality`,
     `evaluation_engine_id`);
ALTER TABLE `data_quality` ADD FOREIGN KEY (`data`) REFERENCES `openml_expdb`.`dataset` (`did`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `data_quality` ADD FOREIGN KEY (`evaluation_engine_id`) REFERENCES `openml_expdb`.`evaluation_engine` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE `data_quality` ADD FOREIGN KEY (`evaluation_engine_id`) REFERENCES `openml_expdb`.`evaluation_engine` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE `data_quality` ADD FOREIGN KEY (`data`, `evaluation_engine_id`) REFERENCES `openml_expdb`.`data_processed` (`did`, `evaluation_engine_id`) ON DELETE CASCADE ON UPDATE CASCADE;

# DATA_QUALITY_INTERVAL
ALTER TABLE `data_quality_interval` CHANGE `implementation_id` `evaluation_engine_id` INT(16) NOT NULL;
UPDATE data_quality_interval SET evaluation_engine_id = 1;
ALTER TABLE `data_quality_interval`
  DROP PRIMARY KEY,
   ADD PRIMARY KEY(
     `data`,
     `quality`,
     `evaluation_engine_id`,
     `interval_start`,
     `interval_end`);
ALTER TABLE `data_quality_interval` ADD FOREIGN KEY (`data`) REFERENCES `openml_expdb`.`dataset` (`did`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `data_quality_interval` ADD FOREIGN KEY (`evaluation_engine_id`) REFERENCES `openml_expdb`.`evaluation_engine` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE `data_quality_interval` ADD FOREIGN KEY (`quality`) REFERENCES `openml_expdb`.`quality` (`name`) ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE `data_quality_interval` ADD FOREIGN KEY (`data`, `evaluation_engine_id`) REFERENCES `openml_expdb`.`data_processed` (`did`, `evaluation_engine_id`) ON DELETE CASCADE ON UPDATE CASCADE;

# FEATURE_QUALITY

ALTER TABLE `feature_quality` ADD `evaluation_engine_id` INT( 16 ) NOT NULL AFTER `feature_index`;
UPDATE feature_quality SET evaluation_engine_id = 1;
 ALTER TABLE `feature_quality`
  DROP PRIMARY KEY,
   ADD PRIMARY KEY(
     `data`,
     `quality`,
     `feature_index`,
     `evaluation_engine_id`);
ALTER TABLE `feature_quality` ADD FOREIGN KEY (`data`) REFERENCES `openml_expdb`.`dataset` (`did`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `feature_quality` ADD FOREIGN KEY (`quality`) REFERENCES `openml_expdb`.`quality` (`name`) ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE `feature_quality` ADD FOREIGN KEY (`data`,`feature_index`) REFERENCES `openml_expdb`.`data_feature` (`did`,`index`) ON DELETE CASCADE ON UPDATE CASCADE; 
ALTER TABLE `feature_quality` ADD FOREIGN KEY (`evaluation_engine_id`) REFERENCES `openml_expdb`.`evaluation_engine` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE `feature_quality` ADD FOREIGN KEY (`data`, `evaluation_engine_id`) REFERENCES `openml_expdb`.`data_processed` (`did`, `evaluation_engine_id`) ON DELETE CASCADE ON UPDATE CASCADE;

# REMOVE OBSOLUTE FIELDS
ALTER TABLE `dataset`
  DROP `processed`,
  DROP `error`,
  DROP `error_message`;

