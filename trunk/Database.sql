-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.6.26-log - MySQL Community Server (GPL)
-- Server OS:                    Win64
-- HeidiSQL Version:             9.2.0.4947
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Dumping database structure for wymi
CREATE DATABASE IF NOT EXISTS `wymi` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `wymi`;


-- Dumping structure for table wymi.balance
CREATE TABLE IF NOT EXISTS `balance` (
  `balanceId` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `balance` bigint(20) unsigned NOT NULL,
  `version` int(11) unsigned NOT NULL DEFAULT '0',
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`balanceId`),
  UNIQUE KEY `userId` (`userId`),
  CONSTRAINT `FK_balance_user` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.callbackcode
CREATE TABLE IF NOT EXISTS `callbackcode` (
  `callbackCodeId` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `code` varchar(50) NOT NULL,
  `type` enum('VALIDATION','PASSWORD_RESET') NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`callbackCodeId`),
  KEY `FK_account-validation_user` (`userId`),
  CONSTRAINT `FK_account-validation_user` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.message
CREATE TABLE IF NOT EXISTS `message` (
  `messageId` int(10) NOT NULL AUTO_INCREMENT,
  `destinationUserId` int(10) NOT NULL,
  `sourceUserId` int(10) DEFAULT NULL,
  `subject` varchar(255) NOT NULL,
  `content` varchar(15000) NOT NULL,
  `alreadyRead` tinyint(4) DEFAULT '0',
  `destinationDeleted` tinyint(4) DEFAULT '0',
  `sourceDeleted` tinyint(4) DEFAULT '0',
  `version` int(10) DEFAULT '0',
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`messageId`),
  KEY `FK_message_user` (`destinationUserId`),
  KEY `FK_message_user_2` (`sourceUserId`),
  CONSTRAINT `FK_message_user` FOREIGN KEY (`destinationUserId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_message_user_2` FOREIGN KEY (`sourceUserId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.post
CREATE TABLE IF NOT EXISTS `post` (
  `postId` int(11) NOT NULL AUTO_INCREMENT,
  `topicId` int(11) NOT NULL,
  `userId` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `url` varchar(1000) NOT NULL,
  `text` varchar(15000) NOT NULL,
  `isText` tinyint(4) NOT NULL,
  `points` bigint(20) NOT NULL DEFAULT '0',
  `score` double NOT NULL,
  `version` int(11) NOT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`postId`),
  KEY `FK_post_user` (`userId`),
  KEY `FK_post_topic` (`topicId`),
  CONSTRAINT `FK_post_topic` FOREIGN KEY (`topicId`) REFERENCES `topic` (`topicId`),
  CONSTRAINT `FK_post_user` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.posttransaction
CREATE TABLE IF NOT EXISTS `posttransaction` (
  `postTransactionId` int(11) NOT NULL AUTO_INCREMENT,
  `postId` int(11) NOT NULL,
  `sourceUserId` int(11) NOT NULL,
  `amount` bigint(20) NOT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `state` enum('UNPROCESSED','PROCESSED','CANCELED') NOT NULL DEFAULT 'UNPROCESSED',
  PRIMARY KEY (`postTransactionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.topic
CREATE TABLE IF NOT EXISTS `topic` (
  `topicId` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `owner` int(11) DEFAULT NULL,
  `rent` bigint(20) NOT NULL DEFAULT '0',
  `rentDueDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `subscribers` int(11) NOT NULL DEFAULT '0',
  `unsubscribers` int(11) NOT NULL DEFAULT '0',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`topicId`),
  UNIQUE KEY `Column 2` (`name`),
  KEY `FK__user` (`owner`),
  CONSTRAINT `FK__user` FOREIGN KEY (`owner`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.user
CREATE TABLE IF NOT EXISTS `user` (
  `userId` int(10) NOT NULL AUTO_INCREMENT,
  `email` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `password` varchar(1024) NOT NULL,
  `roles` varchar(1024) NOT NULL,
  `validated` tinyint(4) DEFAULT '0',
  `version` int(10) unsigned NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
