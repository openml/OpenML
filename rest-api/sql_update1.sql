-- phpMyAdmin SQL Dump
-- version 3.5.1
-- http://www.phpmyadmin.net
--
-- Machine: localhost
-- Genereertijd: 14 dec 2012 om 11:28
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

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
