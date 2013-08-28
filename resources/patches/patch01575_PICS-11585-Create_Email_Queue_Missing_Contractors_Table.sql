-- MySQL dump 10.13  Distrib 5.5.32, for Linux (x86_64)
--
-- Host: localhost    Database: pics_alpha1
-- ------------------------------------------------------
-- Server version	5.5.32-31.0-log

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
-- Table structure for table `email_queue_missing_contractors`
--

DROP TABLE IF EXISTS `email_queue_missing_contractors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `email_queue_missing_contractors` (
  `emailID` int(11) NOT NULL DEFAULT '0',
  `status` enum('Pending','Sent','Bounced','Error') CHARACTER SET latin1 NOT NULL DEFAULT 'Pending',
  `fromAddress` varchar(150) CHARACTER SET utf8 DEFAULT NULL,
  `toAddresses` varchar(1000) CHARACTER SET utf8 DEFAULT NULL,
  `ccAddresses` varchar(2000) CHARACTER SET utf8 DEFAULT NULL,
  `bccAddresses` varchar(2000) CHARACTER SET utf8 DEFAULT NULL,
  `subject` varchar(150) CHARACTER SET utf8 DEFAULT NULL,
  `body` mediumtext CHARACTER SET utf8,
  `priority` tinyint(4) NOT NULL DEFAULT '50',
  `creationDate` datetime NOT NULL,
  `createdBy` int(11) DEFAULT NULL,
  `sentDate` datetime DEFAULT NULL,
  `templateID` int(11) DEFAULT NULL,
  `conID` int(11) DEFAULT NULL,
  `fromPassword` varchar(50) CHARACTER SET latin1 DEFAULT NULL,
  `html` tinyint(4) NOT NULL DEFAULT '0',
  `viewableBy` int(10) DEFAULT NULL,
  `id` int(11),
  `taxID` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `main_trade` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `logo_file` varchar(50) CHARACTER SET latin1 DEFAULT NULL,
  `brochure_file` varchar(10) CHARACTER SET latin1 DEFAULT NULL,
  `description` text CHARACTER SET utf8,
  `mustPay` enum('Yes','No') CHARACTER SET latin1 DEFAULT 'No',
  `paymentExpires` date DEFAULT NULL,
  `requestedByID` mediumint(9) DEFAULT '0',
  `secondContact` varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  `secondPhone` varchar(50) CHARACTER SET latin1 DEFAULT NULL,
  `secondEmail` varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  `billingContact` varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  `billingPhone` varchar(50) CHARACTER SET latin1 DEFAULT NULL,
  `billingEmail` varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  `billingAddress` varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  `billingCity` varchar(35) CHARACTER SET utf8 DEFAULT NULL,
  `billingCountrySubdivision` varchar(10) CHARACTER SET latin1 DEFAULT NULL,
  `billingZip` varchar(10) CHARACTER SET latin1 DEFAULT NULL,
  `billingCountry` varchar(25) CHARACTER SET latin1 DEFAULT NULL,
  `membershipDate` date DEFAULT NULL,
  `payingFacilities` smallint(5) unsigned DEFAULT '0',
  `welcomeAuditor_id` mediumint(8) unsigned DEFAULT NULL,
  `insideSalesPriority` tinyint(3) DEFAULT NULL,
  `safetyRisk` tinyint(3) DEFAULT '2',
  `safetyRiskVerified` date DEFAULT NULL,
  `productRisk` tinyint(3) DEFAULT '2',
  `productRiskVerified` date DEFAULT NULL,
  `transportationRisk` tinyint(3) DEFAULT '2',
  `transportationRiskVerified` date DEFAULT NULL,
  `riskLevel` tinyint(3) DEFAULT '2',
  `viewedFacilities` datetime DEFAULT NULL,
  `paymentMethod` varchar(20) CHARACTER SET latin1 DEFAULT 'CreditCard',
  `paymentMethodStatus` varchar(20) CHARACTER SET latin1 DEFAULT NULL,
  `membershipLevelID` smallint(6) DEFAULT NULL,
  `newMembershipLevelID` smallint(6) DEFAULT NULL,
  `renew` tinyint(4) DEFAULT '1',
  `lastUpgradeDate` date DEFAULT NULL,
  `lastContactedByAutomatedEmailDate` datetime DEFAULT NULL,
  `lastContactedByInsideSales` int(11) DEFAULT NULL,
  `lastContactedByInsideSalesDate` datetime DEFAULT NULL,
  `followupDate` datetime DEFAULT NULL,
  `contactCountByEmail` tinyint(4) unsigned DEFAULT '0',
  `contactCountByPhone` tinyint(4) unsigned DEFAULT '0',
  `expiresOnDate` date DEFAULT NULL,
  `balance` decimal(9,2) DEFAULT '0.00',
  `needsRecalculation` tinyint(4) DEFAULT '1',
  `lastRecalculation` datetime DEFAULT NULL,
  `ccOnFile` tinyint(4) DEFAULT '0',
  `ccExpiration` date DEFAULT NULL,
  `ccEmail` varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  `emrAverage` decimal(8,3) DEFAULT NULL,
  `trirAverage` decimal(8,3) DEFAULT NULL,
  `lwcrAverage` decimal(8,3) DEFAULT NULL,
  `tradesSelf` varchar(4000) CHARACTER SET utf8 DEFAULT NULL,
  `tradesSub` varchar(4000) CHARACTER SET utf8 DEFAULT NULL,
  `agreementDate` datetime DEFAULT NULL,
  `agreedBy` int(11) DEFAULT NULL,
  `score` int(10) unsigned DEFAULT '0',
  `tradesUpdated` datetime DEFAULT NULL,
  `soleProprietor` tinyint(3) DEFAULT NULL,
  `competitorMembership` tinyint(4) DEFAULT NULL,
  `accountLevel` varchar(20) CHARACTER SET latin1 DEFAULT NULL,
  `hasCanadianCompetitor` tinyint(4) DEFAULT NULL,
  `showInDirectory` tinyint(4) DEFAULT '1',
  `reviewedContractorBadge` tinyint(4) DEFAULT '0',
  `lcCorPhase` varchar(20) CHARACTER SET latin1 DEFAULT NULL,
  `lcCorNotification` date DEFAULT NULL,
  `europeanUnionVATnumber` varchar(15) CHARACTER SET latin1 DEFAULT NULL,
  `registrationHash` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `recommendedCsrID` int(10) unsigned DEFAULT NULL,
  `dontReassign` tinyint(4) DEFAULT '0',
  `autoAddClientSite` tinyint(4) DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-08-16  9:13:17
