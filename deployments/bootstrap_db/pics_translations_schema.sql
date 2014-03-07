-- MySQL dump 10.13  Distrib 5.5.34, for Linux (x86_64)
--
-- Host: localhost    Database: pics_translations
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
-- Table structure for table `databasechangelog`
--

DROP TABLE IF EXISTS `databasechangelog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `databasechangelog` (
  `ID` varchar(63) COLLATE utf8_bin NOT NULL,
  `AUTHOR` varchar(63) COLLATE utf8_bin NOT NULL,
  `FILENAME` varchar(200) COLLATE utf8_bin NOT NULL,
  `DATEEXECUTED` datetime NOT NULL,
  `ORDEREXECUTED` int(11) NOT NULL,
  `EXECTYPE` varchar(10) COLLATE utf8_bin NOT NULL,
  `MD5SUM` varchar(35) COLLATE utf8_bin DEFAULT NULL,
  `DESCRIPTION` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `COMMENTS` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TAG` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `LIQUIBASE` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`,`AUTHOR`,`FILENAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `databasechangeloglock`
--

DROP TABLE IF EXISTS `databasechangeloglock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `databasechangeloglock` (
  `ID` int(11) NOT NULL,
  `LOCKED` tinyint(1) NOT NULL,
  `LOCKGRANTED` datetime DEFAULT NULL,
  `LOCKEDBY` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `debug`
--

DROP TABLE IF EXISTS `debug`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `debug` (
  `message` varchar(1000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `file_asset`
--

DROP TABLE IF EXISTS `file_asset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `file_asset` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `guid` varchar(100) NOT NULL,
  `mimeType` varchar(100) NOT NULL,
  `uploadedBy` varchar(100) DEFAULT NULL,
  `uploadedDate` date DEFAULT NULL,
  `etlStatus` enum('PENDING','COMPLETE','FAILED','IMPORTED') DEFAULT 'PENDING',
  `processDate` datetime DEFAULT NULL,
  `allRows` int(11) DEFAULT NULL,
  `insRows` int(11) DEFAULT NULL,
  `updRows` int(11) DEFAULT NULL,
  `delRows` int(11) DEFAULT NULL,
  `errRows` int(11) DEFAULT NULL,
  `dupRows` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `guid` (`guid`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `file_asset_item`
--

DROP TABLE IF EXISTS `file_asset_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `file_asset_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `fileAssetID` int(11) NOT NULL,
  `msgKey` varchar(100) DEFAULT NULL,
  `locale` varchar(8) DEFAULT NULL,
  `msgValue` text,
  `etlAction` enum('INSERT','UPDATE','DELETE','VERIFY','REJECT','IGNORE') DEFAULT 'INSERT',
  `etlActionResult` mediumtext,
  PRIMARY KEY (`id`),
  KEY `fk1_file_asset_item` (`fileAssetID`),
  CONSTRAINT `fk1_file_asset_item` FOREIGN KEY (`fileAssetID`) REFERENCES `file_asset` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1012 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `msg_key`
--

DROP TABLE IF EXISTS `msg_key`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msg_key` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `msgKey` varchar(100) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `js` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1 indicates translation applies to javascript',
  `firstUsed` date DEFAULT NULL,
  `lastUsed` date DEFAULT NULL,
  `createdBy` int(11) DEFAULT NULL,
  `updatedBy` int(11) DEFAULT NULL,
  `creationDate` datetime DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `msgKey` (`msgKey`),
  KEY `lastUsed` (`lastUsed`)
) ENGINE=InnoDB AUTO_INCREMENT=831091 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`pics_admin`@`%`*/ /*!50003 TRIGGER	msg_key_archive_after_insert
AFTER INSERT ON	msg_key
/*
**	Name:		msg_key_archive_after_insert
**	Type:		after insert trigger
**	Purpose:	To insert msg_key DML history into the logging tables
**	Author:		Solomon S. Shacter
**
**	Modified:	2013-NOV-01
**	Modnumber:	00
**	Modification:	Original
**
*/
FOR EACH ROW
BEGIN
	--	-------------------------------------------------------------------------
	SET	@dmlType	= "INSERT";
	SET	@ddlName	= "msg_key";
	SET	@logDate	= UNIX_TIMESTAMP();
	SET	@validStart	= DATE(IFNULL(new.creationDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(new.updateDate,CURRENT_DATE));
	SET @username := CURRENT_USER;
	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', FROM_UNIXTIME(@logDate),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'
	,	'	<field column="id"><oldValue>', IFNULL(new.id,"") ,'</oldValue><newValue>', IFNULL(new.id,"") ,'</newValue></field>'
	,	'	<field column="msgKey"><oldValue>', IFNULL(new.msgKey,"") ,'</oldValue><newValue>', IFNULL(new.msgKey,"") ,'</newValue></field>'
	,	'	<field column="description"><oldValue>', IFNULL(new.description,"") ,'</oldValue><newValue>', IFNULL(new.description,"") ,'</newValue></field>'
	,	'	<field column="js"><oldValue>', IFNULL(new.js,"") ,'</oldValue><newValue>', IFNULL(new.js,"") ,'</newValue></field>'
	,	'	<field column="firstUsed"><oldValue>', IFNULL(new.firstUsed,"") ,'</oldValue><newValue>', IFNULL(new.firstUsed,"") ,'</newValue></field>'
	,	'	<field column="lastUsed"><oldValue>', IFNULL(new.lastUsed,"") ,'</oldValue><newValue>', IFNULL(new.lastUsed,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(new.createdBy,"") ,'</oldValue><newValue>', IFNULL(new.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(new.updatedBy,"") ,'</oldValue><newValue>', IFNULL(new.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(new.creationDate,"") ,'</oldValue><newValue>', IFNULL(new.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(new.updateDate,"") ,'</oldValue><newValue>', IFNULL(new.updateDate,"") ,'</newValue></field>'
	,	'</diff>\n'
	,'</logdata>'
	)
	;
	--	-------------------------------------------------------------------------
	INSERT INTO	log_archive.log_event
	(
		logDate
	,	validStart
	,	validFinish
	,	dmlType
	,	ddlName
	,	ddlKey
	,	logSeq
	,	userName
	,	logYear
	,	logMonth
	,	logWeek
	,	logDay
	,	logQtr
	,	logEntry
	)
	VALUES
	(
		@logDate
	,	@validStart
	,	@validFinish
	,	@dmlType
	,	@ddlName
	,	new.id
	,	new.id
	,	@username
	,	YEAR(CURRENT_DATE)
	,	MONTH(CURRENT_DATE)
	,	WEEK(CURRENT_DATE)
	,	DAY(CURRENT_DATE)
	,	QUARTER(CURRENT_DATE)
	,	@logEntry
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
	INSERT INTO	log_archive.log_msg_key
	VALUES
	(
		@logDate
	,	new.id
	,	new.msgKey
	,	new.description
	,	new.js
	,	new.firstUsed
	,	new.lastUsed
	,	new.createdBy
	,	new.updatedBy
	,	new.creationDate
	,	new.updateDate
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`pics_admin`@`%`*/ /*!50003 TRIGGER	msg_key_archive_after_update
AFTER UPDATE ON	msg_key
/*
**	Name:		msg_key_archive_after_update
**	Type:		after update trigger
**	Purpose:	To insert msg_key DML history into the logging tables
**	Author:		Solomon S. Shacter
**
**	Modified:	2013-NOV-01
**	Modnumber:	00
**	Modification:	Original
**
*/
FOR EACH ROW
BEGIN
	--	-------------------------------------------------------------------------
	SET	@dmlType	= "UPDATE";
	SET	@ddlName	= "msg_key";
	SET	@logDate	= UNIX_TIMESTAMP();
	SET	@validStart	= DATE(IFNULL(new.creationDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(new.updateDate,CURRENT_DATE));
	SET @username := CURRENT_USER;
	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', FROM_UNIXTIME(@logDate),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'
	,	'	<field column="id"><oldValue>', IFNULL(old.id,"") ,'</oldValue><newValue>', IFNULL(new.id,"") ,'</newValue></field>'
	,	'	<field column="msgKey"><oldValue>', IFNULL(old.msgKey,"") ,'</oldValue><newValue>', IFNULL(new.msgKey,"") ,'</newValue></field>'
	,	'	<field column="description"><oldValue>', IFNULL(old.description,"") ,'</oldValue><newValue>', IFNULL(new.description,"") ,'</newValue></field>'
	,	'	<field column="js"><oldValue>', IFNULL(old.js,"") ,'</oldValue><newValue>', IFNULL(new.js,"") ,'</newValue></field>'
	,	'	<field column="firstUsed"><oldValue>', IFNULL(old.firstUsed,"") ,'</oldValue><newValue>', IFNULL(new.firstUsed,"") ,'</newValue></field>'
	,	'	<field column="lastUsed"><oldValue>', IFNULL(old.lastUsed,"") ,'</oldValue><newValue>', IFNULL(new.lastUsed,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(old.createdBy,"") ,'</oldValue><newValue>', IFNULL(new.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(old.updatedBy,"") ,'</oldValue><newValue>', IFNULL(new.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(old.creationDate,"") ,'</oldValue><newValue>', IFNULL(new.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(old.updateDate,"") ,'</oldValue><newValue>', IFNULL(new.updateDate,"") ,'</newValue></field>'
	,	'</diff>\n'
	,'</logdata>'
	)
	;
	--	-------------------------------------------------------------------------
	INSERT INTO	log_archive.log_event
	(
		logDate
	,	validStart
	,	validFinish
	,	dmlType
	,	ddlName
	,	ddlKey
	,	logSeq
	,	userName
	,	logYear
	,	logMonth
	,	logWeek
	,	logDay
	,	logQtr
	,	logEntry
	)
	VALUES
	(
		@logDate
	,	@validStart
	,	@validFinish
	,	@dmlType
	,	@ddlName
	,	new.id
	,	new.id
	,	@username
	,	YEAR(CURRENT_DATE)
	,	MONTH(CURRENT_DATE)
	,	WEEK(CURRENT_DATE)
	,	DAY(CURRENT_DATE)
	,	QUARTER(CURRENT_DATE)
	,	@logEntry
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
	INSERT INTO	log_archive.log_msg_key
	VALUES
	(
		@logDate
	,	new.id
	,	new.msgKey
	,	new.description
	,	new.js
	,	new.firstUsed
	,	new.lastUsed
	,	new.createdBy
	,	new.updatedBy
	,	new.creationDate
	,	new.updateDate
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`pics_admin`@`%`*/ /*!50003 TRIGGER	msg_key_archive_before_delete
BEFORE DELETE ON	msg_key
/*
**	Name:		msg_key_archive_before_delete
**	Type:		before delete trigger
**	Purpose:	To insert msg_key DML history into the logging tables
**	Author:		Solomon S. Shacter
**
**	Modified:	2013-NOV-01
**	Modnumber:	00
**	Modification:	Original
**
*/
FOR EACH ROW
BEGIN
	--	-------------------------------------------------------------------------
	SET	@dmlType	= "DELETE";
	SET	@ddlName	= "msg_key";
	SET	@logDate	= UNIX_TIMESTAMP();
	SET	@validStart	= DATE(IFNULL(old.creationDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(old.updateDate,CURRENT_DATE));
	SET @username := CURRENT_USER;
	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', FROM_UNIXTIME(@logDate),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'
	,	'	<field column="id"><oldValue>', IFNULL(old.id,"") ,'</oldValue><newValue>', IFNULL(old.id,"") ,'</newValue></field>'
	,	'	<field column="msgKey"><oldValue>', IFNULL(old.msgKey,"") ,'</oldValue><newValue>', IFNULL(old.msgKey,"") ,'</newValue></field>'
	,	'	<field column="description"><oldValue>', IFNULL(old.description,"") ,'</oldValue><newValue>', IFNULL(old.description,"") ,'</newValue></field>'
	,	'	<field column="js"><oldValue>', IFNULL(old.js,"") ,'</oldValue><newValue>', IFNULL(old.js,"") ,'</newValue></field>'
	,	'	<field column="firstUsed"><oldValue>', IFNULL(old.firstUsed,"") ,'</oldValue><newValue>', IFNULL(old.firstUsed,"") ,'</newValue></field>'
	,	'	<field column="lastUsed"><oldValue>', IFNULL(old.lastUsed,"") ,'</oldValue><newValue>', IFNULL(old.lastUsed,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(old.createdBy,"") ,'</oldValue><newValue>', IFNULL(old.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(old.updatedBy,"") ,'</oldValue><newValue>', IFNULL(old.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(old.creationDate,"") ,'</oldValue><newValue>', IFNULL(old.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(old.updateDate,"") ,'</oldValue><newValue>', IFNULL(old.updateDate,"") ,'</newValue></field>'
	,	'</diff>\n'
	,'</logdata>'
	)
	;
	--	-------------------------------------------------------------------------
	INSERT INTO	log_archive.log_event
	(
		logDate
	,	validStart
	,	validFinish
	,	dmlType
	,	ddlName
	,	ddlKey
	,	logSeq
	,	userName
	,	logYear
	,	logMonth
	,	logWeek
	,	logDay
	,	logQtr
	,	logEntry
	)
	VALUES
	(
		@logDate
	,	@validStart
	,	@validFinish
	,	@dmlType
	,	@ddlName
	,	old.id
	,	old.id
	,	@username
	,	YEAR(CURRENT_DATE)
	,	MONTH(CURRENT_DATE)
	,	WEEK(CURRENT_DATE)
	,	DAY(CURRENT_DATE)
	,	QUARTER(CURRENT_DATE)
	,	@logEntry
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
	INSERT INTO	log_archive.log_msg_key
	VALUES
	(
		@logDate
	,	old.id
	,	old.msgKey
	,	old.description
	,	old.js
	,	old.firstUsed
	,	old.lastUsed
	,	old.createdBy
	,	old.updatedBy
	,	old.creationDate
	,	old.updateDate
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `msg_locale`
--

DROP TABLE IF EXISTS `msg_locale`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msg_locale` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `keyID` int(11) NOT NULL,
  `locale` varchar(8) NOT NULL,
  `msgValue` text,
  `firstUsed` date DEFAULT NULL,
  `lastUsed` date DEFAULT NULL,
  `qualityRating` int(4) NOT NULL DEFAULT '0',
  `createdBy` int(11) DEFAULT NULL,
  `updatedBy` int(11) DEFAULT NULL,
  `creationDate` datetime DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `msg_locale_key` (`keyID`,`locale`),
  CONSTRAINT `msg_locale_key` FOREIGN KEY (`keyID`) REFERENCES `msg_key` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=555921 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`pics_admin`@`%`*/ /*!50003 TRIGGER	msg_locale_archive_after_insert
AFTER INSERT ON	msg_locale
/*
**	Name:		msg_locale_archive_after_insert
**	Type:		after insert trigger
**	Purpose:	To insert msg_locale DML history into the logging tables
**	Author:		Solomon S. Shacter
**
**	Modified:	2013-NOV-01
**	Modnumber:	00
**	Modification:	Original
**
*/
FOR EACH ROW
BEGIN
	--	-------------------------------------------------------------------------
	SET	@dmlType	= "INSERT";
	SET	@ddlName	= "msg_locale";
	SET	@logDate	= UNIX_TIMESTAMP();
	SET	@validStart	= DATE(IFNULL(new.firstUsed,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(new.lastUsed,CURRENT_DATE));
	SET @username := CURRENT_USER;
	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', FROM_UNIXTIME(@logDate),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'
	,	'	<field column="id"><oldValue>', IFNULL(new.id,"") ,'</oldValue><newValue>', IFNULL(new.id,"") ,'</newValue></field>'
	,	'	<field column="keyID"><oldValue>', IFNULL(new.keyID,"") ,'</oldValue><newValue>', IFNULL(new.keyID,"") ,'</newValue></field>'
	,	'	<field column="locale"><oldValue>', IFNULL(new.locale,"") ,'</oldValue><newValue>', IFNULL(new.locale,"") ,'</newValue></field>'
	,	'	<field column="msgValue"><oldValue>', IFNULL(new.msgValue,"") ,'</oldValue><newValue>', IFNULL(new.msgValue,"") ,'</newValue></field>'
	,	'	<field column="firstUsed"><oldValue>', IFNULL(new.firstUsed,"") ,'</oldValue><newValue>', IFNULL(new.firstUsed,"") ,'</newValue></field>'
	,	'	<field column="lastUsed"><oldValue>', IFNULL(new.lastUsed,"") ,'</oldValue><newValue>', IFNULL(new.lastUsed,"") ,'</newValue></field>'
	,	'	<field column="qualityRating"><oldValue>', IFNULL(new.qualityRating,"") ,'</oldValue><newValue>', IFNULL(new.qualityRating,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(new.createdBy,"") ,'</oldValue><newValue>', IFNULL(new.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(new.updatedBy,"") ,'</oldValue><newValue>', IFNULL(new.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(new.creationDate,"") ,'</oldValue><newValue>', IFNULL(new.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(new.updateDate,"") ,'</oldValue><newValue>', IFNULL(new.updateDate,"") ,'</newValue></field>'
	,	'</diff>\n'
	,'</logdata>'
	)
	;
	--	-------------------------------------------------------------------------
	INSERT INTO	log_archive.log_event
	(
		logDate
	,	validStart
	,	validFinish
	,	dmlType
	,	ddlName
	,	ddlKey
	,	logSeq
	,	userName
	,	logYear
	,	logMonth
	,	logWeek
	,	logDay
	,	logQtr
	,	logEntry
	)
	VALUES
	(
		@logDate
	,	@validStart
	,	@validFinish
	,	@dmlType
	,	@ddlName
	,	new.id
	,	new.id
	,	@username
	,	YEAR(CURRENT_DATE)
	,	MONTH(CURRENT_DATE)
	,	WEEK(CURRENT_DATE)
	,	DAY(CURRENT_DATE)
	,	QUARTER(CURRENT_DATE)
	,	@logEntry
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
	INSERT INTO	log_archive.log_msg_locale
	VALUES
	(
		@logDate
	,	new.id
	,	new.keyID
	,	new.locale
	,	new.msgValue
	,	new.firstUsed
	,	new.lastUsed
	,	new.qualityRating
	,	new.createdBy
	,	new.updatedBy
	,	new.creationDate
	,	new.updateDate
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`pics_admin`@`%`*/ /*!50003 TRIGGER	msg_locale_archive_after_update
AFTER UPDATE ON	msg_locale
/*
**	Name:		msg_locale_archive_after_update
**	Type:		after update trigger
**	Purpose:	To insert msg_locale DML history into the logging tables
**	Author:		Solomon S. Shacter
**
**	Modified:	2013-NOV-01
**	Modnumber:	00
**	Modification:	Original
**
*/
FOR EACH ROW
BEGIN
	--	-------------------------------------------------------------------------
	SET	@dmlType	= "UPDATE";
	SET	@ddlName	= "msg_locale";
	SET	@logDate	= UNIX_TIMESTAMP();
	SET	@validStart	= DATE(IFNULL(new.firstUsed,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(new.lastUsed,CURRENT_DATE));
	SET @username := CURRENT_USER;
	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', FROM_UNIXTIME(@logDate),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'
	,	'	<field column="id"><oldValue>', IFNULL(old.id,"") ,'</oldValue><newValue>', IFNULL(new.id,"") ,'</newValue></field>'
	,	'	<field column="keyID"><oldValue>', IFNULL(old.keyID,"") ,'</oldValue><newValue>', IFNULL(new.keyID,"") ,'</newValue></field>'
	,	'	<field column="locale"><oldValue>', IFNULL(old.locale,"") ,'</oldValue><newValue>', IFNULL(new.locale,"") ,'</newValue></field>'
	,	'	<field column="msgValue"><oldValue>', IFNULL(old.msgValue,"") ,'</oldValue><newValue>', IFNULL(new.msgValue,"") ,'</newValue></field>'
	,	'	<field column="firstUsed"><oldValue>', IFNULL(old.firstUsed,"") ,'</oldValue><newValue>', IFNULL(new.firstUsed,"") ,'</newValue></field>'
	,	'	<field column="lastUsed"><oldValue>', IFNULL(old.lastUsed,"") ,'</oldValue><newValue>', IFNULL(new.lastUsed,"") ,'</newValue></field>'
	,	'	<field column="qualityRating"><oldValue>', IFNULL(old.qualityRating,"") ,'</oldValue><newValue>', IFNULL(new.qualityRating,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(old.createdBy,"") ,'</oldValue><newValue>', IFNULL(new.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(old.updatedBy,"") ,'</oldValue><newValue>', IFNULL(new.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(old.creationDate,"") ,'</oldValue><newValue>', IFNULL(new.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(old.updateDate,"") ,'</oldValue><newValue>', IFNULL(new.updateDate,"") ,'</newValue></field>'
	,	'</diff>\n'
	,'</logdata>'
	)
	;
	--	-------------------------------------------------------------------------
	INSERT INTO	log_archive.log_event
	(
		logDate
	,	validStart
	,	validFinish
	,	dmlType
	,	ddlName
	,	ddlKey
	,	logSeq
	,	userName
	,	logYear
	,	logMonth
	,	logWeek
	,	logDay
	,	logQtr
	,	logEntry
	)
	VALUES
	(
		@logDate
	,	@validStart
	,	@validFinish
	,	@dmlType
	,	@ddlName
	,	new.id
	,	new.id
	,	@username
	,	YEAR(CURRENT_DATE)
	,	MONTH(CURRENT_DATE)
	,	WEEK(CURRENT_DATE)
	,	DAY(CURRENT_DATE)
	,	QUARTER(CURRENT_DATE)
	,	@logEntry
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
	INSERT INTO	log_archive.log_msg_locale
	VALUES
	(
		@logDate
	,	new.id
	,	new.keyID
	,	new.locale
	,	new.msgValue
	,	new.firstUsed
	,	new.lastUsed
	,	new.qualityRating
	,	new.createdBy
	,	new.updatedBy
	,	new.creationDate
	,	new.updateDate
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`pics_admin`@`%`*/ /*!50003 TRIGGER	msg_locale_archive_before_delete
BEFORE DELETE ON	msg_locale
/*
**	Name:		msg_locale_archive_before_delete
**	Type:		before delete trigger
**	Purpose:	To insert msg_locale DML history into the logging tables
**	Author:		Solomon S. Shacter
**
**	Modified:	2013-NOV-01
**	Modnumber:	00
**	Modification:	Original
**
*/
FOR EACH ROW
BEGIN
	--	-------------------------------------------------------------------------
	SET	@dmlType	= "DELETE";
	SET	@ddlName	= "msg_locale";
	SET	@logDate	= UNIX_TIMESTAMP();
	SET	@validStart	= DATE(IFNULL(old.firstUsed,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(old.lastUsed,CURRENT_DATE));
	SET @username := CURRENT_USER;
	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', FROM_UNIXTIME(@logDate),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'
	,	'	<field column="id"><oldValue>', IFNULL(old.id,"") ,'</oldValue><newValue>', IFNULL(old.id,"") ,'</newValue></field>'
	,	'	<field column="keyID"><oldValue>', IFNULL(old.keyID,"") ,'</oldValue><newValue>', IFNULL(old.keyID,"") ,'</newValue></field>'
	,	'	<field column="locale"><oldValue>', IFNULL(old.locale,"") ,'</oldValue><newValue>', IFNULL(old.locale,"") ,'</newValue></field>'
	,	'	<field column="msgValue"><oldValue>', IFNULL(old.msgValue,"") ,'</oldValue><newValue>', IFNULL(old.msgValue,"") ,'</newValue></field>'
	,	'	<field column="firstUsed"><oldValue>', IFNULL(old.firstUsed,"") ,'</oldValue><newValue>', IFNULL(old.firstUsed,"") ,'</newValue></field>'
	,	'	<field column="lastUsed"><oldValue>', IFNULL(old.lastUsed,"") ,'</oldValue><newValue>', IFNULL(old.lastUsed,"") ,'</newValue></field>'
	,	'	<field column="qualityRating"><oldValue>', IFNULL(old.qualityRating,"") ,'</oldValue><newValue>', IFNULL(old.qualityRating,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(old.createdBy,"") ,'</oldValue><newValue>', IFNULL(old.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(old.updatedBy,"") ,'</oldValue><newValue>', IFNULL(old.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(old.creationDate,"") ,'</oldValue><newValue>', IFNULL(old.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(old.updateDate,"") ,'</oldValue><newValue>', IFNULL(old.updateDate,"") ,'</newValue></field>'
	,	'</diff>\n'
	,'</logdata>'
	)
	;
	--	-------------------------------------------------------------------------
	INSERT INTO	log_archive.log_event
	(
		logDate
	,	validStart
	,	validFinish
	,	dmlType
	,	ddlName
	,	ddlKey
	,	logSeq
	,	userName
	,	logYear
	,	logMonth
	,	logWeek
	,	logDay
	,	logQtr
	,	logEntry
	)
	VALUES
	(
		@logDate
	,	@validStart
	,	@validFinish
	,	@dmlType
	,	@ddlName
	,	old.id
	,	old.id
	,	@username
	,	YEAR(CURRENT_DATE)
	,	MONTH(CURRENT_DATE)
	,	WEEK(CURRENT_DATE)
	,	DAY(CURRENT_DATE)
	,	QUARTER(CURRENT_DATE)
	,	@logEntry
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
	INSERT INTO	log_archive.log_msg_locale
	VALUES
	(
		@logDate
	,	old.id
	,	old.keyID
	,	old.locale
	,	old.msgValue
	,	old.firstUsed
	,	old.lastUsed
	,	old.qualityRating
	,	old.createdBy
	,	old.updatedBy
	,	old.creationDate
	,	old.updateDate
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `msg_locale_history`
--

DROP TABLE IF EXISTS `msg_locale_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msg_locale_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `keyID` int(11) NOT NULL,
  `locale` varchar(8) NOT NULL,
  `updateDate` datetime NOT NULL,
  `updatedBy` int(11) DEFAULT NULL,
  `msgValue` text,
  PRIMARY KEY (`id`),
  KEY `msg_locale_history_key` (`keyID`,`locale`,`updateDate`),
  CONSTRAINT `msg_locale_history_key` FOREIGN KEY (`keyID`) REFERENCES `msg_key` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `play_evolutions`
--

DROP TABLE IF EXISTS `play_evolutions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `play_evolutions` (
  `id` int(11) NOT NULL,
  `hash` varchar(255) COLLATE utf8_bin NOT NULL,
  `applied_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `apply_script` text COLLATE utf8_bin,
  `revert_script` text COLLATE utf8_bin,
  `state` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `last_problem` text COLLATE utf8_bin,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `translation_usage`
--

DROP TABLE IF EXISTS `translation_usage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `translation_usage` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `keyID` int(11) NOT NULL,
  `localeID` int(11) NOT NULL,
  `pageName` varchar(100) NOT NULL,
  `pageOrder` varchar(20) DEFAULT NULL,
  `environment` varchar(20) DEFAULT NULL,
  `firstUsed` date DEFAULT NULL,
  `lastUsed` date DEFAULT NULL,
  `synchronizedBatch` varchar(40) DEFAULT NULL,
  `synchronizedDate` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `keyPageEnv` (`keyID`,`localeID`,`pageName`,`environment`),
  KEY `localeFK` (`localeID`),
  CONSTRAINT `keyFK` FOREIGN KEY (`keyID`) REFERENCES `msg_key` (`id`) ON DELETE CASCADE,
  CONSTRAINT `localeFK` FOREIGN KEY (`localeID`) REFERENCES `msg_locale` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=939781 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary table structure for view `vwmsg_key_locale`
--

DROP TABLE IF EXISTS `vwmsg_key_locale`;
/*!50001 DROP VIEW IF EXISTS `vwmsg_key_locale`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `vwmsg_key_locale` (
  `keyID` tinyint NOT NULL,
  `msgKey` tinyint NOT NULL,
  `locale` tinyint NOT NULL,
  `msgValue` tinyint NOT NULL,
  `description` tinyint NOT NULL,
  `js` tinyint NOT NULL,
  `firstUsed` tinyint NOT NULL,
  `lastUsed` tinyint NOT NULL,
  `qualityRating` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Dumping routines for database 'pics_translations'
--
/*!50003 DROP PROCEDURE IF EXISTS `etlFileAssetItem` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES' */ ;
DELIMITER ;;
CREATE DEFINER=`pics_admin`@`%` PROCEDURE `etlFileAssetItem`(
	_fileAssetID	INT(11)
,	_etlAction	VARCHAR(16)
)
BEGIN

###############################################################################
DECLARE	SYSTABLE	VARCHAR(255)	DEFAULT 'file_asset';
DECLARE	SYSRIGHT	VARCHAR(40)	DEFAULT 'ETL-INSERT';
DECLARE	Proc_nm		VARCHAR(255)	DEFAULT 'etlFileAssetItem';
DECLARE	Key_cd		VARCHAR(16)	DEFAULT 'PK';
DECLARE RowExists_fg	BOOLEAN 	DEFAULT FALSE;
DECLARE ProcFailed_fg	BOOLEAN 	DEFAULT FALSE;

DECLARE ERRTABLE	VARCHAR(64) 	DEFAULT SYSTABLE;
DECLARE ERR99000	CONDITION FOR SQLSTATE '99000';
DECLARE MSG99000	VARCHAR(1024) DEFAULT
	CONCAT
	(
		"Error: Invalid action sent. Action must be STAGE or LOAD."
	)
;
DECLARE ERR99001	CONDITION FOR SQLSTATE '99001';
DECLARE MSG99001	VARCHAR(1024) DEFAULT
	CONCAT
	(
		"Error: No matching row in "
	,	ERRTABLE
	,	" for fileAssetID "
	,	_fileAssetID
	,	" . Nothing to process!"
	)
;
###############################################################################
ETL:
BEGIN
	#######################################################################
	#######################################################################
	IF _etlAction IS NULL OR _etlAction = "" THEN SET _etlAction = "STAGE";	END IF;
	#######################################################################


	IF
		_etlAction	= "STAGE"
	OR 	_etlAction	= "LOAD"
	THEN
		SET 	ProcFailed_fg	= FALSE;
	ELSE
		SET ERRTABLE	= "file_asset_item";
		SHOW ERRORS;
		SIGNAL ERR99000
		SET MESSAGE_TEXT	= MSG99000
		,	MYSQL_ERRNO	= 9000
		,	TABLE_NAME	= ERRTABLE
		;
		LEAVE 	ETL;
	END IF
	;


	IF
	EXISTS
	(
		SELECT 	1
		FROM
			file_asset
		WHERE	1=1
		AND	file_asset.id	= _fileAssetID
	)
	THEN
		SET RowExists_fg	= TRUE;
	ELSE
		SET ERRTABLE	= SYSTABLE;
		SHOW ERRORS;
		SIGNAL ERR99001
		SET MESSAGE_TEXT	= MSG99001
		,	MYSQL_ERRNO	= 9001
		,	TABLE_NAME	= ERRTABLE
		;
		LEAVE ETL;
	END IF
	;
	IF
	EXISTS
	(
		SELECT 	1
		FROM
			file_asset_item
		WHERE	1=1
		AND	file_asset_item.fileAssetID	= _fileAssetID
	)
	THEN
		SET RowExists_fg	= TRUE;
	ELSE
		SET ERRTABLE	= "file_asset_item";
		SHOW ERRORS;
		SIGNAL ERR99001
		SET MESSAGE_TEXT	= MSG99001
		,	MYSQL_ERRNO	= 9001
		,	TABLE_NAME	= ERRTABLE
		;
		LEAVE ETL;
	END IF
	;



	SET 	@etlStatus	= "IMPORTED";
	SET 	@processDate	= NOW();
	SET 	@enLocale	= "en";
	SET 	@insAction	= "INSERT";
	SET 	@updAction	= "UPDATE";
	SET 	@ignAction	= "IGNORE";
	SET 	@rejAction	= "REJECT";

	UPDATE
		file_asset_item
	SET
		file_asset_item.etlAction	= @insAction
	,	file_asset_item.etlActionResult	= "This row has no matching key and locale in the database for this msgValue. This row will be inserted into the database."
	WHERE	1=1
	AND	file_asset_item.fileAssetID	= _fileAssetID
	;



	DROP TEMPORARY TABLE IF EXISTS fileDups;
	CREATE TEMPORARY TABLE IF NOT EXISTS	fileDups
	AS
	SELECT
		file_asset_item.msgKey
	,	file_asset_item.locale
	,	file_asset_item.msgValue
	,	COUNT(1) 	dupRows
	FROM
		file_asset_item
	WHERE	1=1
	AND	file_asset_item.fileAssetID	= _fileAssetID
	GROUP BY
		file_asset_item.msgKey
	,	file_asset_item.locale
	HAVING 	dupRows 	> 1
	ORDER BY
		dupRows 	DESC
	;



	DROP TEMPORARY TABLE IF EXISTS tableDups;
	CREATE TEMPORARY TABLE IF NOT EXISTS	tableDups
	SELECT
		file_asset_item.msgKey
	,	file_asset_item.locale
	,	file_asset_item.msgValue
	,	COUNT(1) 	dupRows
	FROM
		file_asset_item
	,	vwmsg_key_locale
	WHERE	1=1
	AND	file_asset_item.fileAssetID	= _fileAssetID
	AND	vwmsg_key_locale.msgKey	= file_asset_item.msgKey
	AND	vwmsg_key_locale.locale	= file_asset_item.locale
	AND	vwmsg_key_locale.msgValue	= file_asset_item.msgValue
	GROUP BY
		file_asset_item.msgKey
	,	file_asset_item.locale
	,	file_asset_item.msgValue
	HAVING 	dupRows 	>= 1
	ORDER BY
		dupRows 	DESC
	;
	UPDATE
		file_asset_item
	,	fileDups
	SET
		file_asset_item.etlAction	= @ignAction
	,	file_asset_item.etlActionResult	= "This row is an exact duplicate of another row in this file. This row will be ignored."
	WHERE 	1=1
	AND	file_asset_item.msgKey	= fileDups.msgKey
	AND	file_asset_item.locale	= fileDups.locale
	AND	file_asset_item.msgValue	= fileDups.msgValue
	;
	UPDATE
		file_asset_item
	,	tableDups
	SET
		file_asset_item.etlAction	= @ignAction
	,	file_asset_item.etlActionResult	= "This row is an exact duplicate of another row in the database table. This row will be ignored."
	WHERE 	1=1
	AND	file_asset_item.msgKey	= tableDups.msgKey
	AND	file_asset_item.locale	= tableDups.locale
	AND	file_asset_item.msgValue	= tableDups.msgValue
	;





	DROP TEMPORARY TABLE IF EXISTS	errRows;
	CREATE TEMPORARY TABLE IF NOT EXISTS	errRows
	SELECT
		file_asset_item.id
	,	file_asset_item.msgKey
	,	file_asset_item.locale
	,	file_asset_item.msgValue
	,	"This row has a missing or empty value for msgKey, locale or msgValue. This row will be rejected."	etlActionResult
	FROM
		file_asset_item
	WHERE	1=1
	AND	file_asset_item.fileAssetID	= _fileAssetID
	AND	NULLIF(file_asset_item.msgKey, "")	IS NULL
	OR	NULLIF(file_asset_item.locale, "")	IS NULL
	OR	NULLIF(file_asset_item.msgValue, "")	IS NULL
	;
	INSERT INTO	errRows
	SELECT
		file_asset_item.id
	,	file_asset_item.msgKey
	,	file_asset_item.locale
	,	file_asset_item.msgValue
	,	"This row has no matching key with locale of 'en' (English) in the database for the msgKey and dialect locale provided. This row will be rejected."
	FROM
		file_asset_item
	WHERE	1=1
	AND	file_asset_item.fileAssetID	= _fileAssetID
	AND	file_asset_item.locale	<> @enLocale
	AND	NULLIF(file_asset_item.msgKey, "")	IS NOT NULL
	AND	NULLIF(file_asset_item.locale, "")	IS NOT NULL
	AND	NULLIF(file_asset_item.msgValue, "")	IS NOT NULL
	AND NOT EXISTS
	(
		SELECT	1
		FROM
			vwmsg_key_locale
		WHERE	1
		AND	vwmsg_key_locale.msgKey = file_asset_item.msgKey
		AND	vwmsg_key_locale.locale	= @enLocale
	)
	;
	UPDATE
		file_asset_item
	,	errRows
	SET
		file_asset_item.etlAction	= @rejAction
	,	file_asset_item.etlActionResult	= errRows.etlActionResult
	WHERE 	1=1
	AND	file_asset_item.id	= errRows.id
	;




	DROP TEMPORARY TABLE IF EXISTS	updRows;
	CREATE TEMPORARY TABLE IF NOT EXISTS	updRows
	SELECT
		file_asset_item.id
	,	file_asset_item.msgKey
	,	file_asset_item.locale
	,	file_asset_item.msgValue
	FROM
		file_asset_item
	JOIN
		vwmsg_key_locale
	ON	vwmsg_key_locale.msgKey = file_asset_item.msgKey
	AND	vwmsg_key_locale.locale	= file_asset_item.locale
	WHERE	1=1
	AND	file_asset_item.fileAssetID	= _fileAssetID
	AND	file_asset_item.etlAction	= @insAction
	AND	NULLIF(file_asset_item.msgKey, "")	IS NOT NULL
	AND	NULLIF(file_asset_item.locale, "")	IS NOT NULL
	AND	NULLIF(file_asset_item.msgValue, "")	IS NOT NULL
	;
	UPDATE
		file_asset_item
	,	updRows
	SET
		file_asset_item.etlAction	= @updAction
	,	file_asset_item.etlActionResult	= "This row has a matching key and locale in the database for this msgValue. This row will update the database."
	WHERE 	1=1
	AND	file_asset_item.id	= updRows.id
	;


	SELECT
		COUNT(1)
	INTO
		@allRows
	FROM
		file_asset_item
	WHERE	1=1
	AND	file_asset_item.fileAssetID	= _fileAssetID
	;

	SELECT
		COUNT(1)
	INTO
		@insRows
	FROM
		file_asset_item
	WHERE	1=1
	AND	file_asset_item.fileAssetID	= _fileAssetID
	AND	file_asset_item.etlAction	= @insAction
	;

	SELECT
		COUNT(1)
	INTO
		@updRows
	FROM
		file_asset_item
	WHERE	1=1
	AND	file_asset_item.fileAssetID	= _fileAssetID
	AND	file_asset_item.etlAction	= @updAction
	;

	SELECT
		COUNT(1)
	INTO
		@errRows
	FROM
		file_asset_item
	WHERE	1=1
	AND	file_asset_item.fileAssetID	= _fileAssetID
	AND	file_asset_item.etlAction	= @rejAction
	;

	SET	@delRows	= 0;
	SET 	@fileDups	= 0;
	SET 	@tableDups	= 0;
	SELECT
		COUNT(1)
	INTO
		@fileDups
	FROM
		fileDups
	;
	SELECT
		COUNT(1)
	INTO
		@tableDups
	FROM
		tableDups
	;


	UPDATE
		file_asset
	SET
		file_asset.etlStatus	= @etlStatus
	,	file_asset.processDate	= @processDate
	,	file_asset.allRows	= @allRows
	,	file_asset.insRows	= @insRows
	,	file_asset.updRows	= @updRows
	,	file_asset.delRows	= @delRows
	,	file_asset.errRows	= @errRows
	,	file_asset.dupRows	= @fileDups + @tableDups
	WHERE	1=1
	AND	file_asset.id	= _fileAssetId
	;




	SELECT
		file_asset.*
	FROM
		file_asset
	WHERE	1=1
	AND	file_asset.id	= _fileAssetID
	;
	IF
		_etlAction	= "STAGE"
	THEN
		LEAVE 	ETL;
	END IF
	;


	INSERT INTO	msg_key
	(
		msgKey
	,	js
	,	creationDate
	)
	SELECT
		msgKey
	,	js
	,	creationDate
	FROM
		file_asset_item
	WHERE	1=1
	AND	file_asset_item.fileAssetID	= _fileAssetID
	AND	file_asset_item.etlAction	= @insAction
	;

	INSERT INTO	msg_locale
	(
		keyID
	,	locale
	,	msgValue
	,	qualityRating
	,	creationDate
	)
	SELECT
		msg_key.id
	,	file_asset_item.locale
	,	file_asset_item.msgValue
	,	2
	,	NOW()
	FROM
		file_asset_item
	JOIN
		msg_key
	ON	msg_key.msgKey	= file_asset_item.msgKey
	WHERE	1=1
	AND	file_asset_item.fileAssetID	= _fileAssetID
	AND	file_asset_item.etlAction	= @insAction
	;

END ETL;
###############################################################################
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_log_request` */;
ALTER DATABASE `pics_translations` CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`pics_translation`@`%` PROCEDURE `sp_log_request`(
	IN in_key_str VARCHAR(100),
	IN in_locale_str VARCHAR(100),
	IN in_environment_str VARCHAR(100),
	IN in_url_str VARCHAR(1000)
	)
BEGIN
DECLARE key_id_int INT;
DECLARE today TIMESTAMP DEFAULT CURRENT_DATE;
SET @today = CURDATE();
INSERT INTO msg_key (msgKey, firstUsed, lastUsed)
VALUES (in_key_str, @today, @today)
ON DUPLICATE KEY UPDATE lastUsed = @today;
SELECT @key_id_int:=id FROM msg_key k WHERE msgKey = in_key_str;
INSERT INTO msg_locale (keyID, locale, firstUsed, lastUsed)
VALUES (@key_id_int, in_locale_str, @today, @today)
ON DUPLICATE KEY UPDATE lastUsed = @today;
INSERT INTO log_environment (keyID, environment, firstUsed, lastUsed)
VALUES (@key_id_int, in_environment_str, @today, @today)
ON DUPLICATE KEY UPDATE lastUsed = @today;
INSERT INTO log_page (keyID, url, firstUsed, lastUsed)
VALUES (@key_id_int, in_url_str, @today, @today)
ON DUPLICATE KEY UPDATE lastUsed = @today;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
ALTER DATABASE `pics_translations` CHARACTER SET utf8 COLLATE utf8_bin ;

--
-- Final view structure for view `vwmsg_key_locale`
--

/*!50001 DROP TABLE IF EXISTS `vwmsg_key_locale`*/;
/*!50001 DROP VIEW IF EXISTS `vwmsg_key_locale`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pics_admin`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `vwmsg_key_locale` AS select `msg_key`.`id` AS `keyID`,`msg_key`.`msgKey` AS `msgKey`,`msg_locale`.`locale` AS `locale`,`msg_locale`.`msgValue` AS `msgValue`,`msg_key`.`description` AS `description`,`msg_key`.`js` AS `js`,`msg_locale`.`firstUsed` AS `firstUsed`,`msg_locale`.`lastUsed` AS `lastUsed`,`msg_locale`.`qualityRating` AS `qualityRating` from (`msg_key` left join `msg_locale` on((`msg_locale`.`keyID` = `msg_key`.`id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-03-05 11:51:35
