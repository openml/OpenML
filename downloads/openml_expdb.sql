-- phpMyAdmin SQL Dump
-- version 4.8.3
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Feb 21, 2019 at 10:14 AM
-- Server version: 5.7.24
-- PHP Version: 7.2.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `openml_expdb`
--
CREATE DATABASE IF NOT EXISTS `openml_expdb` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `openml_expdb`;

-- --------------------------------------------------------

--
-- Table structure for table `algorithm`
--

CREATE TABLE `algorithm` (
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `function` tinytext CHARACTER SET latin1,
  `description` text CHARACTER SET latin1,
  `algorithm_class` varchar(32) CHARACTER SET latin1 DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `algorithm_quality`
--

CREATE TABLE `algorithm_quality` (
  `implementation_id` int(16) NOT NULL,
  `quality` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `qualityImplementation` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `label` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `value` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `algorithm_setup`
--

CREATE TABLE `algorithm_setup` (
  `sid` int(10) UNSIGNED NOT NULL,
  `parent` int(10) UNSIGNED NOT NULL,
  `implementation_id` int(16) NOT NULL,
  `algorithm` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `role` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'Learner',
  `isDefault` enum('true','false') CHARACTER SET utf8 COLLATE utf8_bin DEFAULT 'false',
  `algorithm_structure` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `setup_string` text COLLATE utf8_unicode_ci
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `awarded_badges`
--

CREATE TABLE `awarded_badges` (
  `aid` int(16) NOT NULL,
  `badge_id` int(16) NOT NULL,
  `user_id` int(16) NOT NULL,
  `rank` smallint(6) NOT NULL,
  `awarded_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `dataset`
--

CREATE TABLE `dataset` (
  `did` int(10) UNSIGNED NOT NULL,
  `uploader` mediumint(8) UNSIGNED DEFAULT NULL,
  `source` int(10) UNSIGNED DEFAULT NULL,
  `name` varchar(128) COLLATE utf8_unicode_ci NOT NULL,
  `version` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `version_label` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL,
  `description` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `format` varchar(64) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'arff',
  `creator` text COLLATE utf8_unicode_ci,
  `contributor` text COLLATE utf8_unicode_ci,
  `collection_date` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL,
  `upload_date` datetime NOT NULL,
  `language` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL,
  `licence` varchar(64) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'Public',
  `citation` text COLLATE utf8_unicode_ci,
  `collection` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `url` mediumtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `isOriginal` enum('true','false') CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `file_id` int(10) DEFAULT NULL,
  `default_target_attribute` varchar(1024) COLLATE utf8_unicode_ci DEFAULT NULL,
  `row_id_attribute` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ignore_attribute` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL,
  `paper_url` mediumtext COLLATE utf8_unicode_ci,
  `visibility` varchar(128) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'public',
  `original_data_id` int(10) DEFAULT NULL,
  `original_data_url` mediumtext COLLATE utf8_unicode_ci,
  `update_comment` text COLLATE utf8_unicode_ci,
  `last_update` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `dataset_status`
--

CREATE TABLE `dataset_status` (
  `did` int(10) UNSIGNED NOT NULL,
  `status` enum('active','deactivated') NOT NULL,
  `status_date` datetime NOT NULL,
  `user_id` mediumint(8) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `dataset_tag`
--

CREATE TABLE `dataset_tag` (
  `id` int(10) UNSIGNED NOT NULL,
  `tag` varchar(255) NOT NULL,
  `uploader` mediumint(8) UNSIGNED NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `data_feature`
--

CREATE TABLE `data_feature` (
  `did` int(10) UNSIGNED NOT NULL,
  `index` int(10) UNSIGNED NOT NULL,
  `evaluation_engine_id` int(16) NOT NULL,
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `data_type` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `is_target` enum('true','false') CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'false',
  `is_row_identifier` enum('true','false') CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'false',
  `is_ignore` enum('true','false') CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'false',
  `NumberOfDistinctValues` int(11) DEFAULT NULL,
  `NumberOfUniqueValues` int(11) DEFAULT NULL,
  `NumberOfMissingValues` int(11) NOT NULL,
  `NumberOfIntegerValues` int(11) DEFAULT NULL,
  `NumberOfRealValues` int(11) DEFAULT NULL,
  `NumberOfNominalValues` varchar(512) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `NumberOfValues` int(11) NOT NULL,
  `MaximumValue` int(11) DEFAULT NULL,
  `MinimumValue` int(11) DEFAULT NULL,
  `MeanValue` int(11) DEFAULT NULL,
  `StandardDeviation` int(11) DEFAULT NULL,
  `ClassDistribution` text CHARACTER SET utf8 COLLATE utf8_bin
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `data_feature_value`
--

CREATE TABLE `data_feature_value` (
  `did` int(10) UNSIGNED NOT NULL,
  `index` int(10) UNSIGNED NOT NULL,
  `value` varchar(256) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `data_processed`
--

CREATE TABLE `data_processed` (
  `did` int(10) UNSIGNED NOT NULL,
  `evaluation_engine_id` int(16) NOT NULL,
  `user_id` int(16) NOT NULL,
  `processing_date` datetime NOT NULL,
  `error` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `warning` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `num_tries` int(8) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `data_quality`
--

CREATE TABLE `data_quality` (
  `data` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `quality` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `evaluation_engine_id` int(16) NOT NULL,
  `value` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `description` text COLLATE utf8_unicode_ci
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `data_quality_interval`
--

CREATE TABLE `data_quality_interval` (
  `data` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `quality` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `evaluation_engine_id` int(16) NOT NULL,
  `interval_start` int(16) NOT NULL,
  `interval_end` int(16) NOT NULL,
  `value` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `downloads`
--

CREATE TABLE `downloads` (
  `did` int(16) NOT NULL,
  `knowledge_type` varchar(1) CHARACTER SET utf8 NOT NULL,
  `knowledge_id` int(10) NOT NULL,
  `user_id` mediumint(8) NOT NULL,
  `count` smallint(6) NOT NULL DEFAULT '1',
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `downvotes`
--

CREATE TABLE `downvotes` (
  `did` int(16) NOT NULL,
  `knowledge_type` varchar(1) CHARACTER SET utf8 NOT NULL,
  `knowledge_id` int(10) NOT NULL,
  `user_id` mediumint(8) NOT NULL,
  `reason` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `original` tinyint(4) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `downvote_reasons`
--

CREATE TABLE `downvote_reasons` (
  `reason_id` int(11) NOT NULL,
  `description` varchar(256) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `estimation_procedure`
--

CREATE TABLE `estimation_procedure` (
  `id` int(8) NOT NULL,
  `ttid` int(11) NOT NULL,
  `name` varchar(128) NOT NULL,
  `type` enum('crossvalidation','leaveoneout','holdout','bootstrapping','subsampling','testthentrain','holdoutunlabeled','customholdout','testontrainingdata') NOT NULL,
  `repeats` int(8) DEFAULT NULL,
  `folds` int(8) DEFAULT NULL,
  `samples` enum('false','true') NOT NULL DEFAULT 'false',
  `percentage` int(8) DEFAULT NULL,
  `stratified_sampling` enum('true','false') DEFAULT NULL,
  `custom_testset` enum('true','false') NOT NULL DEFAULT 'false',
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `estimation_procedure_type`
--

CREATE TABLE `estimation_procedure_type` (
  `name` varchar(64) NOT NULL,
  `description` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `evaluation`
--

CREATE TABLE `evaluation` (
  `source` int(10) UNSIGNED NOT NULL,
  `function_id` int(8) NOT NULL,
  `evaluation_engine_id` int(16) NOT NULL,
  `value` double DEFAULT NULL,
  `stdev` double DEFAULT NULL,
  `array_data` text COLLATE utf8_unicode_ci
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `evaluation_engine`
--

CREATE TABLE `evaluation_engine` (
  `id` int(16) NOT NULL,
  `name` varchar(256) NOT NULL,
  `description` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `evaluation_fold`
--

CREATE TABLE `evaluation_fold` (
  `source` int(10) UNSIGNED NOT NULL,
  `function_id` int(8) NOT NULL,
  `evaluation_engine_id` int(16) NOT NULL,
  `fold` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `repeat` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `value` double DEFAULT NULL,
  `array_data` text COLLATE utf8_unicode_ci
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `evaluation_sample`
--

CREATE TABLE `evaluation_sample` (
  `source` int(10) UNSIGNED NOT NULL,
  `function_id` int(8) NOT NULL,
  `evaluation_engine_id` int(16) NOT NULL,
  `repeat` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `fold` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `sample` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `sample_size` int(10) NOT NULL,
  `value` double DEFAULT NULL,
  `array_data` text COLLATE utf8_unicode_ci
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `feature_quality`
--

CREATE TABLE `feature_quality` (
  `data` int(10) UNSIGNED NOT NULL,
  `quality` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `feature_index` int(10) UNSIGNED NOT NULL,
  `evaluation_engine_id` int(16) NOT NULL,
  `value` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `implementation`
--

CREATE TABLE `implementation` (
  `id` int(16) NOT NULL,
  `fullName` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `uploader` mediumint(8) UNSIGNED DEFAULT NULL,
  `name` varchar(1024) COLLATE utf8_unicode_ci NOT NULL,
  `custom_name` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
  `class_name` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
  `version` int(16) NOT NULL,
  `external_version` varchar(128) COLLATE utf8_unicode_ci NOT NULL,
  `creator` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL,
  `contributor` text COLLATE utf8_unicode_ci,
  `uploadDate` datetime NOT NULL,
  `licence` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `language` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8_unicode_ci,
  `fullDescription` text COLLATE utf8_unicode_ci,
  `installationNotes` text COLLATE utf8_unicode_ci,
  `dependencies` text COLLATE utf8_unicode_ci,
  `implements` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL,
  `binary_file_id` int(10) DEFAULT NULL,
  `source_file_id` int(10) DEFAULT NULL,
  `visibility` varchar(128) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'public',
  `citation` text COLLATE utf8_unicode_ci
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `implementation_component`
--

CREATE TABLE `implementation_component` (
  `parent` int(16) NOT NULL,
  `child` int(16) NOT NULL,
  `identifier` varchar(1024) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `implementation_tag`
--

CREATE TABLE `implementation_tag` (
  `id` int(10) NOT NULL,
  `tag` varchar(255) NOT NULL,
  `uploader` mediumint(8) UNSIGNED NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `input`
--

CREATE TABLE `input` (
  `id` int(10) NOT NULL,
  `implementation_id` int(16) NOT NULL,
  `name` varchar(512) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `description` text CHARACTER SET utf8 COLLATE utf8_bin,
  `dataType` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `defaultValue` text CHARACTER SET utf8 COLLATE utf8_bin,
  `recommendedRange` text CHARACTER SET utf8 COLLATE utf8_bin
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `input_data`
--

CREATE TABLE `input_data` (
  `run` int(11) NOT NULL,
  `data` int(11) NOT NULL,
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'inputdata'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `input_setting`
--

CREATE TABLE `input_setting` (
  `setup` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `input` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `input_id` int(10) NOT NULL,
  `value` varchar(2048) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `likes`
--

CREATE TABLE `likes` (
  `lid` int(16) NOT NULL,
  `knowledge_type` varchar(1) CHARACTER SET utf8 NOT NULL,
  `knowledge_id` int(10) NOT NULL,
  `user_id` mediumint(8) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `math_function`
--

CREATE TABLE `math_function` (
  `id` int(8) NOT NULL,
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `functionType` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'EvaluationFunction',
  `min` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `max` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `unit` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `higherIsBetter` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `description` text CHARACTER SET utf8 COLLATE utf8_bin,
  `source_code` text COLLATE utf8_unicode_ci NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `notebook`
--

CREATE TABLE `notebook` (
  `kind` varchar(16) NOT NULL,
  `id` bigint(20) NOT NULL,
  `notebook_name` varchar(512) NOT NULL,
  `github_repo_url` text NOT NULL,
  `everware_compatible` enum('true','false') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `output_data`
--

CREATE TABLE `output_data` (
  `run` int(10) UNSIGNED NOT NULL,
  `data` int(10) UNSIGNED NOT NULL,
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'outputdata',
  `field` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `pdnresults`
--

CREATE TABLE `pdnresults` (
  `dataset` varchar(128) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `algorithm` varchar(128) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `parameters` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `confusion_matrix` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `value` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `quality`
--

CREATE TABLE `quality` (
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `type` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'DataProperty',
  `formula` text CHARACTER SET utf8 COLLATE utf8_bin,
  `description` text CHARACTER SET utf8 COLLATE utf8_bin,
  `datatype` varchar(128) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'undefined',
  `min` float DEFAULT NULL,
  `max` float DEFAULT NULL,
  `unit` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `priority` int(11) NOT NULL DEFAULT '9999',
  `showonline` enum('true','false') COLLATE utf8_unicode_ci NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `run`
--

CREATE TABLE `run` (
  `rid` int(10) UNSIGNED NOT NULL,
  `uploader` mediumint(8) UNSIGNED DEFAULT NULL,
  `setup` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `task_id` int(10) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `error_message` text CHARACTER SET utf8 COLLATE utf8_bin,
  `run_details` text CHARACTER SET utf8 COLLATE utf8_bin,
  `visibility` varchar(128) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'public'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `runfile`
--

CREATE TABLE `runfile` (
  `source` int(10) UNSIGNED NOT NULL,
  `field` varchar(128) NOT NULL,
  `name` varchar(128) NOT NULL,
  `format` varchar(128) NOT NULL,
  `upload_time` datetime DEFAULT NULL,
  `file_id` int(16) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `run_evaluated`
--

CREATE TABLE `run_evaluated` (
  `run_id` int(10) UNSIGNED NOT NULL,
  `evaluation_engine_id` int(16) NOT NULL,
  `user_id` int(16) NOT NULL,
  `evaluation_date` datetime NOT NULL,
  `error` text,
  `warning` text,
  `num_tries` int(8) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `run_study`
--

CREATE TABLE `run_study` (
  `study_id` int(10) NOT NULL,
  `run_id` int(10) UNSIGNED NOT NULL,
  `uploader` mediumint(8) UNSIGNED NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `run_tag`
--

CREATE TABLE `run_tag` (
  `id` int(10) UNSIGNED NOT NULL,
  `tag` varchar(255) NOT NULL,
  `uploader` mediumint(8) UNSIGNED NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `schedule`
--

CREATE TABLE `schedule` (
  `sid` int(8) NOT NULL,
  `task_id` int(8) NOT NULL,
  `experiment` varchar(128) NOT NULL DEFAULT '',
  `active` enum('true','false') NOT NULL DEFAULT 'true',
  `last_assigned` datetime DEFAULT NULL,
  `ttid` int(11) NOT NULL,
  `dependencies` varchar(128) NOT NULL,
  `setup_string` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `setup_differences`
--

CREATE TABLE `setup_differences` (
  `sidA` int(10) NOT NULL,
  `sidB` int(10) NOT NULL,
  `task_id` int(10) NOT NULL,
  `task_size` int(10) NOT NULL,
  `differences` int(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `setup_tag`
--

CREATE TABLE `setup_tag` (
  `id` int(10) UNSIGNED NOT NULL,
  `tag` varchar(255) NOT NULL,
  `uploader` mediumint(8) UNSIGNED NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `study`
--

CREATE TABLE `study` (
  `id` int(10) NOT NULL,
  `alias` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `main_entity_type` enum('run','task') COLLATE utf8_bin NOT NULL DEFAULT 'run',
  `benchmark_suite` int(10) DEFAULT NULL,
  `name` varchar(256) COLLATE utf8_bin NOT NULL,
  `description` text COLLATE utf8_bin NOT NULL,
  `visibility` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT 'public',
  `creation_date` datetime NOT NULL,
  `creator` mediumint(8) UNSIGNED NOT NULL,
  `legacy` enum('y','n') COLLATE utf8_bin NOT NULL DEFAULT 'y'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `study_tag`
--

CREATE TABLE `study_tag` (
  `study_id` int(10) NOT NULL,
  `tag` varchar(255) NOT NULL,
  `window_start` datetime DEFAULT NULL,
  `window_end` datetime DEFAULT NULL,
  `write_access` enum('private','public') NOT NULL DEFAULT 'private'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `task`
--

CREATE TABLE `task` (
  `task_id` int(10) NOT NULL,
  `ttid` int(10) NOT NULL,
  `creator` mediumint(8) UNSIGNED DEFAULT NULL,
  `creation_date` datetime DEFAULT NULL,
  `embargo_end_date` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `task_inputs`
--

CREATE TABLE `task_inputs` (
  `task_id` int(10) NOT NULL,
  `input` varchar(64) NOT NULL,
  `value` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `task_io_types`
--

CREATE TABLE `task_io_types` (
  `name` varchar(64) NOT NULL,
  `description` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `task_study`
--

CREATE TABLE `task_study` (
  `study_id` int(10) NOT NULL,
  `task_id` int(10) NOT NULL,
  `uploader` mediumint(8) UNSIGNED NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `task_tag`
--

CREATE TABLE `task_tag` (
  `id` int(10) NOT NULL,
  `tag` varchar(255) NOT NULL,
  `uploader` mediumint(8) UNSIGNED NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `task_type`
--

CREATE TABLE `task_type` (
  `ttid` int(10) NOT NULL,
  `name` varchar(128) NOT NULL,
  `description` text NOT NULL,
  `creator` varchar(128) NOT NULL,
  `contributors` text,
  `creationDate` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `task_type_inout`
--

CREATE TABLE `task_type_inout` (
  `ttid` int(10) NOT NULL,
  `name` varchar(64) NOT NULL,
  `type` varchar(64) NOT NULL,
  `io` enum('input','output') NOT NULL,
  `requirement` enum('required','optional','hidden') NOT NULL,
  `description` varchar(256) NOT NULL,
  `order` int(8) NOT NULL,
  `api_constraints` text,
  `template_api` text,
  `template_search` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `trace`
--

CREATE TABLE `trace` (
  `run_id` int(10) UNSIGNED NOT NULL,
  `evaluation_engine_id` int(16) NOT NULL DEFAULT '1',
  `repeat` int(11) NOT NULL,
  `fold` int(11) NOT NULL,
  `iteration` int(11) NOT NULL,
  `setup_string` text NOT NULL,
  `evaluation` varchar(265) NOT NULL,
  `selected` enum('true','false') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `algorithm`
--
ALTER TABLE `algorithm`
  ADD PRIMARY KEY (`name`);

--
-- Indexes for table `algorithm_quality`
--
ALTER TABLE `algorithm_quality`
  ADD PRIMARY KEY (`implementation_id`,`quality`,`label`);

--
-- Indexes for table `algorithm_setup`
--
ALTER TABLE `algorithm_setup`
  ADD PRIMARY KEY (`sid`),
  ADD KEY `is_default` (`isDefault`),
  ADD KEY `parent` (`parent`),
  ADD KEY `algorithm` (`algorithm`),
  ADD KEY `role` (`role`),
  ADD KEY `role-parent` (`role`,`parent`),
  ADD KEY `role-sid` (`role`,`sid`),
  ADD KEY `algodefault` (`algorithm`,`isDefault`,`sid`),
  ADD KEY `implementation_id` (`implementation_id`);

--
-- Indexes for table `awarded_badges`
--
ALTER TABLE `awarded_badges`
  ADD PRIMARY KEY (`aid`),
  ADD UNIQUE KEY `badge_id` (`badge_id`,`user_id`,`aid`);

--
-- Indexes for table `dataset`
--
ALTER TABLE `dataset`
  ADD PRIMARY KEY (`did`),
  ADD UNIQUE KEY `nameID` (`name`,`version`),
  ADD KEY `name` (`name`),
  ADD KEY `file_id` (`file_id`),
  ADD KEY `uploader` (`uploader`);

--
-- Indexes for table `dataset_status`
--
ALTER TABLE `dataset_status`
  ADD PRIMARY KEY (`did`,`status`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `dataset_tag`
--
ALTER TABLE `dataset_tag`
  ADD PRIMARY KEY (`id`,`tag`),
  ADD KEY `uploader` (`uploader`);

--
-- Indexes for table `data_feature`
--
ALTER TABLE `data_feature`
  ADD PRIMARY KEY (`did`,`index`),
  ADD KEY `evaluation_engine_id` (`evaluation_engine_id`),
  ADD KEY `did` (`did`,`evaluation_engine_id`);

--
-- Indexes for table `data_feature_value`
--
ALTER TABLE `data_feature_value`
  ADD KEY `did` (`did`,`index`);

--
-- Indexes for table `data_processed`
--
ALTER TABLE `data_processed`
  ADD PRIMARY KEY (`did`,`evaluation_engine_id`),
  ADD KEY `evaluation_engine_id` (`evaluation_engine_id`);

--
-- Indexes for table `data_quality`
--
ALTER TABLE `data_quality`
  ADD PRIMARY KEY (`data`,`quality`,`evaluation_engine_id`),
  ADD KEY `evaluation_engine_id` (`evaluation_engine_id`),
  ADD KEY `quality` (`quality`),
  ADD KEY `data` (`data`,`evaluation_engine_id`);

--
-- Indexes for table `data_quality_interval`
--
ALTER TABLE `data_quality_interval`
  ADD PRIMARY KEY (`data`,`quality`,`evaluation_engine_id`,`interval_start`,`interval_end`),
  ADD KEY `evaluation_engine_id` (`evaluation_engine_id`),
  ADD KEY `quality` (`quality`),
  ADD KEY `data` (`data`,`evaluation_engine_id`);

--
-- Indexes for table `downloads`
--
ALTER TABLE `downloads`
  ADD PRIMARY KEY (`knowledge_type`,`knowledge_id`,`user_id`),
  ADD UNIQUE KEY `did` (`did`);

--
-- Indexes for table `downvotes`
--
ALTER TABLE `downvotes`
  ADD PRIMARY KEY (`knowledge_type`,`knowledge_id`,`user_id`),
  ADD UNIQUE KEY `vid` (`did`);

--
-- Indexes for table `downvote_reasons`
--
ALTER TABLE `downvote_reasons`
  ADD PRIMARY KEY (`reason_id`),
  ADD UNIQUE KEY `reason_id_UNIQUE` (`reason_id`),
  ADD UNIQUE KEY `reason_description_UNIQUE` (`description`);

--
-- Indexes for table `estimation_procedure`
--
ALTER TABLE `estimation_procedure`
  ADD PRIMARY KEY (`id`),
  ADD KEY `ttid` (`ttid`);

--
-- Indexes for table `estimation_procedure_type`
--
ALTER TABLE `estimation_procedure_type`
  ADD PRIMARY KEY (`name`);

--
-- Indexes for table `evaluation`
--
ALTER TABLE `evaluation`
  ADD PRIMARY KEY (`source`,`function_id`,`evaluation_engine_id`),
  ADD KEY `function_id` (`function_id`),
  ADD KEY `evaluation_ibfk_3` (`evaluation_engine_id`),
  ADD KEY `evaluation_ibfk_4` (`source`,`evaluation_engine_id`);

--
-- Indexes for table `evaluation_engine`
--
ALTER TABLE `evaluation_engine`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `evaluation_fold`
--
ALTER TABLE `evaluation_fold`
  ADD PRIMARY KEY (`source`,`function_id`,`evaluation_engine_id`,`fold`,`repeat`),
  ADD KEY `evaluation_fold_ibfk_3` (`evaluation_engine_id`),
  ADD KEY `evaluation_fold_ibfk_4` (`source`,`evaluation_engine_id`),
  ADD KEY `function_id` (`function_id`) USING BTREE;

--
-- Indexes for table `evaluation_sample`
--
ALTER TABLE `evaluation_sample`
  ADD PRIMARY KEY (`source`,`function_id`,`evaluation_engine_id`,`repeat`,`fold`,`sample`),
  ADD KEY `evaluation_sample_ibfk_3` (`evaluation_engine_id`),
  ADD KEY `evaluation_sample_ibfk_4` (`source`,`evaluation_engine_id`),
  ADD KEY `function_id` (`function_id`) USING BTREE;

--
-- Indexes for table `feature_quality`
--
ALTER TABLE `feature_quality`
  ADD PRIMARY KEY (`data`,`quality`,`feature_index`,`evaluation_engine_id`),
  ADD KEY `quality` (`quality`),
  ADD KEY `evaluation_engine_id` (`evaluation_engine_id`),
  ADD KEY `data` (`data`,`evaluation_engine_id`),
  ADD KEY `data_2` (`data`,`feature_index`);

--
-- Indexes for table `implementation`
--
ALTER TABLE `implementation`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `version` (`id`,`external_version`),
  ADD KEY `name` (`name`(255)),
  ADD KEY `user` (`uploader`);

--
-- Indexes for table `implementation_component`
--
ALTER TABLE `implementation_component`
  ADD PRIMARY KEY (`parent`,`child`),
  ADD KEY `child` (`child`);

--
-- Indexes for table `implementation_tag`
--
ALTER TABLE `implementation_tag`
  ADD PRIMARY KEY (`id`,`tag`),
  ADD KEY `uploader` (`uploader`);

--
-- Indexes for table `input`
--
ALTER TABLE `input`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `implementation_id` (`implementation_id`,`name`),
  ADD KEY `name` (`name`);

--
-- Indexes for table `input_data`
--
ALTER TABLE `input_data`
  ADD PRIMARY KEY (`run`,`data`),
  ADD KEY `name` (`name`);

--
-- Indexes for table `input_setting`
--
ALTER TABLE `input_setting`
  ADD PRIMARY KEY (`setup`,`input_id`),
  ADD KEY `value` (`value`(255)),
  ADD KEY `fk_input_setting_input_id` (`input_id`);

--
-- Indexes for table `likes`
--
ALTER TABLE `likes`
  ADD PRIMARY KEY (`knowledge_type`,`knowledge_id`,`user_id`),
  ADD UNIQUE KEY `lid` (`lid`);

--
-- Indexes for table `math_function`
--
ALTER TABLE `math_function`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indexes for table `notebook`
--
ALTER TABLE `notebook`
  ADD UNIQUE KEY `kind` (`kind`,`id`);

--
-- Indexes for table `pdnresults`
--
ALTER TABLE `pdnresults`
  ADD KEY `dataset` (`dataset`,`algorithm`);

--
-- Indexes for table `quality`
--
ALTER TABLE `quality`
  ADD PRIMARY KEY (`name`);

--
-- Indexes for table `run`
--
ALTER TABLE `run`
  ADD PRIMARY KEY (`rid`),
  ADD KEY `setuptask` (`setup`,`task_id`),
  ADD KEY `taskuser` (`task_id`,`uploader`),
  ADD KEY `setupuser` (`setup`,`uploader`),
  ADD KEY `taskusertime` (`task_id`,`uploader`,`start_time`),
  ADD KEY `uploader` (`uploader`);

--
-- Indexes for table `runfile`
--
ALTER TABLE `runfile`
  ADD PRIMARY KEY (`source`,`field`),
  ADD UNIQUE KEY `source` (`source`,`field`),
  ADD KEY `runfile_ibfk_2` (`file_id`);

--
-- Indexes for table `run_evaluated`
--
ALTER TABLE `run_evaluated`
  ADD PRIMARY KEY (`run_id`,`evaluation_engine_id`),
  ADD KEY `evaluation_engine_id` (`evaluation_engine_id`),
  ADD KEY `run_id` (`run_id`);

--
-- Indexes for table `run_study`
--
ALTER TABLE `run_study`
  ADD KEY `run_id` (`run_id`),
  ADD KEY `study_id` (`study_id`),
  ADD KEY `uploader` (`uploader`);

--
-- Indexes for table `run_tag`
--
ALTER TABLE `run_tag`
  ADD PRIMARY KEY (`id`,`tag`),
  ADD KEY `uploader` (`uploader`);

--
-- Indexes for table `schedule`
--
ALTER TABLE `schedule`
  ADD PRIMARY KEY (`sid`,`task_id`),
  ADD UNIQUE KEY `sid` (`sid`,`task_id`,`active`),
  ADD KEY `sid_2` (`sid`,`task_id`,`active`,`last_assigned`),
  ADD KEY `getjob` (`dependencies`,`active`,`ttid`,`last_assigned`);

--
-- Indexes for table `setup_differences`
--
ALTER TABLE `setup_differences`
  ADD PRIMARY KEY (`sidA`,`sidB`,`task_id`);

--
-- Indexes for table `setup_tag`
--
ALTER TABLE `setup_tag`
  ADD PRIMARY KEY (`id`,`tag`),
  ADD KEY `uploader` (`uploader`);

--
-- Indexes for table `study`
--
ALTER TABLE `study`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `id` (`id`,`creator`),
  ADD UNIQUE KEY `alias` (`alias`),
  ADD KEY `creator` (`creator`),
  ADD KEY `benchmark_suite` (`benchmark_suite`);

--
-- Indexes for table `study_tag`
--
ALTER TABLE `study_tag`
  ADD PRIMARY KEY (`study_id`,`tag`);

--
-- Indexes for table `task`
--
ALTER TABLE `task`
  ADD PRIMARY KEY (`task_id`),
  ADD KEY `creator` (`creator`),
  ADD KEY `ttid` (`ttid`);

--
-- Indexes for table `task_inputs`
--
ALTER TABLE `task_inputs`
  ADD PRIMARY KEY (`task_id`,`input`),
  ADD UNIQUE KEY `val` (`task_id`,`input`,`value`(16));

--
-- Indexes for table `task_io_types`
--
ALTER TABLE `task_io_types`
  ADD PRIMARY KEY (`name`);

--
-- Indexes for table `task_study`
--
ALTER TABLE `task_study`
  ADD KEY `task_id` (`task_id`),
  ADD KEY `study_id` (`study_id`),
  ADD KEY `uploader` (`uploader`);

--
-- Indexes for table `task_tag`
--
ALTER TABLE `task_tag`
  ADD PRIMARY KEY (`id`,`tag`),
  ADD KEY `uploader` (`uploader`);

--
-- Indexes for table `task_type`
--
ALTER TABLE `task_type`
  ADD PRIMARY KEY (`ttid`);

--
-- Indexes for table `task_type_inout`
--
ALTER TABLE `task_type_inout`
  ADD PRIMARY KEY (`ttid`,`name`,`type`);

--
-- Indexes for table `trace`
--
ALTER TABLE `trace`
  ADD PRIMARY KEY (`run_id`,`evaluation_engine_id`,`repeat`,`fold`,`iteration`),
  ADD KEY `run_id` (`run_id`,`evaluation_engine_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `algorithm_setup`
--
ALTER TABLE `algorithm_setup`
  MODIFY `sid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `awarded_badges`
--
ALTER TABLE `awarded_badges`
  MODIFY `aid` int(16) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `dataset`
--
ALTER TABLE `dataset`
  MODIFY `did` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `downloads`
--
ALTER TABLE `downloads`
  MODIFY `did` int(16) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `downvotes`
--
ALTER TABLE `downvotes`
  MODIFY `did` int(16) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `downvote_reasons`
--
ALTER TABLE `downvote_reasons`
  MODIFY `reason_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `estimation_procedure`
--
ALTER TABLE `estimation_procedure`
  MODIFY `id` int(8) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `evaluation_engine`
--
ALTER TABLE `evaluation_engine`
  MODIFY `id` int(16) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `implementation`
--
ALTER TABLE `implementation`
  MODIFY `id` int(16) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `input`
--
ALTER TABLE `input`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `likes`
--
ALTER TABLE `likes`
  MODIFY `lid` int(16) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `math_function`
--
ALTER TABLE `math_function`
  MODIFY `id` int(8) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `run`
--
ALTER TABLE `run`
  MODIFY `rid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `study`
--
ALTER TABLE `study`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `task`
--
ALTER TABLE `task`
  MODIFY `task_id` int(10) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `task_type`
--
ALTER TABLE `task_type`
  MODIFY `ttid` int(10) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `algorithm_setup`
--
ALTER TABLE `algorithm_setup`
  ADD CONSTRAINT `fk_implementation_id` FOREIGN KEY (`implementation_id`) REFERENCES `implementation` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `dataset`
--
ALTER TABLE `dataset`
  ADD CONSTRAINT `dataset_ibfk_1` FOREIGN KEY (`file_id`) REFERENCES `openml`.`file` (`id`),
  ADD CONSTRAINT `dataset_ibfk_2` FOREIGN KEY (`uploader`) REFERENCES `openml`.`users` (`id`);

--
-- Constraints for table `dataset_status`
--
ALTER TABLE `dataset_status`
  ADD CONSTRAINT `dataset_status_ibfk_1` FOREIGN KEY (`did`) REFERENCES `dataset` (`did`) ON DELETE CASCADE,
  ADD CONSTRAINT `dataset_status_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `openml`.`users` (`id`);

--
-- Constraints for table `dataset_tag`
--
ALTER TABLE `dataset_tag`
  ADD CONSTRAINT `dataset_tag_ibfk_1` FOREIGN KEY (`uploader`) REFERENCES `openml`.`users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_dataset_tag` FOREIGN KEY (`id`) REFERENCES `dataset` (`did`) ON DELETE CASCADE;

--
-- Constraints for table `data_feature`
--
ALTER TABLE `data_feature`
  ADD CONSTRAINT `data_feature_ibfk_1` FOREIGN KEY (`did`) REFERENCES `dataset` (`did`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `data_feature_ibfk_2` FOREIGN KEY (`evaluation_engine_id`) REFERENCES `evaluation_engine` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `data_feature_ibfk_3` FOREIGN KEY (`did`,`evaluation_engine_id`) REFERENCES `data_processed` (`did`, `evaluation_engine_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `data_feature_value`
--
ALTER TABLE `data_feature_value`
  ADD CONSTRAINT `data_feature_value_ibfk_1` FOREIGN KEY (`did`,`index`) REFERENCES `data_feature` (`did`, `index`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `data_processed`
--
ALTER TABLE `data_processed`
  ADD CONSTRAINT `data_processed_ibfk_1` FOREIGN KEY (`did`) REFERENCES `dataset` (`did`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `data_processed_ibfk_2` FOREIGN KEY (`evaluation_engine_id`) REFERENCES `evaluation_engine` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `data_quality`
--
ALTER TABLE `data_quality`
  ADD CONSTRAINT `data_quality_ibfk_1` FOREIGN KEY (`data`) REFERENCES `dataset` (`did`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `data_quality_ibfk_2` FOREIGN KEY (`evaluation_engine_id`) REFERENCES `evaluation_engine` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `data_quality_ibfk_3` FOREIGN KEY (`quality`) REFERENCES `quality` (`name`) ON UPDATE CASCADE,
  ADD CONSTRAINT `data_quality_ibfk_4` FOREIGN KEY (`data`,`evaluation_engine_id`) REFERENCES `data_processed` (`did`, `evaluation_engine_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `data_quality_interval`
--
ALTER TABLE `data_quality_interval`
  ADD CONSTRAINT `data_quality_interval_ibfk_1` FOREIGN KEY (`data`) REFERENCES `dataset` (`did`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `data_quality_interval_ibfk_2` FOREIGN KEY (`evaluation_engine_id`) REFERENCES `evaluation_engine` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `data_quality_interval_ibfk_3` FOREIGN KEY (`quality`) REFERENCES `quality` (`name`) ON UPDATE CASCADE,
  ADD CONSTRAINT `data_quality_interval_ibfk_4` FOREIGN KEY (`data`,`evaluation_engine_id`) REFERENCES `data_processed` (`did`, `evaluation_engine_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `estimation_procedure`
--
ALTER TABLE `estimation_procedure`
  ADD CONSTRAINT `estimation_procedure_ibfk_1` FOREIGN KEY (`ttid`) REFERENCES `task_type` (`ttid`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `evaluation`
--
ALTER TABLE `evaluation`
  ADD CONSTRAINT `evaluation_ibfk_1` FOREIGN KEY (`source`) REFERENCES `run` (`rid`) ON DELETE CASCADE,
  ADD CONSTRAINT `evaluation_ibfk_2` FOREIGN KEY (`function_id`) REFERENCES `math_function` (`id`),
  ADD CONSTRAINT `evaluation_ibfk_3` FOREIGN KEY (`evaluation_engine_id`) REFERENCES `evaluation_engine` (`id`),
  ADD CONSTRAINT `evaluation_ibfk_4` FOREIGN KEY (`source`,`evaluation_engine_id`) REFERENCES `run_evaluated` (`run_id`, `evaluation_engine_id`) ON DELETE CASCADE;

--
-- Constraints for table `evaluation_fold`
--
ALTER TABLE `evaluation_fold`
  ADD CONSTRAINT `evaluation_fold_ibfk_1` FOREIGN KEY (`source`) REFERENCES `run` (`rid`) ON DELETE CASCADE,
  ADD CONSTRAINT `evaluation_fold_ibfk_2` FOREIGN KEY (`function_id`) REFERENCES `math_function` (`id`),
  ADD CONSTRAINT `evaluation_fold_ibfk_3` FOREIGN KEY (`evaluation_engine_id`) REFERENCES `evaluation_engine` (`id`),
  ADD CONSTRAINT `evaluation_fold_ibfk_4` FOREIGN KEY (`source`,`evaluation_engine_id`) REFERENCES `run_evaluated` (`run_id`, `evaluation_engine_id`) ON DELETE CASCADE;

--
-- Constraints for table `evaluation_sample`
--
ALTER TABLE `evaluation_sample`
  ADD CONSTRAINT `evaluation_sample_ibfk_1` FOREIGN KEY (`source`) REFERENCES `run` (`rid`) ON DELETE CASCADE,
  ADD CONSTRAINT `evaluation_sample_ibfk_2` FOREIGN KEY (`function_id`) REFERENCES `math_function` (`id`),
  ADD CONSTRAINT `evaluation_sample_ibfk_3` FOREIGN KEY (`evaluation_engine_id`) REFERENCES `evaluation_engine` (`id`),
  ADD CONSTRAINT `evaluation_sample_ibfk_4` FOREIGN KEY (`source`,`evaluation_engine_id`) REFERENCES `run_evaluated` (`run_id`, `evaluation_engine_id`) ON DELETE CASCADE;

--
-- Constraints for table `feature_quality`
--
ALTER TABLE `feature_quality`
  ADD CONSTRAINT `feature_quality_ibfk_1` FOREIGN KEY (`data`) REFERENCES `dataset` (`did`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `feature_quality_ibfk_2` FOREIGN KEY (`quality`) REFERENCES `quality` (`name`) ON UPDATE CASCADE,
  ADD CONSTRAINT `feature_quality_ibfk_3` FOREIGN KEY (`evaluation_engine_id`) REFERENCES `evaluation_engine` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `feature_quality_ibfk_4` FOREIGN KEY (`data`,`evaluation_engine_id`) REFERENCES `data_processed` (`did`, `evaluation_engine_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `feature_quality_ibfk_5` FOREIGN KEY (`data`,`feature_index`) REFERENCES `data_feature` (`did`, `index`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `implementation`
--
ALTER TABLE `implementation`
  ADD CONSTRAINT `implementation_ibfk_1` FOREIGN KEY (`uploader`) REFERENCES `openml`.`users` (`id`);

--
-- Constraints for table `implementation_component`
--
ALTER TABLE `implementation_component`
  ADD CONSTRAINT `implementation_component_ibfk_1` FOREIGN KEY (`parent`) REFERENCES `implementation` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `implementation_component_ibfk_2` FOREIGN KEY (`child`) REFERENCES `implementation` (`id`);

--
-- Constraints for table `implementation_tag`
--
ALTER TABLE `implementation_tag`
  ADD CONSTRAINT `fk_implementation_tag` FOREIGN KEY (`id`) REFERENCES `implementation` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `implementation_tag_ibfk_1` FOREIGN KEY (`uploader`) REFERENCES `openml`.`users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `input`
--
ALTER TABLE `input`
  ADD CONSTRAINT `fk_input_implementation_id` FOREIGN KEY (`implementation_id`) REFERENCES `implementation` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `input_setting`
--
ALTER TABLE `input_setting`
  ADD CONSTRAINT `fk_input_algorithm_setup_id` FOREIGN KEY (`setup`) REFERENCES `algorithm_setup` (`sid`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_input_setting_input_id` FOREIGN KEY (`input_id`) REFERENCES `input` (`id`);

--
-- Constraints for table `run`
--
ALTER TABLE `run`
  ADD CONSTRAINT `fk_setup_setup_id` FOREIGN KEY (`setup`) REFERENCES `algorithm_setup` (`sid`),
  ADD CONSTRAINT `run_ibfk_1` FOREIGN KEY (`task_id`) REFERENCES `task` (`task_id`),
  ADD CONSTRAINT `run_ibfk_2` FOREIGN KEY (`uploader`) REFERENCES `openml`.`users` (`id`);

--
-- Constraints for table `runfile`
--
ALTER TABLE `runfile`
  ADD CONSTRAINT `runfile_ibfk_1` FOREIGN KEY (`source`) REFERENCES `run` (`rid`) ON DELETE CASCADE,
  ADD CONSTRAINT `runfile_ibfk_2` FOREIGN KEY (`file_id`) REFERENCES `openml`.`file` (`id`);

--
-- Constraints for table `run_evaluated`
--
ALTER TABLE `run_evaluated`
  ADD CONSTRAINT `run_evaluated_ibfk_1` FOREIGN KEY (`run_id`) REFERENCES `run` (`rid`) ON DELETE CASCADE,
  ADD CONSTRAINT `run_evaluated_ibfk_2` FOREIGN KEY (`evaluation_engine_id`) REFERENCES `evaluation_engine` (`id`);

--
-- Constraints for table `run_study`
--
ALTER TABLE `run_study`
  ADD CONSTRAINT `run_study_ibfk_1` FOREIGN KEY (`run_id`) REFERENCES `run` (`rid`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `run_study_ibfk_2` FOREIGN KEY (`study_id`) REFERENCES `study` (`id`),
  ADD CONSTRAINT `run_study_ibfk_3` FOREIGN KEY (`uploader`) REFERENCES `openml`.`users` (`id`);

--
-- Constraints for table `run_tag`
--
ALTER TABLE `run_tag`
  ADD CONSTRAINT `fk_run_tag` FOREIGN KEY (`id`) REFERENCES `run` (`rid`) ON DELETE CASCADE,
  ADD CONSTRAINT `run_tag_ibfk_1` FOREIGN KEY (`uploader`) REFERENCES `openml`.`users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `setup_tag`
--
ALTER TABLE `setup_tag`
  ADD CONSTRAINT `fk_setup_id` FOREIGN KEY (`id`) REFERENCES `algorithm_setup` (`sid`),
  ADD CONSTRAINT `setup_tag_ibfk_1` FOREIGN KEY (`uploader`) REFERENCES `openml`.`users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `study`
--
ALTER TABLE `study`
  ADD CONSTRAINT `study_ibfk_1` FOREIGN KEY (`creator`) REFERENCES `openml`.`users` (`id`),
  ADD CONSTRAINT `study_ibfk_2` FOREIGN KEY (`benchmark_suite`) REFERENCES `study` (`id`);

--
-- Constraints for table `study_tag`
--
ALTER TABLE `study_tag`
  ADD CONSTRAINT `fk_study_tag` FOREIGN KEY (`study_id`) REFERENCES `study` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `task`
--
ALTER TABLE `task`
  ADD CONSTRAINT `task_ibfk_1` FOREIGN KEY (`creator`) REFERENCES `openml`.`users` (`id`),
  ADD CONSTRAINT `task_ibfk_2` FOREIGN KEY (`ttid`) REFERENCES `task_type` (`ttid`);

--
-- Constraints for table `task_inputs`
--
ALTER TABLE `task_inputs`
  ADD CONSTRAINT `task_inputs_ibfk_1` FOREIGN KEY (`task_id`) REFERENCES `task` (`task_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `task_study`
--
ALTER TABLE `task_study`
  ADD CONSTRAINT `task_study_ibfk_1` FOREIGN KEY (`study_id`) REFERENCES `study` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `task_study_ibfk_2` FOREIGN KEY (`task_id`) REFERENCES `task` (`task_id`),
  ADD CONSTRAINT `task_study_ibfk_3` FOREIGN KEY (`uploader`) REFERENCES `openml`.`users` (`id`);

--
-- Constraints for table `task_tag`
--
ALTER TABLE `task_tag`
  ADD CONSTRAINT `fk_task_tag` FOREIGN KEY (`id`) REFERENCES `task` (`task_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `task_tag_ibfk_1` FOREIGN KEY (`uploader`) REFERENCES `openml`.`users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `task_type_inout`
--
ALTER TABLE `task_type_inout`
  ADD CONSTRAINT `task_type_inout_ibfk_1` FOREIGN KEY (`ttid`) REFERENCES `task_type` (`ttid`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `trace`
--
ALTER TABLE `trace`
  ADD CONSTRAINT `fk_trace` FOREIGN KEY (`run_id`,`evaluation_engine_id`) REFERENCES `run_evaluated` (`run_id`, `evaluation_engine_id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
