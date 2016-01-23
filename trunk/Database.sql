-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.6.26-log - MySQL Community Server (GPL)
-- Server OS:                    Win64
-- HeidiSQL Version:             9.3.0.4984
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
  `userId` int(11) NOT NULL,
  `currentBalance` bigint(20) unsigned NOT NULL,
  `version` int(10) unsigned NOT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `userId` (`userId`),
  CONSTRAINT `FK_balance_user` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.callbackcode
CREATE TABLE IF NOT EXISTS `callbackcode` (
  `callbackCodeId` INT(11)                                              NOT NULL AUTO_INCREMENT,
  `userId`         INT(11)                                              NOT NULL,
  `code`           VARCHAR(50)                                          NOT NULL,
  `type`           ENUM('VALIDATION', 'PASSWORD_RESET', 'EMAIL_CHANGE') NOT NULL,
  `version`        INT(10) UNSIGNED                                     NOT NULL,
  `created`        TIMESTAMP                                            NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated`        TIMESTAMP                                            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`callbackCodeId`),
  KEY `FK_account-validation_user` (`userId`),
  CONSTRAINT `FK_account-validation_user` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.coinbaseresponse
CREATE TABLE IF NOT EXISTS `coinbaseresponse` (
  `coinbaseResponseId` INT(11)          NOT NULL AUTO_INCREMENT,
  `responseText`       TEXT             NOT NULL,
  `processed`          TINYINT(4)                DEFAULT NULL,
  `version`            INT(10) UNSIGNED NOT NULL,
  `updated`            TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created`            TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`coinbaseResponseId`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.comment
CREATE TABLE IF NOT EXISTS `comment` (
  `commentId`       INT(10)          NOT NULL AUTO_INCREMENT,
  `authorId`        INT(10)          NOT NULL,
  `postId`          INT(10)          NOT NULL,
  `parentCommentId` INT(10),
  `trashed`         TINYINT(4)       NOT NULL DEFAULT '0',
  `deleted`         TINYINT(4)       NOT NULL DEFAULT '0',
  `depth`           INT(10) UNSIGNED NOT NULL,
  `points`          INT(10) UNSIGNED NOT NULL,
  `donations`       INT(10) UNSIGNED NOT NULL,
  `base`            DOUBLE UNSIGNED  NOT NULL,
  `score`           DOUBLE UNSIGNED  NOT NULL,
  `content`         VARCHAR(10000)   NOT NULL,
  `version`         INT(10) UNSIGNED NOT NULL,
  `updated`         TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created`         TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`commentId`),
  KEY `FK_comment_user` (`authorId`),
  KEY `FK_comment_post` (`postId`),
  KEY `FK_comment_comment` (`parentCommentId`),
  CONSTRAINT `FK_comment_comment` FOREIGN KEY (`parentCommentId`) REFERENCES `comment` (`commentId`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `FK_comment_post` FOREIGN KEY (`postId`) REFERENCES `post` (`postId`),
  CONSTRAINT `FK_comment_user` FOREIGN KEY (`authorId`) REFERENCES `user` (`userId`)
    ON UPDATE CASCADE
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.commentcreation
CREATE TABLE IF NOT EXISTS `commentcreation` (
  `commentId`        INT(11)                                                     NOT NULL AUTO_INCREMENT,
  `transactionLogId` INT(10) UNSIGNED                                                     DEFAULT NULL,
  `feeFlat`          INT(10) UNSIGNED                                            NOT NULL,
  `feePercent`       SMALLINT(5) UNSIGNED                                        NOT NULL,
  `state`            ENUM('UNCONFIRMED', 'UNPROCESSED', 'PROCESSED', 'CANCELED') NOT NULL,
  `version`          INT(10) UNSIGNED                                            NOT NULL,
  `updated`          TIMESTAMP                                                   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created`          TIMESTAMP                                                   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`commentId`),
  KEY `FK_commentcreation_transactionlog` (`transactionLogId`),
  CONSTRAINT `FK_commentcreation_comment` FOREIGN KEY (`commentId`) REFERENCES `comment` (`commentId`),
  CONSTRAINT `FK_commentcreation_transactionlog` FOREIGN KEY (`transactionLogId`) REFERENCES `transactionlog` (`transactionLogId`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.commentdonation
CREATE TABLE IF NOT EXISTS `commentdonation` (
  `commentDonationId` INT(11) UNSIGNED                                            NOT NULL AUTO_INCREMENT,
  `commentId`         INT(11)                                                     NOT NULL,
  `sourceUserId`      INT(11)                                                     NOT NULL,
  `transactionLogId`  INT(10) UNSIGNED                                                     DEFAULT NULL,
  `amount`            INT(11)                                                     NOT NULL,
  `state`             ENUM('UNCONFIRMED', 'UNPROCESSED', 'PROCESSED', 'CANCELED') NOT NULL,
  `version`           INT(10) UNSIGNED                                            NOT NULL,
  `updated`           TIMESTAMP                                                   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created`           TIMESTAMP                                                   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`commentDonationId`),
  KEY `FK_commenttransaction_comment` (`commentId`),
  KEY `FK_commenttransaction_user` (`sourceUserId`),
  KEY `FK_commentdonation_transactionlog` (`transactionLogId`),
  CONSTRAINT `FK_commentdonation_transactionlog` FOREIGN KEY (`transactionLogId`) REFERENCES `transactionlog` (`transactionLogId`),
  CONSTRAINT `FK_commenttransaction_comment` FOREIGN KEY (`commentId`) REFERENCES `comment` (`commentId`)
    ON UPDATE CASCADE,
  CONSTRAINT `FK_commenttransaction_user` FOREIGN KEY (`sourceUserId`) REFERENCES `user` (`userId`)
    ON UPDATE CASCADE
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.commenttrial
CREATE TABLE IF NOT EXISTS `commenttrial` (
  `commentId`             INT(10)                                NOT NULL,
  `reporterId`            INT(11)                                NOT NULL,
  `totalVotes`            TINYINT(3) UNSIGNED                    NOT NULL,
  `violatedSiteRuleVotes` TINYINT(3) UNSIGNED                    NOT NULL,
  `isIllegalVotes`        TINYINT(3) UNSIGNED                    NOT NULL,
  `state`                 ENUM('ON_TRIAL', 'INNOCENT', 'GUILTY') NOT NULL,
  `version`               INT(10) UNSIGNED                       NOT NULL,
  `updated`               TIMESTAMP                              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created`               TIMESTAMP                              NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`commentId`),
  KEY `FK_posttrial_user` (`reporterId`),
  CONSTRAINT `commenttrial_ibfk_1` FOREIGN KEY (`commentId`) REFERENCES `comment` (`commentId`),
  CONSTRAINT `commenttrial_ibfk_2` FOREIGN KEY (`reporterId`) REFERENCES `user` (`userId`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8
  ROW_FORMAT = COMPACT;

-- Data exporting was unselected.


-- Dumping structure for table wymi.commenttrialjuror
CREATE TABLE IF NOT EXISTS `commenttrialjuror` (
  `commentTrialJurorId`  INT(11)          NOT NULL    AUTO_INCREMENT,
  `userId`               INT(10)          NOT NULL,
  `commentId`            INT(10)          NOT NULL,
  `violatedSiteRuleVote` TINYINT(3) UNSIGNED          DEFAULT NULL,
  `isIllegalVote`        TINYINT(3) UNSIGNED          DEFAULT NULL,
  `version`              INT(10) UNSIGNED NOT NULL,
  `updated`              TIMESTAMP        NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created`              TIMESTAMP        NOT NULL    DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`commentTrialJurorId`),
  KEY `userId_postId` (`userId`, `commentId`),
  KEY `FK_posttrialjuror_posttrial` (`commentId`),
  CONSTRAINT `commenttrialjuror_ibfk_1` FOREIGN KEY (`commentId`) REFERENCES `commenttrial` (`commentId`),
  CONSTRAINT `commenttrialjuror_ibfk_2` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8
  ROW_FORMAT = COMPACT;

-- Data exporting was unselected.


-- Dumping structure for table wymi.email
CREATE TABLE IF NOT EXISTS `email` (
  `emailId`  INT(10) UNSIGNED    NOT NULL AUTO_INCREMENT,
  `address`  VARCHAR(50)         NOT NULL,
  `subject`  VARCHAR(255)        NOT NULL,
  `body`     VARCHAR(15000)      NOT NULL,
  `sent`     TINYINT(3) UNSIGNED NOT NULL,
  `sentDate` TIMESTAMP           NULL     DEFAULT NULL,
  `version`  INT(10) UNSIGNED    NOT NULL,
  `updated`  TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created`  TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`emailId`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.filters
CREATE TABLE IF NOT EXISTS `filters` (
  `userId`  INT(11) DEFAULT NULL,
  `topicId` INT(11) DEFAULT NULL,
  KEY `FK_filters_user` (`userId`),
  KEY `FK_filters_topic` (`topicId`),
  CONSTRAINT `FK_filters_topic` FOREIGN KEY (`topicId`) REFERENCES `topic` (`topicId`),
  CONSTRAINT `FK_filters_user` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.message
CREATE TABLE IF NOT EXISTS `message` (
  `messageId`          INT(10)        NOT NULL AUTO_INCREMENT,
  `destinationUserId`  INT(10)        NOT NULL,
  `sourceUserId`       INT(10)                 DEFAULT NULL,
  `subject`            VARCHAR(255)   NOT NULL,
  `content`            VARCHAR(15000) NOT NULL,
  `alreadyRead`        TINYINT(4)              DEFAULT '0',
  `destinationDeleted` TINYINT(4)              DEFAULT '0',
  `sourceDeleted`      TINYINT(4)              DEFAULT '0',
  `version`            INT(10) UNSIGNED        DEFAULT '0',
  `updated`            TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created`            TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`messageId`),
  KEY `FK_message_user` (`destinationUserId`),
  KEY `FK_message_user_2` (`sourceUserId`),
  CONSTRAINT `FK_message_user` FOREIGN KEY (`destinationUserId`) REFERENCES `user` (`userId`)
    ON UPDATE CASCADE,
  CONSTRAINT `FK_message_user_2` FOREIGN KEY (`sourceUserId`) REFERENCES `user` (`userId`)
    ON UPDATE CASCADE
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.ownershiptransaction
CREATE TABLE IF NOT EXISTS `ownershiptransaction` (
  `ownershipTransactionId`  INT(10) UNSIGNED             NOT NULL AUTO_INCREMENT,
  `topicId`                 INT(11)                      NOT NULL,
  `winningBidId`            INT(10) UNSIGNED                      DEFAULT NULL,
  `state`                   ENUM('WAITING', 'PROCESSED') NOT NULL,
  `waitingPeriodExpiration` DATETIME                     NOT NULL,
  `version`                 INT(10) UNSIGNED             NOT NULL,
  `updated`                 TIMESTAMP                    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created`                 TIMESTAMP                    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ownershipTransactionId`),
  KEY `FK_ownershiptransaction_topicbid` (`winningBidId`),
  KEY `FK_ownershiptransaction_topic` (`topicId`),
  CONSTRAINT `FK_ownershiptransaction_topic` FOREIGN KEY (`topicId`) REFERENCES `topic` (`topicId`),
  CONSTRAINT `FK_ownershiptransaction_topicbid` FOREIGN KEY (`winningBidId`) REFERENCES `topicbid` (`topicBidId`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.post
CREATE TABLE IF NOT EXISTS `post` (
  `postId`       INT(11)             NOT NULL AUTO_INCREMENT,
  `topicId`      INT(11)             NOT NULL,
  `userId`       INT(11)             NOT NULL,
  `title`        VARCHAR(255)        NOT NULL,
  `deletedTitle` VARCHAR(255)                 DEFAULT NULL,
  `href`         VARCHAR(1000)       NOT NULL,
  `deletedHref`  VARCHAR(1000)                DEFAULT NULL,
  `text`         TEXT                NOT NULL,
  `deletedText`  TEXT,
  `isText`       TINYINT(4)          NOT NULL,
  `points`       BIGINT(20) UNSIGNED NOT NULL,
  `donations`    INT(10) UNSIGNED    NOT NULL,
  `base`         DOUBLE UNSIGNED     NOT NULL,
  `score`        DOUBLE UNSIGNED     NOT NULL,
  `trashed`      TINYINT(3) UNSIGNED NOT NULL DEFAULT '0',
  `deleted`      TINYINT(3) UNSIGNED NOT NULL DEFAULT '0',
  `version`      INT(10) UNSIGNED    NOT NULL,
  `updated`      TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created`      TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`postId`),
  KEY `FK_post_topic` (`topicId`),
  KEY `FK_post_user` (`userId`),
  CONSTRAINT `FK_post_topic` FOREIGN KEY (`topicId`) REFERENCES `topic` (`topicId`)
    ON UPDATE CASCADE,
  CONSTRAINT `FK_post_user` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
    ON UPDATE CASCADE
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.postcreation
CREATE TABLE IF NOT EXISTS `postcreation` (
  `postId`           INT(11)                                                     NOT NULL AUTO_INCREMENT,
  `transactionLogId` INT(10) UNSIGNED                                                     DEFAULT NULL,
  `feeFlat`          BIGINT(20) UNSIGNED                                         NOT NULL,
  `feePercent`       SMALLINT(5) UNSIGNED                                        NOT NULL,
  `state`            ENUM('UNCONFIRMED', 'UNPROCESSED', 'PROCESSED', 'CANCELED') NOT NULL DEFAULT 'UNPROCESSED',
  `version`          INT(10) UNSIGNED                                            NOT NULL,
  `updated`          TIMESTAMP                                                   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created`          TIMESTAMP                                                   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`postId`),
  KEY `FK_postcreation_transactionlog` (`transactionLogId`),
  CONSTRAINT `FK_postcreation_post` FOREIGN KEY (`postId`) REFERENCES `post` (`postId`),
  CONSTRAINT `FK_postcreation_transactionlog` FOREIGN KEY (`transactionLogId`) REFERENCES `transactionlog` (`transactionLogId`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.postdonation
CREATE TABLE IF NOT EXISTS `postdonation` (
  `postDonationId`   INT(11) UNSIGNED                                            NOT NULL AUTO_INCREMENT,
  `postId`           INT(11)                                                     NOT NULL,
  `sourceUserId`     INT(11)                                                     NOT NULL,
  `transactionLogId` INT(10) UNSIGNED                                                     DEFAULT NULL,
  `amount`           BIGINT(20)                                                  NOT NULL,
  `state`            ENUM('UNCONFIRMED', 'UNPROCESSED', 'PROCESSED', 'CANCELED') NOT NULL DEFAULT 'UNPROCESSED',
  `version`          INT(10) UNSIGNED                                            NOT NULL,
  `updated`          TIMESTAMP                                                   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created`          TIMESTAMP                                                   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`postDonationId`),
  KEY `FK_posttransaction_user` (`sourceUserId`),
  KEY `FK_posttransaction_post` (`postId`),
  KEY `FK_postdonation_transactionlog` (`transactionLogId`),
  CONSTRAINT `FK_postdonation_transactionlog` FOREIGN KEY (`transactionLogId`) REFERENCES `transactionlog` (`transactionLogId`),
  CONSTRAINT `FK_posttransaction_post` FOREIGN KEY (`postId`) REFERENCES `post` (`postId`)
    ON UPDATE CASCADE,
  CONSTRAINT `FK_posttransaction_user` FOREIGN KEY (`sourceUserId`) REFERENCES `user` (`userId`)
    ON UPDATE CASCADE
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.posttrial
CREATE TABLE IF NOT EXISTS `posttrial` (
  `postId`                INT(10)                                NOT NULL,
  `reporterId`            INT(11)                                NOT NULL,
  `totalVotes`            TINYINT(3) UNSIGNED                    NOT NULL,
  `violatedSiteRuleVotes` TINYINT(3) UNSIGNED                    NOT NULL,
  `isIllegalVotes`        TINYINT(3) UNSIGNED                    NOT NULL,
  `state`                 ENUM('ON_TRIAL', 'INNOCENT', 'GUILTY') NOT NULL,
  `version`               INT(10) UNSIGNED                       NOT NULL,
  `updated`               TIMESTAMP                              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created`               TIMESTAMP                              NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`postId`),
  KEY `FK_posttrial_user` (`reporterId`),
  CONSTRAINT `FK_posttrial_post` FOREIGN KEY (`postId`) REFERENCES `post` (`postId`),
  CONSTRAINT `FK_posttrial_user` FOREIGN KEY (`reporterId`) REFERENCES `user` (`userId`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.posttrialjuror
CREATE TABLE IF NOT EXISTS `posttrialjuror` (
  `postTrialJurorId`     INT(11)          NOT NULL    AUTO_INCREMENT,
  `userId`               INT(10)          NOT NULL,
  `postId`               INT(10)          NOT NULL,
  `violatedSiteRuleVote` TINYINT(3) UNSIGNED          DEFAULT NULL,
  `isIllegalVote`        TINYINT(3) UNSIGNED          DEFAULT NULL,
  `version`              INT(10) UNSIGNED NOT NULL,
  `updated`              TIMESTAMP        NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created`              TIMESTAMP        NOT NULL    DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`postTrialJurorId`),
  KEY `userId_postId` (`userId`, `postId`),
  KEY `FK_posttrialjuror_posttrial` (`postId`),
  CONSTRAINT `FK_posttrialjuror_posttrial` FOREIGN KEY (`postId`) REFERENCES `posttrial` (`postId`),
  CONSTRAINT `FK_posttrialjuror_user` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.subscription
CREATE TABLE IF NOT EXISTS `subscription` (
  `userId`  INT(11) DEFAULT NULL,
  `topicId` INT(11) DEFAULT NULL,
  KEY `FK_subscription_user` (`userId`),
  KEY `FK_subscription_topic` (`topicId`),
  CONSTRAINT `FK_subscription_topic` FOREIGN KEY (`topicId`) REFERENCES `topic` (`topicId`),
  CONSTRAINT `FK_subscription_user` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.topic
CREATE TABLE IF NOT EXISTS `topic` (
  `topicId` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `owner`           INT(11)              NOT NULL,
  `feeFlat`         INT(11) UNSIGNED     NOT NULL DEFAULT '0',
  `feePercent`      SMALLINT(5) UNSIGNED NOT NULL DEFAULT '0',
  `rent`            BIGINT(20) UNSIGNED  NOT NULL DEFAULT '0',
  `rentDueDate`     TIMESTAMP            NOT NULL DEFAULT '0000-00-00 00:00:00',
  `subscriberCount` INT(11) UNSIGNED     NOT NULL DEFAULT '0',
  `filterCount`     INT(11) UNSIGNED     NOT NULL DEFAULT '0',
  `title`           VARCHAR(100)         NOT NULL,
  `description`     VARCHAR(500)         NOT NULL,
  `version` int(10) unsigned NOT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`topicId`),
  UNIQUE KEY `Column 2` (`name`),
  KEY `FK__user` (`owner`),
  CONSTRAINT `FK__user` FOREIGN KEY (`owner`) REFERENCES `user` (`userId`) ON UPDATE CASCADE
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.topicbid
CREATE TABLE IF NOT EXISTS `topicbid` (
  `topicBidId`     INT(10) UNSIGNED                         NOT NULL AUTO_INCREMENT,
  `topicId`        INT(10)                                  NOT NULL,
  `userId`         INT(10)                                  NOT NULL,
  `currentBalance` BIGINT(20) UNSIGNED                      NOT NULL,
  `state`          ENUM('WAITING', 'ACCEPTED', 'PROCESSED') NOT NULL,
  `version`        INT(10) UNSIGNED                         NOT NULL,
  `updated`        TIMESTAMP                                NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created`        TIMESTAMP                                NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`topicBidId`),
  KEY `FK_bid_topic` (`topicId`),
  KEY `FK_bid_user` (`userId`),
  CONSTRAINT `FK_bid_topic` FOREIGN KEY (`topicId`) REFERENCES `topic` (`topicId`),
  CONSTRAINT `FK_bid_user` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.topicbidcreation
CREATE TABLE IF NOT EXISTS `topicbidcreation` (
  `topicBidId`       INT(10) UNSIGNED                                            NOT NULL,
  `transactionLogId` INT(10) UNSIGNED                                                     DEFAULT NULL,
  `amount`           BIGINT(20) UNSIGNED                                         NOT NULL,
  `state`            ENUM('UNCONFIRMED', 'UNPROCESSED', 'PROCESSED', 'CANCELED') NOT NULL,
  `version`          INT(10) UNSIGNED                                            NOT NULL,
  `updated`          TIMESTAMP                                                   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created`          TIMESTAMP                                                   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY `FK_topicbidcreation_topicbid` (`topicBidId`),
  KEY `FK_topicbidcreation_transactionlog` (`transactionLogId`),
  CONSTRAINT `FK_topicbidcreation_topicbid` FOREIGN KEY (`topicBidId`) REFERENCES `topicbid` (`topicBidId`),
  CONSTRAINT `FK_topicbidcreation_transactionlog` FOREIGN KEY (`transactionLogId`) REFERENCES `transactionlog` (`transactionLogId`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.topicbiddispersion
CREATE TABLE IF NOT EXISTS `topicbiddispersion` (
  `topicBidDispersionId` INT(10) UNSIGNED                                            NOT NULL AUTO_INCREMENT,
  `destinationUserId`    INT(11)                                                     NOT NULL,
  `topicBidId`           INT(11) UNSIGNED                                            NOT NULL,
  `amount`               BIGINT(20) UNSIGNED                                         NOT NULL,
  `transactionLogId`     INT(10) UNSIGNED                                                     DEFAULT NULL,
  `state`                ENUM('UNCONFIRMED', 'UNPROCESSED', 'PROCESSED', 'CANCELED') NOT NULL,
  `version`              INT(10) UNSIGNED                                            NOT NULL,
  `updated`              TIMESTAMP                                                   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created`              TIMESTAMP                                                   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`topicBidDispersionId`),
  KEY `FK_topicbiddispersion_transactionlog` (`transactionLogId`),
  KEY `FK_topicbiddispersion_user` (`destinationUserId`),
  KEY `FK_topicbiddispersion_topicbid` (`topicBidId`),
  CONSTRAINT `FK_topicbiddispersion_topicbid` FOREIGN KEY (`topicBidId`) REFERENCES `topicbid` (`topicBidId`),
  CONSTRAINT `FK_topicbiddispersion_transactionlog` FOREIGN KEY (`transactionLogId`) REFERENCES `transactionlog` (`transactionLogId`),
  CONSTRAINT `FK_topicbiddispersion_user` FOREIGN KEY (`destinationUserId`) REFERENCES `user` (`userId`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.transactionlog
CREATE TABLE IF NOT EXISTS `transactionlog` (
  `transactionLogId`    INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `transactionId`       INT(11)          NOT NULL,
  `transactionClass`    VARCHAR(255)     NOT NULL,
  `amountPayed`         INT(10) UNSIGNED NOT NULL,
  `destinationReceived` INT(10) UNSIGNED NOT NULL,
  `taxerReceived`       INT(10) UNSIGNED NOT NULL,
  `siteReceived`        INT(10) UNSIGNED NOT NULL,
  `targetReceived`      INT(10) UNSIGNED          DEFAULT NULL,
  `canceled`            TINYINT(4)       NOT NULL DEFAULT '0',
  `version`             INT(10) UNSIGNED NOT NULL,
  `updated`             TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created`             TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`transactionLogId`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.transfertransaction
CREATE TABLE IF NOT EXISTS `transfertransaction` (
  `transferTransactionId` INT(10) UNSIGNED                                            NOT NULL AUTO_INCREMENT,
  `sourceUserId`          INT(10)                                                     NOT NULL DEFAULT '0',
  `destinationUserId`     INT(10)                                                     NOT NULL DEFAULT '0',
  `transactionLogId`      INT(10) UNSIGNED                                                     DEFAULT NULL,
  `amount`                BIGINT(20) UNSIGNED                                         NOT NULL DEFAULT '0',
  `state`                 ENUM('UNCONFIRMED', 'UNPROCESSED', 'PROCESSED', 'CANCELED') NOT NULL DEFAULT 'UNPROCESSED',
  `version`               INT(10) UNSIGNED                                            NOT NULL,
  `updated`               TIMESTAMP                                                   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created`               TIMESTAMP                                                   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`transferTransactionId`),
  KEY `FK_transfertransaction_user` (`sourceUserId`),
  KEY `FK_transfertransaction_user_2` (`destinationUserId`),
  KEY `FK_transfertransaction_transactionlog` (`transactionLogId`),
  CONSTRAINT `FK_transfertransaction_transactionlog` FOREIGN KEY (`transactionLogId`) REFERENCES `transactionlog` (`transactionLogId`),
  CONSTRAINT `FK_transfertransaction_user` FOREIGN KEY (`sourceUserId`) REFERENCES `user` (`userId`),
  CONSTRAINT `FK_transfertransaction_user_2` FOREIGN KEY (`destinationUserId`) REFERENCES `user` (`userId`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

-- Data exporting was unselected.


-- Dumping structure for table wymi.user
CREATE TABLE IF NOT EXISTS `user` (
  `userId` int(10) NOT NULL AUTO_INCREMENT,
  `email` varchar(50) NOT NULL,
  `newEmail`     VARCHAR(50)                  DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `password` varchar(1024) NOT NULL,
  `roles` varchar(1024) NOT NULL,
  `validated` tinyint(4) DEFAULT '0',
  `willingJuror` TINYINT(3) UNSIGNED NOT NULL DEFAULT '1',
  `lastJurored`  TIMESTAMP           NULL     DEFAULT NULL,
  `version` int(10) unsigned NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `newEmail` (`newEmail`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =latin1
  ROW_FORMAT = COMPACT;

-- Data exporting was unselected.


-- Dumping structure for table wymi.usertopicrank
CREATE TABLE IF NOT EXISTS `usertopicrank` (
  `userId`  INT(11)          NOT NULL,
  `topicId` INT(11)          NOT NULL,
  `rank`    FLOAT            NOT NULL,
  `version` INT(10) UNSIGNED NOT NULL,
  `created` TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated` TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`userId`, `topicId`),
  UNIQUE KEY `userId_topicId` (`userId`, `topicId`),
  KEY `FK_usertopicrank_topic` (`topicId`),
  CONSTRAINT `FK_usertopicrank_topic` FOREIGN KEY (`topicId`) REFERENCES `topic` (`topicId`),
  CONSTRAINT `FK_usertopicrank_user` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

-- Data exporting was unselected.


-- Dumping structure for view wymi.userwithbalance
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `userwithbalance` (
  `userId`         INT(10)             NULL,
  `name`           VARCHAR(50)         NULL COLLATE 'latin1_swedish_ci',
  `currentBalance` BIGINT(20) UNSIGNED NOT NULL
)
  ENGINE =MyISAM;


-- Dumping structure for view wymi.userwithbalance
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `userwithbalance`;
CREATE ALGORITHM = UNDEFINED
  DEFINER =`root`@`localhost` VIEW `userwithbalance` AS
  SELECT
    user.userId,
    name,
    currentBalance
  FROM balance
    LEFT JOIN user ON balance.userId = user.userId;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
