/* ============================================ */
/* PRE                                          */
/* ============================================ */

/* ==== Copy reference tables and data ====*/

/** table data to bring over:
 * This should be updated again if the pics_alpha data changes
-app_properties
-audit_type
-pqfquestion_operator
-tempauditquestionmigratemap
-widget	
-widget_user
**/

/* SEE BOTTOM of SQL for app_properties */

/* Create audit_type table */
DROP TABLE IF EXISTS `audit_type`;

CREATE TABLE `audit_type` (
  `auditTypeID` int(10) unsigned NOT NULL auto_increment,
  `auditName` varchar(100) NOT NULL,
  `description` varchar(255) default NULL,
  `hasMultiple` tinyint(3) unsigned NOT NULL,
  `isScheduled` tinyint(3) unsigned NOT NULL,
  `hasAuditor` tinyint(3) unsigned NOT NULL,
  `hasRequirements` tinyint(3) unsigned NOT NULL,
  `canContractorView` tinyint(3) unsigned NOT NULL,
  `canContractorEdit` tinyint(3) unsigned NOT NULL,
  `monthsToExpire` tinyint(3) default NULL,
  `dateToExpire` datetime default NULL,
  `legacyCode` varchar(7) default NULL,
  `displayOrder` tinyint(4) default 100,
PRIMARY KEY  (`auditTypeID`),
  UNIQUE KEY `auditName` (`auditName`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Data for the table `audit_type` */

insert  into `audit_type`(`auditTypeID`,`auditName`,`description`,`hasMultiple`,`isScheduled`,`hasAuditor`,`hasRequirements`,`canContractorView`,`canContractorEdit`,`monthsToExpire`,`dateToExpire`,`legacyCode`,`displayOrder`) 
values 
(1,'PQF','prequalification that every contractor must fill out',0,0,1,1,1,1,NULL,'2009-03-01 00:00:00','PQF',20),
(2,'Desktop Audit','desktop audit',0,0,1,1,1,0,36,NULL,'Desktop',50),
(3,'Office Audit','office audit',0,1,1,1,1,0,36,NULL,'Office',60),
(4,'Desktop Audit (NCMS)','old imported ncms desktop audits',0,0,0,0,1,0,36,NULL,NULL,100),
(5,'Field Audit','generic field audit',1,1,1,1,1,0,36,NULL,'Field',80),
(6,'D&A Audit','drug and alcohol audit',0,0,1,1,1,0,36,NULL,'DA',70),
(7,'Evaluation','operator performed contractor evaluations',1,0,0,0,0,0,36,NULL,NULL,100),
(8,'Paramount Site Agreement','Terms and conditions for contractors working at Paramount',0,0,0,0,1,0,36,NULL,NULL,100),
(9,'Welcome Call','the initial welcome call we give to contractors',0,0,1,0,0,0,3,NULL,NULL,10);

/*Table structure for table `pqfquestion_operator` */

DROP TABLE IF EXISTS `pqfquestion_operator`;

CREATE TABLE `pqfquestion_operator` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `opID` int(10) unsigned NOT NULL,
  `questionID` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `opIDquestion` (`opID`,`questionID`),
  KEY `questionID` (`questionID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Data for the table `pqfquestion_operator` */

insert  into `pqfquestion_operator`(`id`,`opID`,`questionID`) values (1,16,401),(2,16,755);

/*Table structure for table `tempauditquestionmigratemap` */

DROP TABLE IF EXISTS `tempauditquestionmigratemap`;

CREATE TABLE `tempauditquestionmigratemap` (
  `auditQuestionID` mediumint(9) default NULL,
  `pqfQuestionID` mediumint(9) default NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Data for the table `tempauditquestionmigratemap` */

insert  into `tempauditquestionmigratemap`(`auditQuestionID`,`pqfQuestionID`) values (319,1281),(321,1710),(19,1284),(4,1711),(18,1712),(23,1293),(27,1713),(24,1714),(98,1298),(110,1299),(21,1300),(26,1301),(29,1302),(30,1303),(31,1304),(65,1306),(87,1307),(94,1715),(97,1308),(111,1333),(33,1334),(34,1335),(36,1336),(39,1338),(72,1339),(150,1340),(43,1341),(45,1343),(46,1344),(47,1345),(48,1346),(49,1347),(50,1348),(63,1349),(52,1350),(57,1351),(58,1353),(59,1354),(66,1356),(67,1384),(68,1358),(69,1360),(70,1361),(71,1362),(73,1363),(76,1716),(78,1365),(79,1367),(88,1368),(89,1370),(92,1371),(93,1372),(38,1352),(51,1355),(77,1359),(35,1364),(40,1366),(60,1434),(85,1380),(117,1375),(124,1379),(122,1381);

/*Table structure for table `widget` */

DROP TABLE IF EXISTS `widget`;

CREATE TABLE `widget` (
  `widgetID` int(10) unsigned NOT NULL auto_increment,
  `caption` varchar(50) NOT NULL,
  `widgetType` varchar(30) NOT NULL,
  `synchronous` tinyint(3) unsigned NOT NULL default '0',
  `url` varchar(255) default NULL,
  `requiredPermission` varchar(100) default NULL,
  `chartType` varchar(30) default NULL,
  PRIMARY KEY  (`widgetID`),
  UNIQUE KEY `caption` (`caption`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Data for the table `widget` */

insert  into `widget`(`widgetID`,`caption`,`widgetType`,`synchronous`,`url`,`requiredPermission`,`chartType`) values (1,'Contractor Count by Flag Color','Chart',0,'ChartXMLFlagCount.action',NULL,'Pie2D'),(2,'Recently Registered Contractors','Html',0,'ContractorRegistrationAjax.action',NULL,NULL),(3,'Upcoming Audits','Html',0,'UpcomingAuditsAjax.action',NULL,NULL),(4,'Recently Closed Audits','Html',0,'ClosedAuditsAjax.action',NULL,NULL),(5,'Contractors by Trade','Chart',0,'ChartXMLTradeCount.action',NULL,'Column3D'),(6,'Open Tasks','Html',0,'ContractorTasksAjax.action',NULL,NULL),(7,'Contractor Information','Html',0,'ContractorStatsAjax.action',NULL,NULL),(8,'My Audits By Month','Chart',0,'ChartXMLAuditsClosed.action',NULL,'MSColumn3D'),(9,'My Assigned Audits','Html',0,'MyAssignedAuditsAjax.action',NULL,NULL),(10,'My User Profile','Html',0,'UserProfileAjax.action',NULL,NULL),(11,'Facilities','Html',0,'ContractorFacilitiesAjax.action',NULL,NULL);

/*Table structure for table `widget_user` */

DROP TABLE IF EXISTS `widget_user`;

CREATE TABLE `widget_user` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `widgetID` int(10) unsigned NOT NULL,
  `userID` int(10) unsigned NOT NULL,
  `expanded` tinyint(3) unsigned NOT NULL default '1',
  `column` tinyint(4) NOT NULL default '1',
  `sortOrder` tinyint(4) NOT NULL default '10',
  `customConfig` varchar(255) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `widgetID` (`userID`,`widgetID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Data for the table `widget_user` */

insert  into `widget_user`(`id`,`widgetID`,`userID`,`expanded`,`column`,`sortOrder`,`customConfig`) values (9,5,616,1,2,15,NULL),(2,2,941,1,2,10,NULL),(10,4,910,1,2,20,NULL),(5,1,616,1,1,10,NULL),(6,2,616,1,1,20,NULL),(7,3,616,1,2,10,NULL),(8,4,616,1,2,20,NULL),(15,8,910,1,1,20,NULL),(13,6,1,1,2,10,NULL),(14,7,1,1,1,10,NULL),(17,9,941,1,1,10,NULL),(18,9,910,1,2,10,NULL),(19,10,910,1,1,1,NULL),(20,10,941,1,1,1,NULL),(21,10,616,1,1,1,NULL),(22,11,1,1,1,10,NULL);


/* ==== Create new tables with empty data ====*/

/* Create contractor_audit table */
drop table IF EXISTS `contractor_audit`;
create table `contractor_audit`(
	`auditID` int(10) unsigned NOT NULL  auto_increment  , 
	`auditTypeID` int(10) unsigned NOT NULL   , 
	`conID` int(10) unsigned NOT NULL   , 
	`createdDate` datetime NOT NULL   , 
	`auditStatus` varchar(10) COLLATE latin1_swedish_ci NOT NULL  DEFAULT 'Pending'  , 
	`expiresDate` datetime NULL , 
	`auditorID` int(10) unsigned NULL   , 
	`assignedDate` datetime NULL   , 
	`scheduledDate` datetime NULL   , 
	`completedDate` datetime NULL   , 
	`closedDate` datetime NULL   , 
	`requestedByOpID` int(10) unsigned NULL   , 
	`auditLocation` varchar(45) COLLATE latin1_swedish_ci NULL   , 
	`percentComplete` tinyint(3) unsigned NOT NULL  DEFAULT '0'  , 
	`percentVerified` tinyint(3) unsigned NOT NULL  DEFAULT '0'  , 
	`canDelete` tinyint(3) unsigned   NOT NULL DEFAULT '1' ,
	`contractorConfirm` datetime NULL ,
	`auditorConfirm` datetime NULL ,
	PRIMARY KEY (`auditID`) , 
	UNIQUE KEY `auditTypeID_conID_createdDate`(`conID`,`auditTypeID`,`createdDate`) , 
	KEY `auditTypeStatus`(`auditTypeID`,`auditStatus`) 
)ENGINE=MyISAM DEFAULT CHARSET='latin1';


/* ==== Alter tables ====*/

/* Alter accounts table - add nullable*/
alter table `accounts` 
	change `password` `password` varchar(50)  COLLATE latin1_swedish_ci NULL after `username`, 
	change `passwordChange` `passwordChange` date   NULL DEFAULT '0000-00-00' after `password`, 
	change `lastLogin` `lastLogin` datetime   NULL DEFAULT '2000-01-01 00:00:00' after `passwordChange`, 
	change `contact` `contact` varchar(50)  COLLATE latin1_swedish_ci NULL after `lastLogin`, 
	change `address` `address` varchar(50)  COLLATE latin1_swedish_ci NULL after `contact`, 
	change `city` `city` varchar(50)  COLLATE latin1_swedish_ci NULL after `address`, 
	change `state` `state` char(2)  COLLATE latin1_swedish_ci NULL after `city`, 
	change `zip` `zip` varchar(50)  COLLATE latin1_swedish_ci NULL after `state`, 
	change `phone` `phone` varchar(50)  COLLATE latin1_swedish_ci NULL after `zip`, 
	change `phone2` `phone2` varchar(50)  COLLATE latin1_swedish_ci NULL after `phone`, 
	change `fax` `fax` varchar(20)  COLLATE latin1_swedish_ci NULL after `phone2`, 
	change `email` `email` varchar(50)  COLLATE latin1_swedish_ci NULL after `fax`, 
	change `web_URL` `web_URL` varchar(50)  COLLATE latin1_swedish_ci NULL after `email`, 
	change `industry` `industry` varchar(50)  COLLATE latin1_swedish_ci NULL after `web_URL`, 
	change `seesAll_B` `seesAll_B` char(1)  COLLATE latin1_swedish_ci NULL DEFAULT 'N' after `dateCreated`, 
	change `sendActivationEmail_B` `sendActivationEmail_B` char(1)  COLLATE latin1_swedish_ci NULL DEFAULT 'N' after `seesAll_B`, 
	change `activationEmails_B` `activationEmails_B` varchar(155)  COLLATE latin1_swedish_ci NULL after `sendActivationEmail_B`, 
	change `emailConfirmedDate` `emailConfirmedDate` date   NULL DEFAULT '0000-00-00' after `activationEmails_B`, COMMENT='';

/* Alter auditquestions table - add temp pqfQuestionID */
alter table `auditquestions` 
	add column `pqfQuestionID` mediumint(9)   NULL after `linkText6`, COMMENT='';


/* Alter contractor_info table - make nullabe*/
alter table `contractor_info` 
	change `taxID` `taxID` varchar(100)  COLLATE latin1_swedish_ci NULL after `id`, 
	change `main_trade` `main_trade` varchar(100)  COLLATE latin1_swedish_ci NULL after `taxID`, 
	change `trades` `trades` text  COLLATE latin1_swedish_ci NULL after `main_trade`, 
	change `subTrades` `subTrades` text  COLLATE latin1_swedish_ci NULL after `trades`, 
	change `logo_file` `logo_file` varchar(50)  COLLATE latin1_swedish_ci NULL after `subTrades`, 
	change `brochure_file` `brochure_file` enum('No','Yes')  COLLATE latin1_swedish_ci NULL DEFAULT 'No' after `logo_file`, 
	change `description` `description` text  COLLATE latin1_swedish_ci NULL after `brochure_file`, 
	change `status` `status` enum('Inactive','Active')  COLLATE latin1_swedish_ci NULL DEFAULT 'Inactive' after `description`, 
	change `certs` `certs` tinyint(4)   NULL DEFAULT '0' after `status`, 
	change `welcomeEmailDate` `welcomeEmailDate` date   NULL DEFAULT '0000-00-00' after `accountDate`, 
	change `paid` `paid` enum('No','Yes')  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'No' after `welcomeEmailDate`, 
	change `lastPayment` `lastPayment` date   NULL DEFAULT '0000-00-00' after `paid`, 
	change `lastPaymentAmount` `lastPaymentAmount` smallint(5) unsigned   NULL after `lastPayment`, 
	change `lastInvoiceDate` `lastInvoiceDate` date   NULL DEFAULT '0000-00-00' after `lastPaymentAmount`, 
	change `accountNewComplete` `accountNewComplete` char(1)  COLLATE latin1_swedish_ci NULL DEFAULT 'N' after `lastInvoiceDate`, 
	change `notes` `notes` mediumtext  COLLATE latin1_swedish_ci NULL after `accountNewComplete`, 
	change `adminNotes` `adminNotes` mediumtext  COLLATE latin1_swedish_ci NULL after `notes`, 
	change `mustPay` `mustPay` enum('Yes','No')  COLLATE latin1_swedish_ci NULL DEFAULT 'No' after `adminNotes`, 
	change `paymentExpires` `paymentExpires` date   NULL DEFAULT '0000-00-00' after `mustPay`, 
	change `lastAnnualUpdateEmailDate` `lastAnnualUpdateEmailDate` date   NULL DEFAULT '0000-00-00' after `paymentExpires`, 
	change `requestedByID` `requestedByID` mediumint(9)   NULL DEFAULT '0' after `lastAnnualUpdateEmailDate`, 
	change `billingAmount` `billingAmount` smallint(6)  NOT NULL DEFAULT '0' after `billingCycle`, 
	change `hasExpiredCerts` `hasExpiredCerts` enum('No','Yes')  COLLATE latin1_swedish_ci NULL DEFAULT 'No' after `isExempt`, 
	change `isOnlyCerts` `isOnlyCerts` enum('No','Yes')  COLLATE latin1_swedish_ci NULL DEFAULT 'No' after `hasExpiredCerts`, 
	change `secondContact` `secondContact` varchar(50)  COLLATE latin1_swedish_ci NULL after `isOnlyCerts`, 
	change `secondPhone` `secondPhone` varchar(50)  COLLATE latin1_swedish_ci NULL after `secondContact`, 
	change `secondEmail` `secondEmail` varchar(50)  COLLATE latin1_swedish_ci NULL after `secondPhone`, 
	change `billingContact` `billingContact` varchar(50)  COLLATE latin1_swedish_ci NULL after `secondEmail`, 
	change `billingPhone` `billingPhone` varchar(50)  COLLATE latin1_swedish_ci NULL after `billingContact`, 
	change `billingEmail` `billingEmail` varchar(50)  COLLATE latin1_swedish_ci NULL after `billingPhone`, 
	change `newBillingAmount` `newBillingAmount` smallint(5) unsigned  NOT NULL DEFAULT '0' after `membershipDate`, 
	change `payingFacilities` `payingFacilities` smallint(5) unsigned   NOT NULL DEFAULT '0' after `newBillingAmount`, 
	change `welcomeCallDate` `welcomeCallDate` date   NULL after `payingFacilities`, 
	change `welcomeAuditor_id` `welcomeAuditor_id` mediumint(8) unsigned   NULL after `welcomeCallDate`, 
	change `annualUpdateEmails` `annualUpdateEmails` tinyint(4)   NULL DEFAULT '0' after `riskLevel`, 
	add column `oqEmployees` enum('No','Yes')  COLLATE latin1_swedish_ci NULL after `annualUpdateEmails`, COMMENT='';

/* Alter facilities table - add primary key and unique key */
alter table `facilities` 
	add column `facilityID` int(10) unsigned   NOT NULL auto_increment first, 
	change `corporateID` `corporateID` mediumint(8) unsigned   NOT NULL DEFAULT '0' after `facilityID`, 
	add UNIQUE KEY `opID`(`opID`,`corporateID`), 
	drop key `PRIMARY`, add PRIMARY KEY(`facilityID`), COMMENT='';

/* Alter flagcriteria table - add primaray key, change few columns */
alter table `flagcriteria` 
	add column `criteriaID` int(10) unsigned   NOT NULL auto_increment first, 
	change `opID` `opID` mediumint(8) unsigned   NOT NULL after `criteriaID`, 
	change `isChecked` `isChecked` enum('No','Yes')  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'No' after `flagStatus`,
	add column `validationRequired` tinyint(3) unsigned   NOT NULL DEFAULT '0' after `value`,
	add UNIQUE KEY `opFlagQuestion`(`opID`,`flagStatus`,`questionID`), 
	drop key `opID`, 
	drop key `PRIMARY`, add PRIMARY KEY(`criteriaID`), 
	add KEY `questionID`(`questionID`), COMMENT='';

/* Alter flagoshacriteria table - add primary key, change column types*/
alter table `flagoshacriteria` 
	add column `criteriaID` int(10) unsigned   NOT NULL auto_increment first, 
	change `opID` `opID` mediumint(9)   NOT NULL after `criteriaID`, 
	change `flagStatus` `flagStatus` enum('Red','Amber')  COLLATE latin1_swedish_ci NOT NULL after `opID`, 
	change `flagLwcr` `flagLwcr` enum('No','Yes')  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'No' after `flagStatus`, 
	change `lwcrHurdle` `lwcrHurdle` decimal(4,2)   NOT NULL after `flagLwcr`, 
	change `flagTrir` `flagTrir` enum('No','Yes')  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'No' after `lwcrTime`, 
	change `trirHurdle` `trirHurdle` decimal(4,2)   NOT NULL after `flagTrir`, 
	change `flagFatalities` `flagFatalities` enum('No','Yes')  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'No' after `trirTime`, 
	change `fatalitiesHurdle` `fatalitiesHurdle` decimal(4,2)   NOT NULL after `flagFatalities`, 
	drop key `PRIMARY`, add PRIMARY KEY(`criteriaID`), add UNIQUE KEY `opID`(`opID`,`flagStatus`), COMMENT='';

/* Alter flags table - add primary key, add force flag columns */
alter table `flags` 
	add column `id` int(10) unsigned   NOT NULL auto_increment first, 
	change `opID` `opID` mediumint(8) unsigned   NOT NULL after `id`, 
	change `flag` `flag` enum('Red','Amber','Green')  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'Red' after `conID`, 
	add column `forceFlag` enum('Red','Amber','Green')  COLLATE latin1_swedish_ci NULL after `lastUpdate`, 
	add column `forceBegin` date   NULL after `forceFlag`, 
	add column `forceEnd` date   NULL after `forceBegin`, 
	drop key `opID`, add UNIQUE KEY `opID`(`opID`,`conID`), 
	add PRIMARY KEY(`id`), COMMENT='';

/* Alter generalcontractors table - add primary columng, not nullable, and force flag columns*/
alter table `generalcontractors` 
	add column `id` mediumint(8) unsigned   NOT NULL auto_increment first, 
	change `genID` `genID` mediumint(9)   NOT NULL DEFAULT '0' after `id`, 
	change `subID` `subID` mediumint(9)   NOT NULL DEFAULT '0' after `genID`, 
	change `dateAdded` `dateAdded` date   NOT NULL DEFAULT '0000-00-00' after `subID`, 
	add column `forceFlag` enum('Red','Amber','Green')  COLLATE latin1_swedish_ci NULL after `workStatus`, 
	add column `forceBegin` date   NULL after `forceFlag`, 
	add column `forceEnd` date   NULL after `forceBegin`, 
	add PRIMARY KEY(`id`), COMMENT='';

/* Alter operators table - make not nullable*/
alter table `operators` 
	change `isCorporate` `isCorporate` enum('No','Yes')  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'No' after `id`, 
	change `activationEmails` `activationEmails` varchar(255)  COLLATE latin1_swedish_ci NOT NULL after `isCorporate`, 
	change `canSeeInsurance` `canSeeInsurance` enum('No','Yes')  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'No' after `doContractorsPay`, 
	change `approvesRelationships` `approvesRelationships` enum('No','Yes')  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'No' after `canSeeInsurance`, 
	change `doSendActivationEmail` `doSendActivationEmail` enum('No','Yes')  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'No' after `activationEmails`, 
	change `seesAllContractors` `seesAllContractors` enum('No','Yes')  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'No' after `doSendActivationEmail`, 
	change `insuranceAuditor_id` `insuranceAuditor_id` mediumint(8) unsigned   NULL after `approvesRelationships`, 
	drop column `flagEmr`, 
	drop column `emrHurdle`, 
	drop column `emrTime`, 
	drop column `flagLwcr`, 
	drop column `lwcrHurdle`, 
	drop column `lwcrTime`, 
	drop column `flagTrir`, 
	drop column `trirHurdle`, 
	drop column `trirTime`, 
	drop column `flagFatalities`, 
	drop column `fatalitiesHurdle`, 
	drop column `flagQ318`, 
	drop column `flagQ1385`, COMMENT='';

/* Alter pqfcateogies table - add auditTypeID */
alter table `pqfcategories` 
	change `catID` `catID` smallint(6)   NOT NULL auto_increment first, 
	add column `auditTypeID` int(11)   NULL after `catID`, 
	change `category` `category` varchar(255)  COLLATE latin1_swedish_ci NOT NULL after `auditTypeID`, 
	add UNIQUE KEY `auditTypeCategory`(`auditTypeID`,`category`), COMMENT='';

/* Alter pqfdata table - add primary key, auditID, */
/* this one takes a long time to execute */
alter table `pqfdata` 
	add column `dataID` bigint(20) unsigned   NOT NULL auto_increment first, 
	add column `auditID` mediumint(9) unsigned   NULL after `dataID`, 
	change `questionID` `questionID` smallint(6) unsigned   NOT NULL after `auditID`, 
	change `conID` `conID` mediumint(9) unsigned   NOT NULL after `questionID`, 
	change `num` `num` smallint(6) unsigned   NOT NULL DEFAULT '0' after `conID`, 
	change `auditorID` `auditorID` smallint(5) unsigned   NULL after `verifiedAnswer`, 
	change `isCorrect` `isCorrect` enum('No','Yes')  COLLATE latin1_swedish_ci NULL after `auditorID`, 
	drop key `PRIMARY`, add PRIMARY KEY(`dataID`), 
	add KEY `conID`(`conID`),
	add UNIQUE KEY `questionContractor`(`auditID`,`questionID`), COMMENT='';



/* Alter pqfquestions table - make nullabe*/
alter table `pqfquestions` 
	change `okAnswer` `okAnswer` set('Yes','No','NA')  COLLATE latin1_swedish_ci NULL after `hasRequirement`, 
	change `requirement` `requirement` mediumtext  COLLATE latin1_swedish_ci NULL after `okAnswer`, 
	change `dependsOnQID` `dependsOnQID` smallint(6)   NULL after `isRequired`, 
	change `dependsOnAnswer` `dependsOnAnswer` varchar(100)  COLLATE latin1_swedish_ci NULL after `dependsOnQID`, 
	change `title` `title` varchar(250)  COLLATE latin1_swedish_ci NULL after `lastModified`, 
	change `link` `link` varchar(250)  COLLATE latin1_swedish_ci NULL after `isGroupedWithPrevious`, 
	change `linkText` `linkText` varchar(250)  COLLATE latin1_swedish_ci NULL after `link`, 
	change `linkURL1` `linkURL1` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkText`, 
	change `linkText1` `linkText1` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkURL1`, 
	change `linkURL2` `linkURL2` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkText1`, 
	change `linkText2` `linkText2` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkURL2`, 
	change `linkURL3` `linkURL3` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkText2`, 
	change `linkText3` `linkText3` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkURL3`, 
	change `linkURL4` `linkURL4` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkText3`, 
	change `linkText4` `linkText4` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkURL4`, 
	change `linkURL5` `linkURL5` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkText4`, 
	change `linkText5` `linkText5` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkURL5`, 
	change `linkURL6` `linkURL6` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkText5`, 
	change `linkText6` `linkText6` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkURL6`, COMMENT='';

/* Alter users table */
alter table `users` 
	change `dateCreated` `dateCreated` datetime   NOT NULL after `isActive`, 
	change `lastLogin` `lastLogin` datetime   NULL after `dateCreated`, COMMENT='';

/* Alter table in Second database */
alter table `pqfcatdata` 
	add column `catDataID` int(10) unsigned   NOT NULL auto_increment first, 
	change `catID` `catID` smallint(6) unsigned   NOT NULL DEFAULT '0' after `catDataID`, 
	add column `auditID` mediumint(9) unsigned   NULL after `catID`, 
	change `requiredCompleted` `requiredCompleted` smallint(6) unsigned   NOT NULL DEFAULT '0' after `auditID`, 
	change `numRequired` `numRequired` smallint(6) unsigned   NOT NULL DEFAULT '0' after `requiredCompleted`, 
	change `numAnswered` `numAnswered` smallint(6) unsigned   NOT NULL DEFAULT '0' after `numRequired`, 
	change `applies` `applies` enum('Yes','No')  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'Yes' after `numAnswered`, 
	add column `override` tinyint DEFAULT 0,
	drop key `conID`, 
	drop key `PRIMARY`, add PRIMARY KEY(`catDataID`), COMMENT='';


/* ==== Fix null data ====*/

update pqfdata set  isCorrect = null
	where  isCorrect = '';

update pqfdata set wasChanged = null where wasChanged = '';

update accounts set industry = null where industry = '';

insert into pqfquestions (questionID, subCategoryID, number, question)
	values (0, 0, 1, 'Average EMR');

update pqfquestions set questionID=0
	where question like 'Average%';

update operators set doSendActivationEmail = 'No'
	where doSendActivationEmail not in ('No', 'Yes') or doSendActivationEmail is null;

/*Update questionType field to Manual for office audit in pqfquestions*/
update pqfquestions set questionType = 'Manual'
	where subcategoryid in 
		(select subcatid from pqfsubcategories where categoryid in
			(select catId from pqfcategories where audittypeid=3)); 

/* Update the dependsOnAnswer to Yes* for California */
update pqfquestions set dependsOnQID = 1622, dependsOnAnswer = 'Yes*', isRequired='Depends' 
where questionid = 401;

/************************/
/* cleanup orphaned/incorrect data */

delete from generalcontractors where subID BETWEEN 1087 and 1091;
delete from flags where opID not in (select id from accounts where type = 'Operator');
delete from flags where conID not in (select id from accounts where type = 'Contractor');

delete from contractor_info where id in 
(select id from accounts where type <> 'Contractor');

DELETE from pqfCatData where catID NOT IN
(select catID from pqfCategories);

DELETE from pqfCatData where conID not in
(select id from contractor_info);

delete from flags
where opID in (select id from accounts where type = 'Contractor');

update contractor_info set welcomeAuditor_id = 917 where welcomeAuditor_id = 2823;
update contractor_info set welcomeAuditor_id = 940 where welcomeAuditor_id = 3224;
update contractor_info set welcomeAuditor_id = 937 where welcomeAuditor_id = 3225;
update contractor_info set welcomeAuditor_id = 938 where welcomeAuditor_id = 3226;
update contractor_info set welcomeAuditor_id = 941 where welcomeAuditor_id = 3487;
update contractor_info set welcomeAuditor_id = 943 where welcomeAuditor_id = 3486;

/* Create audit_operator table */
drop table IF EXISTS `audit_operator`;
create table `audit_operator`(
	`auditOperatorID` int(10) unsigned NOT NULL  auto_increment  , 
	`auditTypeID` int(10) unsigned NOT NULL   , 
	`opID` int(10) unsigned NOT NULL   ,
	`canSee` tinyint(3) unsigned NOT NULL DEFAULT '1', 
	`minRiskLevel` tinyint(3) unsigned NOT NULL   , 
	`orderedCount` int(10) unsigned NOT NULL DEFAULT '0'   , 
	`orderDate` datetime NULL   , 
	`requiredForFlag` varchar(10) COLLATE latin1_swedish_ci NULL   , 
	PRIMARY KEY (`auditOperatorID`) , 
	UNIQUE KEY `auditTypeID_opID`(`opID`,`auditTypeID`) , 
	KEY `auditTypeID`(`auditTypeID`) 
)ENGINE=MyISAM DEFAULT CHARSET='latin1';

insert into audit_operator (auditTypeID, opID, canSee, minRiskLevel, requiredForFlag)
	(select 1 AS a, id, 1, 1, 'Red' AS b from operators where canSeePQF='Yes');
insert into audit_operator (auditTypeID, opID, canSee, minRiskLevel,requiredForFlag)
	(select 2 AS a, id, 1, 2, 'Amber' AS b from operators where canSeeDesktop='Yes');
insert into audit_operator (auditTypeID, opID, canSee, minRiskLevel,requiredForFlag)
	(select 3 AS a, id, 1, 2, 'Amber' AS b from operators where canSeeOffice='Yes');
insert into audit_operator (auditTypeID, opID, canSee, minRiskLevel,requiredForFlag)
	(select 5 AS a, id, 1, 0, null AS b from operators where canSeeField='Yes');
insert into audit_operator (auditTypeID, opID, canSee, minRiskLevel,requiredForFlag)
	(select 6 AS a, id, 1, 0, null AS b from operators where canSeeDA='Yes');

insert into useraccess 
	(select id, 'AuditVerification', 1, 1, 0, 0, NOW(), 941 
	 from users where id in (935,959,1029));


/* Populate pqfcategories.auditTypeID */

update pqfcategories p ,audit_type a 
	set p.auditTypeid=a.auditTypeid
	where p.auditType=a.legacyCode;

/* copy the medium risk matrix to high risk */
delete from pqfOpMatrix where riskLevel = 3;

insert into pqfOpMatrix (catID, opID, riskLevel)
select catID, opID, 3 from pqfOpMatrix where riskLevel = 2;

/* ===== WELCOME CALL ====*/


insert into contractor_audit (auditTypeID, conID, createdDate, expiresDate, auditStatus, auditorID, assignedDate, completedDate, closedDate, canDelete, percentComplete)
select 9, c.id, welcomeCallDate, ADDDATE(welcomeCallDate,INTERVAL 3 MONTH), 'Active', welcomeAuditor_id, welcomeCallDate, welcomeCallDate, welcomeCallDate, 0, 100
from contractor_info c
join accounts a on c.id = a.id
where welcomeCallDate > '2001-01-01';

insert into contractor_audit (auditTypeID, conID, createdDate, auditStatus, auditorID, canDelete)
select 9, c.id, a.dateCreated, 'Pending', welcomeAuditor_id, 0
from contractor_info c
join accounts a on c.id = a.id
where a.dateCreated > '2008-03-01'
and c.welcomeCallDate = '0000-00-00';

/* ==== PQF ====*/

/* migrate pqf info from contractor_info to contractor_audit */
insert into contractor_audit (auditTypeID,conID,createdDate ,
	auditorID,completedDate, closedDate,percentComplete)
	(
	  select 1, c.id,a.dateCreated, pqfAuditor_id,pqfSubmittedDate,
	  pqfSubmittedDate,pqfPercent from contractor_info c join accounts a on a.id=c.id
	);

/* Set pqf expiration date */
update contractor_audit
	set expiresDate = '2009-03-01'
	where audittypeID = 1;

/* Set pqf expiration date */
update contractor_audit
	set createdDate = '2008-01-01'
	where audittypeID = 1 AND createdDate < '2008-01-01';


/* ==== Desktop ==== */

/* Migrate desktop info from contractor_info to contractor_audit */
insert into contractor_audit (auditTypeID,conID,createdDate,
	expiresDate, auditorID,assignedDate, completedDate, closedDate,percentComplete,
	percentVerified)
	(
	  select 2, id, desktopAssignedDate,desktopValidUntilDate,desktopAuditor_id, desktopAssignedDate, 
	  desktopSubmittedDate, desktopClosedDate, desktopPercent, desktopVerifiedPercent
	    from contractor_info
	    where (desktopAssignedDate <> '0000-00-00' OR desktopSubmittedDate <> '0000-00-00')
	    AND hasncmsdesktop='No'
	);

/* ==== NCMS ==== */

/* Migrate desktop info from contractor_info to contractor_audit */
insert into contractor_audit (auditTypeID,conID,createdDate,
	expiresDate, closedDate)
	(
	  select 4, conID, lastReview, lastReview + INTERVAL 3 YEAR, lastReview
	    from ncms_desktop
	    where conID > 0
	);

/* ==== DA ==== */

/* Migrate DA desktop info from contractor_info to contractor_audit */
insert into contractor_audit (auditTypeID,conID,createdDate,
	auditorID,assignedDate, completedDate, closedDate,percentComplete,
	percentVerified)
	(
	  select 6, id,daAssignedDate,daAuditor_id, daAssignedDate, 
	  daSubmittedDate, daClosedDate, daPercent, daVerifiedPercent
	    from contractor_info
	    where (daAssignedDate <> '0000-00-00' OR daSubmittedDate <> '0000-00-00')
	);

update contractor_audit
	set expiresDate = adddate(completedDate, interval 3 year)
	where completeddate  <> '0000-00-00' AND auditTypeID > 1;

/* ==== OFFICE ==== */

alter table `contractor_info` 
	add column `tempAuditDateTime` datetime default NULL;

update contractor_info set tempAuditDateTime = auditDate
	where auditDate <> '0000-00-00';

update contractor_info set tempAuditDateTime = 
	str_to_date(concat(auditDate,' ',auditHour,' ',auditAmPm), 
	'%Y-%m-%d %h %p')
	where str_to_date(concat(auditDate,' ',auditHour,' ',auditAmPm), 
	'%Y-%m-%d %h %p')
	IS NOT null;

update contractor_info set tempAuditDateTime = 
	str_to_date(concat(auditDate,' ',auditHour,' ',auditAmPm), 
	'%Y-%m-%d %h:%i %p')
	where str_to_date(concat(auditDate,' ',auditHour,' ',auditAmPm), 
	'%Y-%m-%d %h:%i %p')
	IS NOT null;

insert into contractor_audit (auditTypeID,conID,createdDate,
	expiresDate,auditorID,assignedDate,scheduledDate,completedDate,
	closedDate,auditLocation)
	(
	  select 3, id,assignedDate,
	  auditValidUntilDate,auditor_id, assignedDate, tempAuditDateTime,auditCompletedDate,
	  auditClosedDate,auditLocation
	  from contractor_info
	  where (assignedDate <> '0000-00-00' OR auditcompletedDate <> '0000-00-00')
	);

update contractor_audit
	set percentComplete = 100 where completedDate <> '0000-00-00' and auditTypeID=3;

update contractor_audit
	set percentVerified = 100 where closedDate <> '0000-00-00' and auditTypeID=3;

update contractor_audit
	set expiresDate = adddate(completedDate, interval 3 year)
	where closeddate  <> '0000-00-00'
	AND auditTypeID=3;

/*****transfer Office Audit data***********/

/* Alter auditData table - add tempdateverified, tempwaschanged */
alter table `auditdata` 
	add column `tempdateverified` date   NULL,
	add column `tempwaschanged` varchar(50)   NULL;

update auditdata	
	set tempwaschanged = 'Yes'
	where ok='No';

update auditdata
	set tempdateVerified=dateProgramComplete
	where dateProgramComplete <> '0000-00-00';
update auditdata
	set tempdateVerified=dateClassComplete
	where dateClassComplete <> '0000-00-00';
update auditdata
	set tempdateVerified=dateReqComplete
	where dateReqComplete <> '0000-00-00';


/* delete audits that already have a new office audit */
delete from auditData where con_id in (808,1654);

/* delete all the new office pqf data for a contractor */
delete from pqfdata where conID in (70,249)
	and questionid in
	(
	select questionID from
	pqfquestions pq inner join pqfsubcategories ps 
	  on (ps.subCatId=pq.subCategoryID)
	inner join pqfcategories pc
	  on  (pc.catid=ps.categoryID)
	where auditType='Office'
	);


insert into flagcriteria (criteriaID, opID, questionID, flagStatus, isChecked, questionType, comparison, value, validationRequired)
values (1573, 16, 401, 'Red', 'Yes', "Exists", "=", "", 1);

insert into flagcriteria (criteriaID, opID, questionID, flagStatus, isChecked, questionType, comparison, value, validationRequired)
values (1574, 16, 755, 'Red', 'Yes', "Date", "<", "Today", 1);

insert into pqfData (conID,questionID,answer,comment,dateVerified,wasChanged)
	(select con_id AS conID,
	pqfQuestionID AS questionID,
	answer,	
	textAnswer AS comment,
	tempdateverified AS dateVerified,
	tempwaschanged AS waschanged
	from auditData join tempauditquestionmigratemap on (auditQuestionID=id)
	group by con_id,pqfQuestionID
	);


insert into pqfcatdata (catID, auditID, 
	requiredCompleted, numRequired, numAnswered, 
	applies, conID, percentCompleted, percentVerified, percentClosed)
select distinct c.catID, ca.auditID, numRequired, numRequired, numRequired, 'Yes', ca.conID, percentComplete, percentVerified, percentVerified
from contractor_audit ca
join pqfcategories c on c.auditTypeID = ca.auditTypeID
where c.auditTypeID = 3
and ca.conID not in (70,249,808,1654,2617);

/* Added a update to all the answers that were changed and the verified date is not null */
update pqfdata set answer = 'Yes'
	where wasChanged = 'Yes' and dateVerified is not null 
	and answer = 'No' and questionid in
		(select questionId from pqfquestions where subcategoryid in 
			(select subcatid from pqfsubcategories where categoryid in
				(select catId from pqfcategories where audittypeid=3)));


/* ==== Change foreigh keys - From conid to auditID ==== */

/* update pqfData to use auditID instead of conId */
/** This takes a Long time, probably the bulk of the conversion time right here **/
update pqfCategories pc JOIN 
	pqfSubCategories ON (catID=categoryID) JOIN
	pqfQuestions ON (subCatID=subCategoryID) JOIN
	pqfData p USING (questionID) join
	contractor_audit ca on(ca.conID=p.conID) join
	audit_type a on (ca.auditTypeid=a.auditTypeid AND a.legacyCode=pc.auditType)
	set p.auditID = ca.auditID;

/* update pqfCatData to use auditID instead of conId */
update pqfCategories pc JOIN 
	pqfCatData pd using (catId) join
	contractor_audit ca on(ca.conID=pd.conID) join
	audit_type a on (ca.auditTypeid=a.auditTypeid AND a.legacyCode=pc.auditType)
	set pd.auditID = ca.auditID;

/* remove orphaned pqfcatdata */
delete from pqfcatdata where auditID = 0;

/* remove the office audit form */
delete from operatorForms where formID = 71;


/* ==== All Statuses ==== */

update contractor_audit set auditStatus = 'Submitted'
	where completedDate <> '0000-00-00';

update contractor_audit set auditStatus = 'Active'
	where closedDate <> '0000-00-00';

update contractor_audit set auditStatus = 'Expired'
	where expiresDate <> '0000-00-00' and expiresDate < curDate();

update contractor_audit set auditorID = null
	where auditorID = 0;

update contractor_audit set auditorID = 917 where auditorID = 2823;
update contractor_audit set auditorID = 940 where auditorID = 3224;
update contractor_audit set auditorID = 937 where auditorID = 3225;
update contractor_audit set auditorID = 938 where auditorID = 3226;
update contractor_audit set auditorID = 941 where auditorID = 3487;
update contractor_audit set auditorID = 943 where auditorID = 3486;

update contractor_audit set auditorID = null 
	where auditorID not in (select id from users);

update pqfdata set auditorID = null
	where auditorID = 0;

update contractor_info set welcomeAuditor_id = null
	where welcomeAuditor_id = 0;

update generalcontractors, forcedflaglist
	set generalcontractors.forceFlag = forcedflaglist.flagStatus,
		generalcontractors.forceBegin = forcedflaglist.dateAdded,
		generalcontractors.forceEnd = forcedflaglist.dateExpires
	where generalcontractors.genID = forcedflaglist.opID
		and generalcontractors.subID = forcedflaglist.conID;

/* ============================================ */
/* POST                                         */
/* ============================================ */
                                             
/* Alter table in Second database */
alter table `contractor_info` 
	drop column `auditDate`, 
	drop column `lastAuditDate`, 
	drop column `auditHour`, 
	drop column `auditAmPm`, 
	drop column `prequal_file`, 
	drop column `pqfSubmittedDate`, 
	drop column `desktopSubmittedDate`, 
	drop column `emailConfirmedDate`, 
	drop column `canEditPrequal`, 
	drop column `canEditDesktop`, 
	drop column `lastAuditEmailDate`, 
	drop column `auditor_id`, 
	drop column `desktopAuditor_id`, 
	drop column `daAuditor_id`, 
	drop column `pqfAuditor_id`, 
	drop column `assignedDate`, 
	drop column `isPrequalOK`, 
	drop column `auditStatus`, 
	drop column `auditCompletedDate`, 
	drop column `auditClosedDate`, 
	drop column `auditValidUntilDate`, 
	drop column `desktopAssignedDate`, 
	drop column `desktopCompletedDate`, 
	drop column `desktopClosedDate`, 
	drop column `desktopValidUntilDate`, 
	drop column `daAssignedDate`, 
	drop column `daSubmittedDate`, 
	drop column `daClosedDate`, 
	drop column `officeSubmittedDate`, 
	drop column `officeClosedDate`, 
	drop column `auditLocation`, 
	drop column `desktopPercent`, 
	drop column `desktopVerifiedPercent`, 
	drop column `officePercent`, 
	drop column `officeVerifiedPercent`, 
	drop column `daPercent`, 
	drop column `daVerifiedPercent`, 
	drop column `pqfPercent`, 
	drop column `hasNCMSDesktop`, 
	drop column `isNewOfficeAudit`, 
	drop column `daRequired`,
	drop column `tempAuditDateTime`;

alter table `operators` 
	drop column `canSeePQF`, 
	drop column `canSeeDesktop`, 
	drop column `canSeeDA`, 
	drop column `canSeeOffice`, 
	drop column `canSeeField`, COMMENT='';


alter table `pqfcategories` 
	drop column `auditType`, COMMENT='';

drop table `auditcategories`; 

drop table `auditdata`; 

drop table `auditquestions`; 

drop table `forcedflaglist`;

alter table `pqfcatdata` 
	change `auditID` `auditID` mediumint(9) unsigned   NOT NULL after `catID`, 
	drop column `conID`;

delete from pqfcatdata
	where auditID = 0;

alter table `pqfcatdata` 
	add UNIQUE KEY `auditID`(`auditID`,`catID`);

alter table `pqfopmatrix` 
	add column `id` int(10) unsigned   NOT NULL auto_increment first, 
	change `catID` `catID` mediumint(8) unsigned   NOT NULL after `id`, 
	drop key `catID`,
	drop key `PRIMARY`, add PRIMARY KEY(`id`);

alter table `pqfopmatrix` 
	add UNIQUE KEY `catID`(`catID`,`opID`,`riskLevel`);

analyze table desktopMatrix;
analyze table contractor_audit;
analyze table pqfQuestions;
analyze table pqfData;
analyze table pqfcatdata;

/* fix orphaned office audit question id reference in IIPP category */
update pqfQuestions set dependsonqid = null where questionid=1284;

/* Remove the blank and 0 answer and verifiedAnswer fields from the database */
delete from pqfdata where answer = '0000-00-00';
delete from pqfdata where answer = '' and verifiedAnswer = '';


/* delete the form that was a link to a blank office audit */
delete from forms
where formID=11;

delete from app_properties;
insert  into `app_properties`(`property`,`value`) values ('DEFAULT_SIGNATURE','PICS \r\nP.O. Box 51387\r\nIrvine CA 92619-1387\r\ntel: (949)387-1940\r\nfax: (949)269-9177\r\nhttp://www.picsauditing.com\r\nemail: info@picsauditing.com (Please add this email address to your address book to prevent it from being labeled as spam)\r\n'),('email_welcome_body','Welcome ${user.name},\r\n\r\nPlease click on this link to confirm your receipt of this email:\r\nhttp://www.picsauditing.com/login.jsp?uname=${user.username}\r\n\r\nBecause we send important account info to this email, your account will not be activated until you have confirmed receipt of this email.  If the link does not work, please cut and paste the url into your web browser.  After that, you will be able to log into your account at www.picsauditing.com.\r\n\r\nYour username is ${user.username} and your password is ${user.password}\r\n\r\nUpon logging in you will want to review your company information and verify that it is accurate as well as pick the facilities your company performs work for. If you have any difficulties, please contact us and we will assist you. Keep in mind that your clients will be viewing this information, so make it as detailed and professional as possible. Everything on the website is password protected; therefore your information is only viewable by your clients and you.\r\n\r\nThe first page you will be taken to will list several sections of information that you will need to complete (PQF). You can save them as many times as needed before submitting them to us. You will be contacted after submitting your prequalification form (PQF) in reference to your audit. As requested by the owner/operators you will have approximately 2 weeks to complete the PQF (prequalification forms) in order to stay on the approved vendor/contractor list.\r\n\r\nRemember to click on save so you do not lose any of your information. (30 minute timeout)\r\n\r\nPlease contact your insurance company and upload your company\'s verification of your last 3 years EMR (experience modification rate) to us (as a single pdf). This will need to be on the insurance company\'s letterhead or may be a loss run report. This may take some time so you should start this immediately. If you are having problems with this you can fax it to 949-269-9146 or it can be emailed to info@picsauditing.com.\r\n\r\nThank you again for your business and feel free to contact us with any questions.\r\n\r\nRegards,\r\n\r\nDEFAULT_SIGNATURE'),('email_welcome_subject','Account Activation'),('email_password_subject','PICS Login Info'),('email_password_body','Attn: ${user.name}\r\n\r\nThis is an automatically generated email to remind you of your username and password to log in to the PICS website.\r\n\r\nYour username is: ${user.username}\r\nand your password is: ${user.password}\r\n\r\nIf you have any questions or did not request that this email be sent to you, please let us know.\r\n\r\nDEFAULT_SIGNATURE'),('email_annual_update_subject','PICS annual update - last week before you are deactivated'),('email_annual_update_body','Hello ${user.name},\r\n\r\nIt is time for you to update your company\'s information that PICS provides to your clients. Each January, the facilities you currently work at require your company to update your prequalification information online and resubmit it. If you do not update this information you will become inactive beginning March 1, which will remove you from your clients\' approved contractor list until the forms are completed. As a reminder, audits are performed on a 3 year rotation as well.\r\n\r\nPlease Log into the website at http://www.picsauditing.com. If you are unable to log in to your account, you may have forgotten your username and password. If this is the case, you can go to http://www.picsauditing.com/forgot_password.jsp. If this does not work you can call or email us.\r\n\r\nPICS has made some changes to the website over the past year so please make sure that you check all of your company information, which will provide your clients with the most current details about your business.\r\n\r\nThere are several items that must be updated annually. Upon logging in to your account, please follow these steps:\r\n\r\n1. First click on the [Facilities] link from your details page, which will direct you to a page that will have you indicate and verify which operators/facilities your company works for. If you do not choose the facilities they will not be able to view your information, which could prevent you from working there.\r\n\r\n2. Next, you will need to click on the [Complete PQF] link from your details page. This will direct you to the prequalification form (PQF). There are some new additions for 2008. You will need to complete all of the new questions and update anything that has changed in order to submit it. As a reminder each section must indicate 100 percent before you can resubmit the PQF. MAKE SURE YOU SUBMIT THE PQF WHEN COMPLETE. The most time-consuming item is to have your OSHA 300 log from 2007 filled out and your latest EMR.\r\n\r\n3. After submitting a completed PQF please review your details page where you can check on your company\'s status and review/edit your company details. Please update your details page to indicate a billing contact person for your company.\r\n\r\nPlease make sure that you update all of this information and have it submitted Feb 29 so that your company is not removed from any of the facilities\' approved contractor list.\r\n\r\nIf you have any questions or concerns, feel free to contact us.\r\n\r\nThanks, and have a safe year!\r\n\r\nDEFAULT_SIGNATURE\r\n'),('email_newuser_subject','New PICS User Account Created'),('email_newuser_body','Hello ${user.name},\r\n\r\n${opAcct.contact} has issued you a login for the ${opAcct.name} account on PICS.\r\n\r\nYour username: ${user.username}\r\nYour password: ${user.password}\r\n\r\nAttached is a User\'s Manual in case you have any questions.\r\n\r\nHave a great week,\r\n\r\nDEFAULT_SIGNATURE'),('email_desktopsubmit_subject','Your PICS Desktop Audit has been completed'),('email_desktopsubmit_body','Hello ${contractor.contact},\r\n\r\nPICS has completed a desktop audit of ${contractor.name}\'s safety manual.\r\nPlease log in to our website, and click on the \'View Desktop Audit\' link to see any categories that may have outstanding requirements to fulfill. Please feel free to contact us and review the desktop audit as there may be many items we can close out just by speaking over the phone. \r\n\r\nOur auditors have made every effort to perform the audit based on their knowledge of what your company does, but sometimes there are requirements listed that do not apply to the work your company performs. These items can often be rectified immediately so we encourage you to contact us to discuss any items you feel may not apply. \r\n\r\nIn order to close out any requirements, please fax, email, or mail the documentation addressing the section in question.  Our overnight address is: 17701 Cowan Suite 140, Irvine, CA, 92614. You can log in to your account during the process to view any requirements that are still outstanding or to check on your status.\r\n\r\nAgain, if you have any questions or concerns, feel free to contact us.\r\n\r\nThanks, and have a safe year!\r\n\r\nDEFAULT_SIGNATURE'),('email_dasubmit_subject','Your PICS Drug and Alcohol Audit has been completed'),('email_dasubmit_body','Hello ${contractor.contact},\r\n\r\nPICS has completed a D&A audit for ${contractor.name}.  Please log in to our website, and click on the \'View D&A Audit\' link to see any categories that may have outstanding requirements to fulfill. Please feel free to contact us and review the D&A audit as there may be many items we can close out just by speaking over the phone. \r\n\r\nOur auditors have made every effort to perform the audit based on their knowledge of what your company does, but sometimes there are requirements listed that do not apply to the work your company performs. These items can often be rectified immediately so we encourage you to contact us to discuss any items you feel may not apply. \r\n\r\nIn order to close out any requirements, please fax, email, or mail the documentation addressing the section in question.  Our overnight address is: 17701 Cowan Suite 140, Irvine, CA, 92614. You can log in to your account during the process to view any requirements that are still outstanding or to check on your status.\r\n\r\nAgain, if you have any questions or concerns, feel free to contact us.\r\n\r\nThanks, and have a safe year!\r\n\r\nDEFAULT_SIGNATURE'),('email_contractoradded_subject','${opName} has added you to their PICS database'),('email_contractoradded_body','Hello ${user.name},\r\n\r\nThis is an automatically generated email to inform you that ${opAcct.contact} from ${opName} has added your company, ${opAcct.name}, to their PICS database, and will have access to your PQF and audit information.  If you do not want this facility/person to have access to your PICS information, please contact us immediately.\r\n\r\nThanks, and have a safe year!\r\n\r\nDEFAULT_SIGNATURE'),('email_certificate_expire_body','Attn: ${contractor.contact},\r\n\r\nThis is an automatically generated email from ${opName} to remind you that your company has an insurance certificate that has expired or is about to expire.\r\n\r\nThe ${certificate_type} Certificate of Insurance for ${opName} expires on ${expiration_date}. Please mail, email or fax us a new insurance certificate listing ${opName} as the additional insured.\r\n\r\nIf we do not receive this certificate prior to the expiration date you will not be permitted to work for us.\r\n\r\nAs always we appreciate your services and are here to answer any questions you may have.\r\n\r\n${opName} c/o PICS\r\nP.O. Box 51387\r\nIrvine CA 92619-1387\r\ntel: (949)387-1940\r\nfax: (949)269-9149\r\neorozco@picsauditing.com\r\nhttp://www.picsauditing.com\r\n'),('email_certificate_expire_subject','${opName} insurance certificate about to expire'),('email_verifyPqf_body','${user.name},\r\n\r\nUpon review of your PQF I have noticed that there are a few items that need your attention. Please either email or fax the following items to me:\r\n\r\n${missing_items}\r\n\r\nPlease find the OSHA Forms in the below links.\r\n\r\nOSHA Forms in Excel Format\r\nhttp://www.picsauditing.com/forms/form284.xls\r\nOSHA Recordkeeping Assistance\r\nhttp://www.picsauditing.com/forms/form41.pdf\r\n\r\n\r\n\r\n${permissions.name},\r\nPICS\r\nP.O. Box 51387\r\nIrvine CA 92619-1387\r\ntel: (949)387-1940\r\nfax: (949)417-9360\r\n\r\nhttp://www.picsauditing.com\r\n'),('email_verifyPqf_subject','PQF Verification for ${user.name}'),('email_contractorconfirm_body','Attn: ${contractor.name}\r\n\r\nThis is an automatically generated email from Pacific Industrial Contractor Screening (PICS) to remind you that your company has a safety audit scheduled for ${conAudit.scheduledDate} at 9 am (PT).\r\n\r\nPrior to the audit date please review the \'How to Prepare for the Onsite Audit\' and \'Onsite Audit Form\' in the forms and documents section of our website under PICS.  You will need to log in with your username and password. \r\n\r\nIt is important that you review these materials as we will be reviewing documentation to include inspection forms, training records, safety meeting sheets, etc.\r\n\r\nIf you have any questions, please contact us so that we can help you prepare for the audit.  The more prepared you are, the quicker the process will move.\r\n\r\nAdditionally, please note that there is a $150 cancellation fee if you do not give us at least a 48-hour notice.\r\n\r\nTo finalize the scheduling of your audit, click on the link below to confirm receipt of this notice.\r\n\r\nhttp://www.picsauditing.com/audit_confirmEmailReceipt.jsp?id=${contractor.id}&isaud=false\r\n\r\nPICS - Pacific Industrial Contractor Screening P.O. Box 51387 Irvine CA 92619-1387 (949)387-1940\r\nfax: (949)269-9146\r\ninfo@picsauditing.com (Please add this email address to your address book to prevent it from being labeled as spam) http://www.picsauditing.com\r\n'),('email_contractorconfirm_subject','Office Audit Confirmation'),('email_auditorconfirm_subject','Office Audit Confirmation for a contractor'),('email_auditorconfirm_body','Attn: ${opAcct.name}\r\n\r\nThis is an automatically generated email from Pacific Industrial Contractor Screening  (PICS) to remind you of an audit you have scheduled to perform with Pacific Industrial Contractor Screening (PICS) on#if (${conAudit.auditType.auditName} == \"Desktop\")${conAudit.scheduledDate}!#end at 9AM (PT). \r\n\r\nThe address is:\r\n\r\n17701 Cowan Ste. 140\r\nIrvine, CA 92606\r\n\r\nPlease make sure you log into your account to review the safety qualification forms and details about the company in order to assist you in preparing for the audit. There is also a map on the contractors details page if you need assistance in driving to the location. \r\n\r\nAdditionally, you should be receiving a confirmation email from the companies you are assigned to audit. If you do not receive this it is a good idea to call them to confirm yourself. The contact person at Pacific Industrial Contractor Screening (PICS) is Jesse Cota and can be reached at (800) 506-PICS. \r\n\r\nNobody else at PICS will be calling the companies assigned to you. Therefore, it is your responsibility to remember to confirm all appointments.  Every effort has been made to make sure that the contractors are prepared for the audit so that your time is manageable.\r\n\r\nThank you and please click on this link to confirm receipt of this notice:\r\n\r\nhttp://www.picsauditing.com/audit_confirmEmailReceipt.jsp?id=${opAcct.id}&isaud=true\r\n\r\nIf you have any questions or concerns about the audit, contact the audit manager, Jesse Cota, @ 949-387-1940, ext. 704 or email him at jcota@picsauditing.com.\r\n\r\nWe appreciate your time and effort in making the process a pleasant experience for our customers!\r\n\r\nThanks,\r\n\r\n${permissions.name}\r\n\r\nPICS - Pacific Industrial Contractor Screening P.O. Box 51387 Irvine CA 92619-1387 (949)387-1940\r\nfax: (949)269-9146\r\ninfo@picsauditing.com (Please add this email address to your address book to prevent it from being labeled as spam) http://www.picsauditing.com'),('email_audits_thankyou_body','Hello ${contractor.contact},\r\n\r\nThank you for your time and effort in closing out your company\'s Audit(s) requirements. Often these audits can be an interruption in your daily operations and we appreciate your time in focusing on the safety of your company and employees. \r\n\r\nYou will be notified each year to update your online Qualification Form (PQF).  Your audits are now complete and will be good for three years.  Until then, we hope that your company is prosperous and your safety culture continues to improve. If you have any questions or are in need of any further assistance, feel free to contact us at anytime.\r\n\r\nThank you,\r\n\r\nDEFAULT_SIGNATURE'),('email_audits_thankyou_subject','Thank you from PICS');
