ALTER TABLE `dataset` 
	ADD `processed` ENUM( 'true', 'false' ) NOT NULL DEFAULT 'false',
	ADD `error` ENUM( 'true', 'false' ) NOT NULL DEFAULT 'false';