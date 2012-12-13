-- phpMyAdmin SQL Dump
-- version 3.5.1
-- http://www.phpmyadmin.net
--
-- Machine: localhost
-- Genereertijd: 13 dec 2012 om 17:39
-- Serverversie: 5.5.24-log
-- PHP-versie: 5.3.13

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Databank: `expdb`
--

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `algorithm`
--

CREATE TABLE IF NOT EXISTS `algorithm` (
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `function` tinytext CHARACTER SET latin1,
  `description` text CHARACTER SET latin1,
  `class` varchar(32) CHARACTER SET latin1 DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `algorithm_quality`
--

CREATE TABLE IF NOT EXISTS `algorithm_quality` (
  `implementation` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `quality` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `qualityImplementation` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `label` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `value` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`implementation`,`quality`,`label`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `algorithm_setup`
--

CREATE TABLE IF NOT EXISTS `algorithm_setup` (
  `sid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `parent` int(10) unsigned NOT NULL,
  `implementation` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `algorithm` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `role` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'Learner',
  `isDefault` enum('true','false') CHARACTER SET utf8 COLLATE utf8_bin DEFAULT 'false',
  PRIMARY KEY (`sid`),
  KEY `is_default` (`isDefault`),
  KEY `parent` (`parent`),
  KEY `algorithm` (`algorithm`),
  KEY `implementation` (`implementation`(255)),
  KEY `role` (`role`),
  KEY `role-parent` (`role`,`parent`),
  KEY `role-sid` (`role`,`sid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=635046 ;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `bvrun`
--

CREATE TABLE IF NOT EXISTS `bvrun` (
  `rid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `inputData` int(10) unsigned DEFAULT '0',
  `learner` int(10) unsigned DEFAULT '0',
  `BVSetup` int(10) unsigned DEFAULT '0',
  PRIMARY KEY (`rid`),
  KEY `learner` (`learner`),
  KEY `inputData` (`inputData`),
  KEY `BVSetup` (`BVSetup`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=468832 ;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `component`
--

CREATE TABLE IF NOT EXISTS `component` (
  `parent` bigint(20) unsigned NOT NULL,
  `child` bigint(20) unsigned NOT NULL,
  `role` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `canvasXY` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  `logRuns` enum('true','false') COLLATE utf8_unicode_ci NOT NULL DEFAULT 'false',
  PRIMARY KEY (`parent`,`child`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `connection`
--

CREATE TABLE IF NOT EXISTS `connection` (
  `workflow` bigint(20) unsigned NOT NULL,
  `source` bigint(20) unsigned NOT NULL,
  `sourcePort` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `target` bigint(20) unsigned NOT NULL,
  `targetPort` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `dataType` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'Weka.Instances',
  PRIMARY KEY (`workflow`,`source`,`target`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `cvrun`
--

CREATE TABLE IF NOT EXISTS `cvrun` (
  `rid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `inputData` int(10) unsigned DEFAULT '0',
  `learner` int(10) unsigned DEFAULT '0',
  `CVSetup` int(10) unsigned DEFAULT '0',
  `type` varchar(32) CHARACTER SET latin1 DEFAULT NULL,
  `nrFolds` int(11) NOT NULL DEFAULT '10',
  `nrIterations` int(11) NOT NULL DEFAULT '1',
  `leaveOneOut` enum('true','false') CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'false',
  PRIMARY KEY (`rid`),
  KEY `liid` (`learner`),
  KEY `diid` (`inputData`),
  KEY `emiid` (`CVSetup`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=716951 ;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `dataset`
--

CREATE TABLE IF NOT EXISTS `dataset` (
  `did` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `source` int(10) unsigned NOT NULL,
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `format` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'ARFF',
  `version` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `license` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'public domain',
  `description` text CHARACTER SET utf8 COLLATE utf8_bin,
  `collection` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `task` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'classification',
  `url` mediumtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `isOriginal` enum('true','false') CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `classIndex` smallint(6) DEFAULT '-1',
  PRIMARY KEY (`did`),
  KEY `name` (`name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=2238 ;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `dataset_description`
--

CREATE TABLE IF NOT EXISTS `dataset_description` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `url` varchar(255) NOT NULL,
  `author` varchar(255) NOT NULL,
  `licence` varchar(255) NOT NULL,
  `row_id_attribute` varchar(255) NOT NULL,
  `md5_checksum` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `data_class`
--

CREATE TABLE IF NOT EXISTS `data_class` (
  `did` int(10) unsigned NOT NULL DEFAULT '0',
  `classIndex` int(11) NOT NULL,
  `label` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `nrInstances` int(11) NOT NULL,
  `weight` double NOT NULL,
  PRIMARY KEY (`did`,`classIndex`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `data_feature`
--

CREATE TABLE IF NOT EXISTS `data_feature` (
  `did` int(10) NOT NULL,
  `index` int(10) NOT NULL DEFAULT '0',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `data_type` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `is_target` enum('true','false') CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `NumberOfDistinctValues` int(11) NOT NULL,
  `NumberOfUniqueValues` int(11) NOT NULL,
  `NumberOfMissingValues` int(11) NOT NULL,
  `NumberOfIntegerValues` int(11) NOT NULL,
  `NumberOfRealValues` int(11) NOT NULL,
  `NumberOfNominalValues` varchar(512) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `NumberOfValues` int(11) NOT NULL,
  `MaximumValue` int(11) NOT NULL,
  `MinimumValue` int(11) NOT NULL,
  `MeanValue` int(11) NOT NULL,
  `StandardDeviation` int(11) NOT NULL,
  PRIMARY KEY (`did`,`index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `data_quality`
--

CREATE TABLE IF NOT EXISTS `data_quality` (
  `data` int(10) unsigned NOT NULL DEFAULT '0',
  `quality` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `implementation` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `label` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `value` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`data`,`quality`,`label`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `evaluates_function`
--

CREATE TABLE IF NOT EXISTS `evaluates_function` (
  `implementation` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `function` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`implementation`,`function`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `evaluation`
--

CREATE TABLE IF NOT EXISTS `evaluation` (
  `did` int(10) unsigned NOT NULL DEFAULT '0',
  `source` int(10) unsigned NOT NULL,
  `implementation` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `function` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `label` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `value` double NOT NULL,
  `stdev` double DEFAULT NULL,
  UNIQUE KEY `did` (`did`,`function`,`label`),
  KEY `function` (`function`,`source`,`label`(32)),
  KEY `func` (`function`),
  KEY `impl` (`implementation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `evaluation_statistics`
--

CREATE TABLE IF NOT EXISTS `evaluation_statistics` (
  `data` int(10) unsigned NOT NULL,
  `function` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `label` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `best` double DEFAULT NULL,
  `worst` double DEFAULT NULL,
  `baseline` double DEFAULT NULL,
  PRIMARY KEY (`data`,`function`,`label`),
  KEY `best` (`best`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `experiment`
--

CREATE TABLE IF NOT EXISTS `experiment` (
  `eid` int(10) unsigned NOT NULL,
  `setup` int(10) unsigned NOT NULL,
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `experimentDesign` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `author` varchar(512) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `question` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `hypothesis` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `description` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`eid`),
  KEY `name` (`name`,`experimentDesign`,`author`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `experiment_variable`
--

CREATE TABLE IF NOT EXISTS `experiment_variable` (
  `vid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `experiment` int(10) unsigned NOT NULL,
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `type` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `setup` bigint(20) NOT NULL,
  `input` varchar(128) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`vid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=128 ;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `fold`
--

CREATE TABLE IF NOT EXISTS `fold` (
  `task_id` int(10) NOT NULL,
  `fold_id` int(10) NOT NULL,
  `repeat_id` int(10) NOT NULL,
  `set` enum('training','test') NOT NULL,
  `data` text NOT NULL,
  PRIMARY KEY (`task_id`,`fold_id`,`repeat_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `function_setup`
--

CREATE TABLE IF NOT EXISTS `function_setup` (
  `sid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `parent` int(10) unsigned NOT NULL,
  `implementation` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `function` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `isDefault` enum('true','false') CHARACTER SET utf8 COLLATE utf8_bin DEFAULT 'false',
  `role` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`sid`),
  KEY `implementation` (`implementation`),
  KEY `parent` (`parent`),
  KEY `role` (`role`),
  KEY `function` (`function`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=112525 ;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `implementation`
--

CREATE TABLE IF NOT EXISTS `implementation` (
  `fullName` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `version` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `implements` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `type` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'algorithm',
  `library` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `url` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'http://sourceforge.net/projects/weka/files/weka-3-4/3.4.8/weka-3-4-8a.zip/download',
  `sourceCodeUrl` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `licence` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `programmingLanguage` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `operatingSystem` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'universal',
  `classPath` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `methodToCall` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT 'main()',
  PRIMARY KEY (`fullName`),
  KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `input`
--

CREATE TABLE IF NOT EXISTS `input` (
  `fullName` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `implementation` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `generalName` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `description` text CHARACTER SET utf8 COLLATE utf8_bin,
  `dataType` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `suggestedDistribution` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `defaultValue` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `suggestedValues` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `lowThreshold` double DEFAULT NULL,
  `highThreshold` double DEFAULT NULL,
  `min` double DEFAULT NULL,
  `max` double DEFAULT NULL,
  `setsComponent` int(11) DEFAULT NULL,
  PRIMARY KEY (`fullName`),
  KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `input_data`
--

CREATE TABLE IF NOT EXISTS `input_data` (
  `run` int(11) NOT NULL,
  `data` int(11) NOT NULL,
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'inputdata',
  PRIMARY KEY (`run`,`data`),
  KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `input_setting`
--

CREATE TABLE IF NOT EXISTS `input_setting` (
  `setup` int(10) unsigned NOT NULL DEFAULT '0',
  `input` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `value` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`setup`,`input`),
  KEY `value` (`value`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `machine`
--

CREATE TABLE IF NOT EXISTS `machine` (
  `mid` varchar(32) CHARACTER SET latin1 NOT NULL,
  `corr_factor` double NOT NULL DEFAULT '1',
  PRIMARY KEY (`mid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `math_function`
--

CREATE TABLE IF NOT EXISTS `math_function` (
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `type` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'EvaluationFunction',
  `min` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `max` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `unit` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `higherIsBetter` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `description` text CHARACTER SET utf8 COLLATE utf8_bin,
  PRIMARY KEY (`name`),
  KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `model`
--

CREATE TABLE IF NOT EXISTS `model` (
  `did` int(10) unsigned NOT NULL DEFAULT '0',
  `format` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '1',
  `value` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`did`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci MAX_ROWS=2000000000 AVG_ROW_LENGTH=6000;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `output`
--

CREATE TABLE IF NOT EXISTS `output` (
  `fullName` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `implementation` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `generalName` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `description` text CHARACTER SET utf8 COLLATE utf8_bin,
  `dataType` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`fullName`),
  KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `output_data`
--

CREATE TABLE IF NOT EXISTS `output_data` (
  `run` int(10) unsigned NOT NULL,
  `data` int(10) unsigned NOT NULL,
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'outputdata'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `pprun`
--

CREATE TABLE IF NOT EXISTS `pprun` (
  `rid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `setup` int(10) unsigned NOT NULL,
  `inputData` int(10) unsigned NOT NULL,
  `outputData` int(10) unsigned NOT NULL,
  PRIMARY KEY (`rid`),
  KEY `outputData` (`outputData`),
  KEY `inputData` (`inputData`),
  KEY `setup` (`setup`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=718191 ;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `quality`
--

CREATE TABLE IF NOT EXISTS `quality` (
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `type` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'DataProperty',
  `formula` text CHARACTER SET utf8 COLLATE utf8_bin,
  `description` text CHARACTER SET utf8 COLLATE utf8_bin,
  `min` float DEFAULT NULL,
  `max` float DEFAULT NULL,
  `unit` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `queries`
--

CREATE TABLE IF NOT EXISTS `queries` (
  `qid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `type` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `description` text CHARACTER SET utf8 COLLATE utf8_bin,
  `query` mediumtext CHARACTER SET utf8 COLLATE utf8_bin,
  PRIMARY KEY (`qid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=22 ;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `query_graphs`
--

CREATE TABLE IF NOT EXISTS `query_graphs` (
  `graphID` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `type` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `description` text CHARACTER SET utf8 COLLATE utf8_bin,
  `query` mediumtext CHARACTER SET utf8 COLLATE utf8_bin,
  `favorite` tinyint(1) NOT NULL,
  PRIMARY KEY (`graphID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `run`
--

CREATE TABLE IF NOT EXISTS `run` (
  `rid` int(10) unsigned NOT NULL,
  `parent` int(10) unsigned DEFAULT NULL,
  `setup` int(10) unsigned NOT NULL DEFAULT '0',
  `start_time` varchar(254) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `status` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `priority` tinyint(8) NOT NULL DEFAULT '10',
  `error` varchar(512) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `experiment` int(10) unsigned DEFAULT NULL,
  `runner` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `machine` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`rid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `task`
--

CREATE TABLE IF NOT EXISTS `task` (
  `task_id` int(10) NOT NULL AUTO_INCREMENT,
  `task_type` varchar(255) NOT NULL DEFAULT 'classification',
  `description` text,
  `method` varchar(255) NOT NULL DEFAULT 'cross-validation',
  `repeats` int(10) NOT NULL DEFAULT '1',
  `folds` int(10) NOT NULL DEFAULT '1',
  `dataset_description_id` int(10) NOT NULL,
  `target_feature` varchar(255) NOT NULL,
  PRIMARY KEY (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `task_math_function`
--

CREATE TABLE IF NOT EXISTS `task_math_function` (
  `task_id` int(10) NOT NULL,
  `math_function_id` int(10) NOT NULL,
  PRIMARY KEY (`task_id`,`math_function_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `tuple`
--

CREATE TABLE IF NOT EXISTS `tuple` (
  `variable` int(11) NOT NULL,
  `valueNr` int(10) unsigned NOT NULL,
  `value` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`variable`,`valueNr`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `workflow`
--

CREATE TABLE IF NOT EXISTS `workflow` (
  `name` varchar(132) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `description` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`name`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `workflow_setup`
--

CREATE TABLE IF NOT EXISTS `workflow_setup` (
  `sid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `implementation` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `workflow` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `isDefault` enum('true','false') CHARACTER SET utf8 COLLATE utf8_bin DEFAULT 'false',
  PRIMARY KEY (`sid`),
  KEY `is_default` (`isDefault`),
  KEY `workflow` (`workflow`),
  KEY `implementation` (`implementation`(255))
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=203050 ;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `_duplicates`
--

CREATE TABLE IF NOT EXISTS `_duplicates` (
  `original` bigint(20) NOT NULL,
  `duplicate` bigint(20) NOT NULL,
  UNIQUE KEY `original` (`original`,`duplicate`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `_hiddennodes`
--

CREATE TABLE IF NOT EXISTS `_hiddennodes` (
  `eid` int(10) unsigned NOT NULL DEFAULT '0',
  `laid` int(10) unsigned NOT NULL DEFAULT '0',
  `val` varchar(64) CHARACTER SET latin1 NOT NULL DEFAULT '',
  `nr_attributes` smallint(5) unsigned DEFAULT NULL,
  `classcount` smallint(5) unsigned DEFAULT NULL,
  `nodes` double(64,0) DEFAULT NULL,
  KEY `eid` (`eid`,`laid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Tabelstructuur voor tabel `_wierdperclassresults`
--

CREATE TABLE IF NOT EXISTS `_wierdperclassresults` (
  `runID` bigint(20) NOT NULL,
  PRIMARY KEY (`runID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
