-- phpMyAdmin SQL Dump
-- version 4.5.4.1deb2ubuntu2.1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Oct 07, 2018 at 09:50 PM
-- Server version: 5.7.23-0ubuntu0.16.04.1-log
-- PHP Version: 7.0.32-0ubuntu0.16.04.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `openml`
--
CREATE DATABASE IF NOT EXISTS `openml` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `openml`;

-- --------------------------------------------------------

--
-- Table structure for table `access`
--

CREATE TABLE `access` (
  `user_id` bigint(20) NOT NULL,
  `group` enum('true','false') NOT NULL,
  `type` enum('data','flow','study') NOT NULL,
  `entity_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `category`
--

CREATE TABLE `category` (
  `id` int(10) NOT NULL,
  `activated` enum('y','n') NOT NULL DEFAULT 'y',
  `deleted` enum('y','n') NOT NULL DEFAULT 'n',
  `title` varchar(255) NOT NULL,
  `description` text,
  `order` int(10) NOT NULL DEFAULT '100'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `file`
--

CREATE TABLE `file` (
  `id` int(16) NOT NULL,
  `creator` int(16) NOT NULL,
  `creation_date` datetime NOT NULL,
  `filepath` varchar(256) NOT NULL,
  `filesize` int(64) NOT NULL,
  `filename_original` varchar(256) NOT NULL,
  `extension` varchar(16) NOT NULL,
  `mime_type` varchar(32) NOT NULL,
  `md5_hash` varchar(64) NOT NULL,
  `type` enum('dataset','implementation','predictions','userimage','run_trace','run_uploaded_file','url','misc') NOT NULL,
  `access_policy` enum('public','private','none','deleted') NOT NULL DEFAULT 'public'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `groups`
--

CREATE TABLE `groups` (
  `id` mediumint(8) UNSIGNED NOT NULL,
  `name` varchar(20) NOT NULL,
  `description` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `login_attempts`
--

CREATE TABLE `login_attempts` (
  `id` mediumint(8) UNSIGNED NOT NULL,
  `ip_address` varbinary(16) NOT NULL,
  `login` varchar(100) NOT NULL,
  `time` int(11) UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `meta_dataset`
--

CREATE TABLE `meta_dataset` (
  `id` int(8) NOT NULL,
  `user_id` int(8) NOT NULL,
  `request_date` datetime NOT NULL,
  `type` enum('evaluations','qualities','inputs') NOT NULL DEFAULT 'evaluations',
  `task_type` int(10) NOT NULL,
  `datasets` text,
  `tasks` text,
  `flows` text,
  `setups` text,
  `functions` text,
  `file_id` int(8) DEFAULT NULL,
  `processed` datetime DEFAULT NULL,
  `error_message` varchar(256) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `thread`
--

CREATE TABLE `thread` (
  `id` int(10) NOT NULL,
  `activated` enum('y','n') NOT NULL DEFAULT 'n',
  `deleted` enum('y','n') NOT NULL DEFAULT 'n',
  `title` varchar(255) NOT NULL,
  `body` text NOT NULL,
  `post_date` datetime NOT NULL,
  `author_id` int(10) NOT NULL,
  `category_id` int(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` mediumint(8) UNSIGNED NOT NULL,
  `ip_address` varbinary(16) NOT NULL,
  `username` varchar(100) NOT NULL,
  `password` varchar(80) NOT NULL,
  `salt` varchar(40) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `activation_code` varchar(40) DEFAULT NULL,
  `forgotten_password_code` varchar(40) DEFAULT NULL,
  `forgotten_password_time` int(11) UNSIGNED DEFAULT NULL,
  `remember_code` varchar(40) DEFAULT NULL,
  `created_on` int(11) UNSIGNED NOT NULL,
  `last_login` int(11) UNSIGNED DEFAULT NULL,
  `active` tinyint(1) UNSIGNED DEFAULT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `affiliation` varchar(50) NOT NULL,
  `country` varchar(50) NOT NULL,
  `image` varchar(128) DEFAULT NULL,
  `bio` text NOT NULL,
  `core` enum('true','false') NOT NULL DEFAULT 'false',
  `external_source` varchar(50) DEFAULT NULL,
  `external_id` varchar(50) DEFAULT NULL,
  `session_hash` varchar(40) DEFAULT NULL,
  `session_hash_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `gamification_visibility` varchar(32) NOT NULL DEFAULT 'show'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `users_groups`
--

CREATE TABLE `users_groups` (
  `id` mediumint(8) UNSIGNED NOT NULL,
  `user_id` mediumint(8) UNSIGNED NOT NULL,
  `group_id` mediumint(8) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `vipercharts`
--

CREATE TABLE `vipercharts` (
  `id` int(10) NOT NULL,
  `run_id` int(10) NOT NULL,
  `class` varchar(255) NOT NULL,
  `viper_id` varchar(255) NOT NULL,
  `activated` datetime NOT NULL,
  `inactived` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `access`
--
ALTER TABLE `access`
  ADD PRIMARY KEY (`user_id`,`group`,`type`,`entity_id`);

--
-- Indexes for table `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `file`
--
ALTER TABLE `file`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `groups`
--
ALTER TABLE `groups`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `login_attempts`
--
ALTER TABLE `login_attempts`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `meta_dataset`
--
ALTER TABLE `meta_dataset`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `thread`
--
ALTER TABLE `thread`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `session_hash` (`session_hash`);

--
-- Indexes for table `users_groups`
--
ALTER TABLE `users_groups`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `vipercharts`
--
ALTER TABLE `vipercharts`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `category`
--
ALTER TABLE `category`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `file`
--
ALTER TABLE `file`
  MODIFY `id` int(16) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `groups`
--
ALTER TABLE `groups`
  MODIFY `id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `login_attempts`
--
ALTER TABLE `login_attempts`
  MODIFY `id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `meta_dataset`
--
ALTER TABLE `meta_dataset`
  MODIFY `id` int(8) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `thread`
--
ALTER TABLE `thread`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `users_groups`
--
ALTER TABLE `users_groups`
  MODIFY `id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `vipercharts`
--
ALTER TABLE `vipercharts`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
