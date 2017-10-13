--
-- Table structure for table `evaluation_engine`
--

CREATE TABLE IF NOT EXISTS `evaluation_engine` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  `description` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

INSERT INTO `evaluation_engine` (`id`, `name`, `description`) VALUES
(1, 'weka_engine', 'Default OpenML evaluation engine');

--
-- creates new table for book keeping
--
--
-- Table structure for table `run_evaluated`
--

CREATE TABLE IF NOT EXISTS `run_evaluated` (
  `run_id` int(10) unsigned NOT NULL,
  `evaluation_engine_id` int(16) NOT NULL,
  `user_id` int(16) NOT NULL,
  `evaluation_date` datetime NOT NULL,
  `error` text,
  `warning` text,
  PRIMARY KEY (`run_id`,`evaluation_engine_id`),
  KEY `evaluation_engine_id` (`evaluation_engine_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- fills it with relevant information
INSERT INTO run_evaluated(`run_id`,`evaluation_engine_id`,`user_id`,`evaluation_date`,`error`, `warning`) SELECT `rid` AS `run_id`, 1 AS `evaluation_engine_id`, 1 AS `user_id`, `processed` AS `evaluation_date`, `error`, `warning` FROM `run` WHERE `evaluation_date` IS NOT NULL;

-- deletes the obsolute fields
ALTER TABLE `run`
  DROP `parent`,
  DROP `status`,
  DROP `priority`,
  DROP `error`,
  DROP `warning`,
  DROP `processed`,
  DROP `experiment`,
  DROP `runner`;

-- delete unused fields
ALTER TABLE `evaluation`
--  DROP `parent`,
  DROP `did`,
  DROP `implementation_id`;

ALTER TABLE `evaluation_fold`
  DROP `did`,
  DROP `parent`,
  DROP `implementation_id`;

ALTER TABLE `evaluation_sample`
  DROP `did`,
  DROP `parent`,
  DROP `implementation_id`;
  
-- remove did field from runfile
ALTER TABLE `runfile` ADD UNIQUE (`did`); 

ALTER TABLE `runfile`
  DROP PRIMARY KEY,
   ADD PRIMARY KEY(`source`, `field`);

ALTER TABLE `runfile` DROP `did`;

-- delete unused tables
DROP TABLE `evaluation_interval`;
DROP TABLE `query_graphs`;
DROP TABLE `queries`;
DROP TABLE `bibliographicsl_reference`;

-- add additional column to evaluation tables
ALTER TABLE `evaluation` ADD `evaluation_engine_id` INT( 16 ) NOT NULL AFTER `function` ;
ALTER TABLE `evaluation`
  DROP PRIMARY KEY,
   ADD PRIMARY KEY(
     `source`,
     `function`,
     `evaluation_engine_id`);

ALTER TABLE `evaluation_fold` ADD `evaluation_engine_id` INT( 16 ) NOT NULL AFTER `function` ;
ALTER TABLE `evaluation_fold`
  DROP PRIMARY KEY,
   ADD PRIMARY KEY(
     `source`,
     `function`,
     `evaluation_engine_id`,
     `repeat`,
     `fold`);

ALTER TABLE `evaluation_sample` ADD `evaluation_engine_id` INT( 16 ) NOT NULL AFTER `function` ;
ALTER TABLE `evaluation_sample`
  DROP PRIMARY KEY,
   ADD PRIMARY KEY(
     `source`,
     `function`,
     `evaluation_engine_id`,
     `repeat`,
     `fold`,
     `sample`);

ALTER TABLE `runfile` CHANGE `source` `source` INT( 10 ) UNSIGNED NOT NULL 
ALTER TABLE `runfile` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci; -- maybe??
ALTER TABLE `runfile` 
  ADD FOREIGN KEY (`source`) REFERENCES `run` (`rid`) ON DELETE CASCADE ON UPDATE RESTRICT;
  
ALTER TABLE `run_evaluated`
  ADD CONSTRAINT `run_evaluated_ibfk_2` FOREIGN KEY (`evaluation_engine_id`) REFERENCES `evaluation_engine` (`id`),
  ADD CONSTRAINT `run_evaluated_ibfk_1` FOREIGN KEY (`run_id`) REFERENCES `run` (`rid`) ON DELETE CASCADE;

ALTER TABLE `evaluation` 
  ADD FOREIGN KEY (`source`) REFERENCES `run` (`rid`) ON DELETE CASCADE ON UPDATE RESTRICT,
  ADD FOREIGN KEY (`function`) REFERENCES `math_function` (`name`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD FOREIGN KEY (`evaluation_engine_id`) REFERENCES `evaluation_engine` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;
  
  
ALTER TABLE `evaluation_fold` 
  ADD FOREIGN KEY (`source`) REFERENCES `run` (`rid`) ON DELETE CASCADE ON UPDATE RESTRICT,
  ADD FOREIGN KEY (`function`) REFERENCES `math_function` (`name`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD FOREIGN KEY (`evaluation_engine_id`) REFERENCES `evaluation_engine` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;
  

ALTER TABLE `evaluation_sample` 
  ADD FOREIGN KEY (`source`) REFERENCES `run` (`rid`) ON DELETE CASCADE ON UPDATE RESTRICT,
  ADD FOREIGN KEY (`function`) REFERENCES `math_function` (`name`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD FOREIGN KEY (`evaluation_engine_id`) REFERENCES `evaluation_engine` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

