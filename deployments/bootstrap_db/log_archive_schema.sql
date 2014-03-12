-- MySQL dump 10.13  Distrib 5.5.34, for Linux (x86_64)
--
-- Host: localhost    Database: log_archive
-- ------------------------------------------------------
-- Server version	5.5.34-32.0-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `log_accounts`
--

DROP TABLE IF EXISTS `log_accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `log_accounts` (
  `logID` bigint(20) NOT NULL,
  `type` enum('Contractor','Operator','Admin','Corporate','Assessment') CHARACTER SET latin1 NOT NULL DEFAULT 'Contractor',
  `name` varchar(50) DEFAULT NULL,
  `createdBy` int(11) DEFAULT NULL,
  `creationDate` datetime DEFAULT NULL,
  `updatedBy` int(11) DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL,
  `deactivationDate` datetime DEFAULT NULL,
  `deactivatedBy` int(11) DEFAULT NULL,
  `status` varchar(15) CHARACTER SET latin1 NOT NULL DEFAULT 'Pending',
  `address` varchar(50) DEFAULT NULL,
  `address2` varchar(50) DEFAULT NULL,
  `address3` varchar(50) DEFAULT NULL,
  `city` varchar(35) DEFAULT NULL,
  `countrySubdivision` varchar(10) CHARACTER SET latin1 DEFAULT NULL,
  `zip` varchar(15) CHARACTER SET latin1 DEFAULT NULL,
  `country` varchar(25) CHARACTER SET latin1 DEFAULT NULL,
  `phone` varchar(30) CHARACTER SET latin1 DEFAULT NULL,
  `phone2` varchar(35) CHARACTER SET latin1 DEFAULT NULL,
  `fax` varchar(30) CHARACTER SET latin1 DEFAULT NULL,
  `contactID` mediumint(9) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `web_URL` varchar(50) DEFAULT NULL,
  `mainTradeID` int(11) DEFAULT NULL,
  `industryID` int(11) DEFAULT NULL,
  `industry` varchar(50) CHARACTER SET latin1 DEFAULT NULL,
  `naics` varchar(10) CHARACTER SET latin1 NOT NULL DEFAULT '0',
  `naicsValid` tinyint(4) NOT NULL DEFAULT '0',
  `dbaName` varchar(400) DEFAULT NULL,
  `nameIndex` varchar(50) DEFAULT NULL,
  `reason` varchar(100) DEFAULT NULL,
  `acceptsBids` tinyint(4) NOT NULL DEFAULT '0',
  `description` text,
  `requiresOQ` tinyint(4) unsigned NOT NULL DEFAULT '0',
  `requiresCompetencyReview` tinyint(4) unsigned NOT NULL DEFAULT '0',
  `needsIndexing` tinyint(4) unsigned NOT NULL DEFAULT '1',
  `onsiteServices` tinyint(4) unsigned NOT NULL DEFAULT '0',
  `transportationServices` tinyint(4) unsigned NOT NULL DEFAULT '0',
  `offsiteServices` tinyint(4) unsigned NOT NULL DEFAULT '0',
  `materialSupplier` tinyint(4) unsigned NOT NULL DEFAULT '0',
  `generalContractor` tinyint(4) NOT NULL DEFAULT '0',
  `autoApproveRelationships` tinyint(4) NOT NULL DEFAULT '1',
  `accreditation` date DEFAULT NULL,
  `parentID` int(11) DEFAULT NULL,
  `currencyCode` char(3) CHARACTER SET latin1 DEFAULT 'USD',
  `qbListID` varchar(25) CHARACTER SET latin1 DEFAULT NULL,
  `qbListCAID` varchar(25) CHARACTER SET latin1 DEFAULT NULL,
  `qbListUKID` varchar(25) CHARACTER SET latin1 DEFAULT NULL,
  `qbListEUID` varchar(25) CHARACTER SET latin1 DEFAULT NULL,
  `qbSync` tinyint(4) NOT NULL DEFAULT '1',
  `sapLastSync` datetime DEFAULT NULL,
  `sapSync` tinyint(1) DEFAULT '0',
  `locale` varchar(5) CHARACTER SET latin1 DEFAULT 'en',
  `timezone` varchar(50) CHARACTER SET latin1 DEFAULT NULL,
  `rememberMeTime` tinyint(4) DEFAULT '-1',
  `sessionTimeout` tinyint(3) unsigned DEFAULT '60',
  `rememberMeTimeEnabled` tinyint(4) DEFAULT '1',
  `passwordSecurityLevelId` tinyint(4) DEFAULT '0'
) ENGINE=ARCHIVE DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `log_event`
--

DROP TABLE IF EXISTS `log_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `log_event` (
  `logDate` bigint(20) NOT NULL COMMENT 'Log transaction date in UNIX epoch format.',
  `validStart` date NOT NULL COMMENT 'Start date this fact was valid in reality.',
  `validFinish` date NOT NULL COMMENT 'Finish date this fact was valid in reality.',
  `dmlType` enum('INSERT','UPDATE','DELETE','VIEW') DEFAULT NULL COMMENT 'Type of data manipulation (INSERT, UPDATE, DELETE, VIEW).',
  `ddlName` varchar(128) DEFAULT NULL COMMENT 'Name of data definition (table) being logged.',
  `ddlKey` int(11) NOT NULL COMMENT 'The primary key value of the table being logged (PK from ddlName).',
  `logSeq` int(11) DEFAULT NULL COMMENT 'The log sequence number for contiguous history.',
  `userName` varchar(128) DEFAULT NULL COMMENT 'The user logging the changes.',
  `logYear` int(11) DEFAULT NULL COMMENT 'Log transaction date year.',
  `logMonth` int(11) DEFAULT NULL COMMENT 'Log transaction date month.',
  `logWeek` int(11) DEFAULT NULL COMMENT 'Log transaction date week.',
  `logDay` int(11) DEFAULT NULL COMMENT 'Log transaction date day.',
  `logQtr` int(11) DEFAULT NULL COMMENT 'Log transaction date quarter.',
  `logEntry` text COMMENT 'The XML description of changes.'
) ENGINE=ARCHIVE DEFAULT CHARSET=utf8
/*!50100 PARTITION BY RANGE (logDate)
(PARTITION p0 VALUES LESS THAN (1041408000) ENGINE = ARCHIVE,
 PARTITION p1 VALUES LESS THAN (1072944000) ENGINE = ARCHIVE,
 PARTITION p2 VALUES LESS THAN (1104566400) ENGINE = ARCHIVE,
 PARTITION p3 VALUES LESS THAN (1136102400) ENGINE = ARCHIVE,
 PARTITION p4 VALUES LESS THAN (1167638400) ENGINE = ARCHIVE,
 PARTITION p5 VALUES LESS THAN (1199174400) ENGINE = ARCHIVE,
 PARTITION p6 VALUES LESS THAN (1230796800) ENGINE = ARCHIVE,
 PARTITION p7 VALUES LESS THAN (1262332800) ENGINE = ARCHIVE,
 PARTITION p8 VALUES LESS THAN (1293868800) ENGINE = ARCHIVE,
 PARTITION p9 VALUES LESS THAN (1325404800) ENGINE = ARCHIVE,
 PARTITION p10 VALUES LESS THAN (1357027200) ENGINE = ARCHIVE,
 PARTITION p11 VALUES LESS THAN MAXVALUE ENGINE = ARCHIVE) */;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `log_invoice`
--

DROP TABLE IF EXISTS `log_invoice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `log_invoice` (
  `logDate` bigint(20) NOT NULL,
  `id` int(11) NOT NULL,
  `accountID` int(11) NOT NULL,
  `tableType` char(1) NOT NULL DEFAULT 'I',
  `invoiceType` varchar(20) DEFAULT NULL,
  `createdBy` int(11) DEFAULT NULL,
  `updatedBy` int(11) DEFAULT NULL,
  `creationDate` datetime DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL,
  `dueDate` date DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL,
  `totalAmount` decimal(9,2) DEFAULT NULL,
  `amountApplied` decimal(9,2) DEFAULT NULL,
  `commissionableAmount` decimal(9,2) NOT NULL DEFAULT '0.00',
  `paidDate` datetime DEFAULT NULL,
  `paymentMethod` varchar(30) DEFAULT NULL,
  `checkNumber` varchar(50) DEFAULT NULL,
  `transactionID` varchar(50) DEFAULT NULL,
  `poNumber` varchar(20) DEFAULT NULL,
  `ccNumber` varchar(20) DEFAULT NULL,
  `qbSync` tinyint(4) NOT NULL DEFAULT '0',
  `sapLastSync` datetime DEFAULT NULL,
  `sapSync` tinyint(1) DEFAULT '0',
  `sapID` varchar(25) DEFAULT NULL,
  `qbListID` varchar(25) DEFAULT NULL,
  `qbSyncWithTax` tinyint(4) NOT NULL DEFAULT '0',
  `payingFacilities` int(11) NOT NULL DEFAULT '0',
  `notes` text,
  `currency` char(3) DEFAULT 'USD',
  `lateFeeInvoiceID` int(11) DEFAULT NULL
) ENGINE=ARCHIVE DEFAULT CHARSET=utf8
/*!50100 PARTITION BY RANGE (logDate)
(PARTITION p0 VALUES LESS THAN (1041408000) ENGINE = ARCHIVE,
 PARTITION p1 VALUES LESS THAN (1072944000) ENGINE = ARCHIVE,
 PARTITION p2 VALUES LESS THAN (1104566400) ENGINE = ARCHIVE,
 PARTITION p3 VALUES LESS THAN (1136102400) ENGINE = ARCHIVE,
 PARTITION p4 VALUES LESS THAN (1167638400) ENGINE = ARCHIVE,
 PARTITION p5 VALUES LESS THAN (1199174400) ENGINE = ARCHIVE,
 PARTITION p6 VALUES LESS THAN (1230796800) ENGINE = ARCHIVE,
 PARTITION p7 VALUES LESS THAN (1262332800) ENGINE = ARCHIVE,
 PARTITION p8 VALUES LESS THAN (1293868800) ENGINE = ARCHIVE,
 PARTITION p9 VALUES LESS THAN (1325404800) ENGINE = ARCHIVE,
 PARTITION p10 VALUES LESS THAN (1357027200) ENGINE = ARCHIVE,
 PARTITION p11 VALUES LESS THAN MAXVALUE ENGINE = ARCHIVE) */;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `log_invoice_commission`
--

DROP TABLE IF EXISTS `log_invoice_commission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `log_invoice_commission` (
  `logDate` bigint(20) NOT NULL,
  `id` int(11) NOT NULL,
  `invoiceID` int(11) NOT NULL,
  `userID` int(11) DEFAULT NULL,
  `createdBy` int(11) NOT NULL,
  `creationDate` datetime NOT NULL,
  `updatedBy` int(11) NOT NULL,
  `updateDate` datetime NOT NULL,
  `activationPoints` decimal(11,7) NOT NULL,
  `revenue` decimal(11,7) NOT NULL,
  `accountUserID` int(11) NOT NULL
) ENGINE=ARCHIVE DEFAULT CHARSET=utf8
/*!50100 PARTITION BY RANGE (logDate)
(PARTITION p0 VALUES LESS THAN (1041408000) ENGINE = ARCHIVE,
 PARTITION p1 VALUES LESS THAN (1072944000) ENGINE = ARCHIVE,
 PARTITION p2 VALUES LESS THAN (1104566400) ENGINE = ARCHIVE,
 PARTITION p3 VALUES LESS THAN (1136102400) ENGINE = ARCHIVE,
 PARTITION p4 VALUES LESS THAN (1167638400) ENGINE = ARCHIVE,
 PARTITION p5 VALUES LESS THAN (1199174400) ENGINE = ARCHIVE,
 PARTITION p6 VALUES LESS THAN (1230796800) ENGINE = ARCHIVE,
 PARTITION p7 VALUES LESS THAN (1262332800) ENGINE = ARCHIVE,
 PARTITION p8 VALUES LESS THAN (1293868800) ENGINE = ARCHIVE,
 PARTITION p9 VALUES LESS THAN (1325404800) ENGINE = ARCHIVE,
 PARTITION p10 VALUES LESS THAN (1357027200) ENGINE = ARCHIVE,
 PARTITION p11 VALUES LESS THAN MAXVALUE ENGINE = ARCHIVE) */;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `log_invoice_fee`
--

DROP TABLE IF EXISTS `log_invoice_fee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `log_invoice_fee` (
  `logDate` bigint(20) NOT NULL,
  `id` int(11) NOT NULL,
  `fee` varchar(100) DEFAULT NULL,
  `defaultAmount` decimal(9,2) NOT NULL DEFAULT '0.00',
  `ratePercent` decimal(6,3) DEFAULT '0.000',
  `visible` tinyint(4) NOT NULL DEFAULT '1',
  `feeClass` varchar(50) CHARACTER SET latin1 DEFAULT NULL,
  `minFacilities` int(11) NOT NULL DEFAULT '0',
  `maxFacilities` int(11) NOT NULL DEFAULT '0',
  `qbFullName` varchar(75) DEFAULT NULL,
  `createdBy` int(11) DEFAULT NULL,
  `updatedBy` int(11) DEFAULT NULL,
  `creationDate` datetime DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL,
  `effectiveDate` datetime DEFAULT NULL,
  `displayOrder` tinyint(3) unsigned NOT NULL DEFAULT '100',
  `commissionEligible` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=ARCHIVE DEFAULT CHARSET=utf8
/*!50100 PARTITION BY RANGE (logDate)
(PARTITION p0 VALUES LESS THAN (1041408000) ENGINE = ARCHIVE,
 PARTITION p1 VALUES LESS THAN (1072944000) ENGINE = ARCHIVE,
 PARTITION p2 VALUES LESS THAN (1104566400) ENGINE = ARCHIVE,
 PARTITION p3 VALUES LESS THAN (1136102400) ENGINE = ARCHIVE,
 PARTITION p4 VALUES LESS THAN (1167638400) ENGINE = ARCHIVE,
 PARTITION p5 VALUES LESS THAN (1199174400) ENGINE = ARCHIVE,
 PARTITION p6 VALUES LESS THAN (1230796800) ENGINE = ARCHIVE,
 PARTITION p7 VALUES LESS THAN (1262332800) ENGINE = ARCHIVE,
 PARTITION p8 VALUES LESS THAN (1293868800) ENGINE = ARCHIVE,
 PARTITION p9 VALUES LESS THAN (1325404800) ENGINE = ARCHIVE,
 PARTITION p10 VALUES LESS THAN (1357027200) ENGINE = ARCHIVE,
 PARTITION p11 VALUES LESS THAN MAXVALUE ENGINE = ARCHIVE) */;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `log_invoice_fee_country`
--

DROP TABLE IF EXISTS `log_invoice_fee_country`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `log_invoice_fee_country` (
  `logDate` bigint(20) NOT NULL,
  `id` int(11) NOT NULL,
  `feeID` int(11) NOT NULL,
  `country` varchar(10) NOT NULL,
  `subdivision` varchar(10) DEFAULT NULL,
  `amount` decimal(9,2) NOT NULL DEFAULT '0.00',
  `ratePercent` decimal(6,3) DEFAULT '0.000',
  `createdBy` int(11) DEFAULT NULL,
  `updatedBy` int(11) DEFAULT NULL,
  `creationDate` datetime DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL,
  `effectiveDate` datetime NOT NULL DEFAULT '1970-01-01 00:00:00',
  `expirationDate` datetime NOT NULL DEFAULT '4000-01-01 23:59:59'
) ENGINE=ARCHIVE DEFAULT CHARSET=utf8
/*!50100 PARTITION BY RANGE (logDate)
(PARTITION p0 VALUES LESS THAN (1041408000) ENGINE = ARCHIVE,
 PARTITION p1 VALUES LESS THAN (1072944000) ENGINE = ARCHIVE,
 PARTITION p2 VALUES LESS THAN (1104566400) ENGINE = ARCHIVE,
 PARTITION p3 VALUES LESS THAN (1136102400) ENGINE = ARCHIVE,
 PARTITION p4 VALUES LESS THAN (1167638400) ENGINE = ARCHIVE,
 PARTITION p5 VALUES LESS THAN (1199174400) ENGINE = ARCHIVE,
 PARTITION p6 VALUES LESS THAN (1230796800) ENGINE = ARCHIVE,
 PARTITION p7 VALUES LESS THAN (1262332800) ENGINE = ARCHIVE,
 PARTITION p8 VALUES LESS THAN (1293868800) ENGINE = ARCHIVE,
 PARTITION p9 VALUES LESS THAN (1325404800) ENGINE = ARCHIVE,
 PARTITION p10 VALUES LESS THAN (1357027200) ENGINE = ARCHIVE,
 PARTITION p11 VALUES LESS THAN MAXVALUE ENGINE = ARCHIVE) */;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `log_invoice_item`
--

DROP TABLE IF EXISTS `log_invoice_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `log_invoice_item` (
  `logDate` bigint(20) NOT NULL,
  `id` int(11) NOT NULL,
  `invoiceID` int(11) NOT NULL,
  `feeID` int(11) NOT NULL,
  `amount` decimal(9,2) NOT NULL DEFAULT '0.00',
  `originalAmount` decimal(9,2) NOT NULL DEFAULT '0.00',
  `description` varchar(100) DEFAULT NULL,
  `createdBy` int(11) DEFAULT NULL,
  `updatedBy` int(11) DEFAULT NULL,
  `creationDate` datetime DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL,
  `paymentExpires` date DEFAULT NULL,
  `qbRefundID` varchar(25) CHARACTER SET latin1 DEFAULT NULL,
  `refunded` tinyint(4) DEFAULT '0',
  `refundFor` int(11) DEFAULT NULL,
  `transactionType` char(1) DEFAULT NULL,
  `revenueStartDate` date DEFAULT NULL,
  `revenueFinishDate` date DEFAULT NULL
) ENGINE=ARCHIVE DEFAULT CHARSET=utf8
/*!50100 PARTITION BY RANGE (logDate)
(PARTITION p0 VALUES LESS THAN (1041408000) ENGINE = ARCHIVE,
 PARTITION p1 VALUES LESS THAN (1072944000) ENGINE = ARCHIVE,
 PARTITION p2 VALUES LESS THAN (1104566400) ENGINE = ARCHIVE,
 PARTITION p3 VALUES LESS THAN (1136102400) ENGINE = ARCHIVE,
 PARTITION p4 VALUES LESS THAN (1167638400) ENGINE = ARCHIVE,
 PARTITION p5 VALUES LESS THAN (1199174400) ENGINE = ARCHIVE,
 PARTITION p6 VALUES LESS THAN (1230796800) ENGINE = ARCHIVE,
 PARTITION p7 VALUES LESS THAN (1262332800) ENGINE = ARCHIVE,
 PARTITION p8 VALUES LESS THAN (1293868800) ENGINE = ARCHIVE,
 PARTITION p9 VALUES LESS THAN (1325404800) ENGINE = ARCHIVE,
 PARTITION p10 VALUES LESS THAN (1357027200) ENGINE = ARCHIVE,
 PARTITION p11 VALUES LESS THAN MAXVALUE ENGINE = ARCHIVE) */;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `log_invoice_payment`
--

DROP TABLE IF EXISTS `log_invoice_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `log_invoice_payment` (
  `logDate` bigint(20) NOT NULL,
  `id` int(11) NOT NULL,
  `paymentID` int(10) NOT NULL,
  `invoiceID` int(10) DEFAULT NULL,
  `refundID` int(10) DEFAULT NULL,
  `amount` decimal(6,2) NOT NULL DEFAULT '0.00',
  `createdBy` int(11) DEFAULT NULL,
  `updatedBy` int(11) DEFAULT NULL,
  `creationDate` datetime DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL,
  `paymentType` char(1) NOT NULL DEFAULT 'I'
) ENGINE=ARCHIVE DEFAULT CHARSET=utf8
/*!50100 PARTITION BY RANGE (logDate)
(PARTITION p0 VALUES LESS THAN (1041408000) ENGINE = ARCHIVE,
 PARTITION p1 VALUES LESS THAN (1072944000) ENGINE = ARCHIVE,
 PARTITION p2 VALUES LESS THAN (1104566400) ENGINE = ARCHIVE,
 PARTITION p3 VALUES LESS THAN (1136102400) ENGINE = ARCHIVE,
 PARTITION p4 VALUES LESS THAN (1167638400) ENGINE = ARCHIVE,
 PARTITION p5 VALUES LESS THAN (1199174400) ENGINE = ARCHIVE,
 PARTITION p6 VALUES LESS THAN (1230796800) ENGINE = ARCHIVE,
 PARTITION p7 VALUES LESS THAN (1262332800) ENGINE = ARCHIVE,
 PARTITION p8 VALUES LESS THAN (1293868800) ENGINE = ARCHIVE,
 PARTITION p9 VALUES LESS THAN (1325404800) ENGINE = ARCHIVE,
 PARTITION p10 VALUES LESS THAN (1357027200) ENGINE = ARCHIVE,
 PARTITION p11 VALUES LESS THAN MAXVALUE ENGINE = ARCHIVE) */;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `log_msg_key`
--

DROP TABLE IF EXISTS `log_msg_key`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `log_msg_key` (
  `logDate` bigint(20) NOT NULL,
  `id` int(11) NOT NULL,
  `msgKey` varchar(100) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `js` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1 indicates translation applies to javascript',
  `firstUsed` date DEFAULT NULL,
  `lastUsed` date DEFAULT NULL,
  `createdBy` int(11) DEFAULT NULL,
  `updatedBy` int(11) DEFAULT NULL,
  `creationDate` datetime DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL
) ENGINE=ARCHIVE DEFAULT CHARSET=utf8
/*!50100 PARTITION BY RANGE (logDate)
(PARTITION p0 VALUES LESS THAN (1041408000) ENGINE = ARCHIVE,
 PARTITION p1 VALUES LESS THAN (1072944000) ENGINE = ARCHIVE,
 PARTITION p2 VALUES LESS THAN (1104566400) ENGINE = ARCHIVE,
 PARTITION p3 VALUES LESS THAN (1136102400) ENGINE = ARCHIVE,
 PARTITION p4 VALUES LESS THAN (1167638400) ENGINE = ARCHIVE,
 PARTITION p5 VALUES LESS THAN (1199174400) ENGINE = ARCHIVE,
 PARTITION p6 VALUES LESS THAN (1230796800) ENGINE = ARCHIVE,
 PARTITION p7 VALUES LESS THAN (1262332800) ENGINE = ARCHIVE,
 PARTITION p8 VALUES LESS THAN (1293868800) ENGINE = ARCHIVE,
 PARTITION p9 VALUES LESS THAN (1325404800) ENGINE = ARCHIVE,
 PARTITION p10 VALUES LESS THAN (1357027200) ENGINE = ARCHIVE,
 PARTITION p11 VALUES LESS THAN MAXVALUE ENGINE = ARCHIVE) */;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `log_msg_locale`
--

DROP TABLE IF EXISTS `log_msg_locale`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `log_msg_locale` (
  `logDate` bigint(20) NOT NULL,
  `id` int(11) NOT NULL,
  `keyID` int(11) NOT NULL,
  `locale` varchar(8) NOT NULL,
  `msgValue` text,
  `firstUsed` date DEFAULT NULL,
  `lastUsed` date DEFAULT NULL,
  `qualityRating` int(4) NOT NULL DEFAULT '0',
  `createdBy` int(11) DEFAULT NULL,
  `updatedBy` int(11) DEFAULT NULL,
  `creationDate` datetime DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL
) ENGINE=ARCHIVE DEFAULT CHARSET=utf8
/*!50100 PARTITION BY RANGE (logDate)
(PARTITION p0 VALUES LESS THAN (1041408000) ENGINE = ARCHIVE,
 PARTITION p1 VALUES LESS THAN (1072944000) ENGINE = ARCHIVE,
 PARTITION p2 VALUES LESS THAN (1104566400) ENGINE = ARCHIVE,
 PARTITION p3 VALUES LESS THAN (1136102400) ENGINE = ARCHIVE,
 PARTITION p4 VALUES LESS THAN (1167638400) ENGINE = ARCHIVE,
 PARTITION p5 VALUES LESS THAN (1199174400) ENGINE = ARCHIVE,
 PARTITION p6 VALUES LESS THAN (1230796800) ENGINE = ARCHIVE,
 PARTITION p7 VALUES LESS THAN (1262332800) ENGINE = ARCHIVE,
 PARTITION p8 VALUES LESS THAN (1293868800) ENGINE = ARCHIVE,
 PARTITION p9 VALUES LESS THAN (1325404800) ENGINE = ARCHIVE,
 PARTITION p10 VALUES LESS THAN (1357027200) ENGINE = ARCHIVE,
 PARTITION p11 VALUES LESS THAN MAXVALUE ENGINE = ARCHIVE) */;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `log_users`
--

DROP TABLE IF EXISTS `log_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `log_users` (
  `logID` bigint(20) NOT NULL,
  `username` varchar(100) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `isGroup` enum('Yes','No') CHARACTER SET latin1 NOT NULL DEFAULT 'No',
  `email` varchar(100) DEFAULT NULL,
  `firstName` varchar(50) DEFAULT NULL,
  `lastName` varchar(50) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `isActive` enum('Yes','No') CHARACTER SET latin1 NOT NULL DEFAULT 'Yes',
  `lastLogin` datetime DEFAULT NULL,
  `accountID` int(11) NOT NULL,
  `passwordHistory` varchar(1000) CHARACTER SET latin1 DEFAULT NULL,
  `failedAttempts` tinyint(4) NOT NULL DEFAULT '0',
  `lockUntil` datetime DEFAULT NULL,
  `resetHash` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `phone` varchar(50) CHARACTER SET latin1 DEFAULT NULL,
  `fax` varchar(15) CHARACTER SET latin1 DEFAULT NULL,
  `phoneIndex` varchar(11) CHARACTER SET latin1 DEFAULT NULL,
  `passwordChanged` date DEFAULT NULL,
  `createdBy` int(11) DEFAULT NULL,
  `updatedBy` int(11) DEFAULT NULL,
  `creationDate` datetime DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL,
  `emailConfirmedDate` date DEFAULT NULL,
  `timezone` varchar(50) CHARACTER SET latin1 DEFAULT NULL,
  `forcePasswordReset` tinyint(4) NOT NULL DEFAULT '0',
  `needsIndexing` tinyint(4) NOT NULL DEFAULT '1',
  `locale` varchar(5) CHARACTER SET latin1 DEFAULT 'en',
  `department` varchar(100) DEFAULT NULL,
  `inheritReportMenuFrom` int(11) DEFAULT NULL,
  `usingDynamicReports` tinyint(4) NOT NULL DEFAULT '0',
  `usingDynamicReportsDate` datetime DEFAULT NULL,
  `usingVersion7Menus` tinyint(4) NOT NULL DEFAULT '0',
  `usingVersion7MenusDate` datetime DEFAULT NULL,
  `reportsManagerTutorialDate` datetime DEFAULT NULL COMMENT 'Indicates the date that the user was redirected to the tutorial. NULL indicates never.',
  `assignmentCapacity` smallint(6) DEFAULT NULL,
  `shiftStartHour` tinyint(3) unsigned DEFAULT '8',
  `shiftEndHour` tinyint(3) unsigned DEFAULT '16',
  `workdays` char(7) CHARACTER SET latin1 DEFAULT 'xMTWTFx',
  `api` tinyint(4) NOT NULL DEFAULT '0',
  `apiKey` varchar(36) CHARACTER SET latin1 DEFAULT NULL,
  `appUserID` int(11) DEFAULT NULL
) ENGINE=ARCHIVE DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'log_archive'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-03-05 11:48:23
