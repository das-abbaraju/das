CREATE TABLE `assessment_result_stage`(
	`id` int(11) unsigned NOT NULL  auto_increment , 
	`centerID` int(11) unsigned NOT NULL  , 
	`resultID` varchar(20) COLLATE latin1_swedish_ci NULL  , 
	`qualificationType` varchar(50) COLLATE latin1_swedish_ci NULL  , 
	`qualificationMethod` varchar(50) COLLATE latin1_swedish_ci NULL  , 
	`description` varchar(255) COLLATE latin1_swedish_ci NULL  , 
	`testID` int(11) NULL  , 
	`firstName` varchar(50) COLLATE latin1_swedish_ci NULL  , 
	`lastName` varchar(50) COLLATE latin1_swedish_ci NULL  , 
	`employeeID` varchar(50) COLLATE latin1_swedish_ci NULL  , 
	`companyName` varchar(50) COLLATE latin1_swedish_ci NULL  , 
	`companyID` int(11) NULL  , 
	`qualificationDate` date NULL  , 
	`creationDate` datetime NULL  , 
	`updateDate` datetime NULL  , 
	`createdBy` int(11) NULL  , 
	`updatedBy` int(11) NULL  , 
	PRIMARY KEY (`id`) , 
	KEY `centerID`(`centerID`) 
) ENGINE=InnoDB DEFAULT CHARSET='latin1';

ALTER TABLE `contractor_registration_request` 
	CHANGE `contactCount` `contactCount` tinyint(4) unsigned   NOT NULL DEFAULT '0' after `lastContactDate`, 
	CHANGE `matchCount` `matchCount` tinyint(4) unsigned   NOT NULL DEFAULT '0' after `contactCount`, 
	ADD COLUMN `watch` tinyint(4) unsigned   NOT NULL DEFAULT '0' after `conID`, COMMENT='';

CREATE TABLE `stats_gco_count`(
	`opID` mediumint(9) NOT NULL  , 
	`opID2` mediumint(9) NULL  , 
	`total` int(11) NOT NULL  DEFAULT '0' , 
	UNIQUE KEY `operators`(`opID`,`opID2`) 
) ENGINE=MyISAM DEFAULT CHARSET='utf8';


/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;
**/

/** Update the requiresOQ for all contractors
 * we don't want to run this yet 
update accounts set requiresOQ = 1
where id in (select distinct conid from contractor_audit ca
join pqfdata pd on ca.id = pd.auditid
where pd.questionid = 894
and pd.answer = 'Yes');
**/

/* remove the question with id 0*/
delete from pqfquestions where id = 0;

update flag_criteria set questionid = null where questionid = 0;

update `email_template` 
set `id`='83',`accountID`='1100',`templateName`='Operator Request for Registration',
`subject`='${newContractor.requestedBy.name} has requested you join PICS',
`body`='${newContractor.name}\r\n\r\nHello, ${newContractor.contact}\r\n\r\n${requestedBy} of ${newContractor.requestedBy.name} has requested that you create an account with PICS to complete the prequalification process. Please click on the link below to register an account with PICS.\r\n${requestLink}\r\n\r\nThank you,\r\n<PICSSignature>',
`createdBy`='951',`creationDate`='2010-01-14 16:36:12',`updatedBy`='951',`updateDate`='2010-01-14 16:36:12',
`listType`='Contractor',`allowsVelocity`='1',`html`='0',`recipient`=NULL where `id`='83';

insert into `useraccess` 
(accessID, userID, accessType, viewFlag, editFlag, deleteFlag, grantFlag, lastUpdate, grantedByID)
values (NULL, 959, 'RequestNewContractor', 1, 1, 0, 1, now(), 1);

insert into `widget_user`
(widgetID, userID, expanded, widget_user.column, sortOrder) 
values(26, 646, 1, 2, 15);

insert into `widget_user`
(widgetID, userID, expanded, widget_user.column, sortOrder) 
values(26, 616, 1, 2, 15);

update `widget` set `caption`='Registration Requests' where `widgetID`='26';

/* Updated widget caption */
update `widget` set `caption`='Recently Added Contractors' where `widgetID`='2';

insert into `useraccess`
(accessID, userID, accessType, viewFlag, editFlag, deleteFlag, grantFlag, lastUpdate, grantedByID)
values (null, 959, 'ManageAssessment', 1, 1, null, null, NOW(), 941);


/* app properties for filter logging */
INSERT INTO app_properties VALUES ('filterlog.enabled', '1');

INSERT INTO app_properties VALUES ('filterlog.ignore', 'ajax,destinationAction,allowMailMerge');

INSERT INTO app_properties VALUES ('filterlog.ignorevalues', 'ccOnFile:2,minorityQuestion:0,pendingPqfAnnualUpdate:false,primaryInformation:false,tradeInformation:false');

/* Change in deactivation email to include address  */
INSERT INTO token(tokenName, listType, velocityCode) VALUES('PrimaryAddress', 'Contractor', '${contractor.address}
${contractor.city}, ${contractor.state}
${contractor.zip}
${contractor.country}');

UPDATE email_template SET body = 'As you know PICS endeavors to maintain active accounts for all contractors who work at your facility.\r\n
This notification is to inform you that <CompanyName> has either not responded to requests to maintain an active status in the PICS database or has requested to have their account closed.  Deactivation of contractor accounts affects your facility\'s ability to view the prequalification information and more importantly the approval status to perform work.\r\n
If this contractor is working at your facility, please advise PICS on how to proceed.  If you are no longer doing business with them, we can remove them from your active list of contractors.\r\n
If you wish to make contact with them to discuss the situation, their contact information is as follows:\r\n
ContactName - <ContactName>\rPhone - <PrimaryPhone>\rEmail - <PrimaryEmail>\rAddress\r<PrimaryAddress>\r\n\nThank you,\rPICS Customer Service\rToll Free 800.506.(PICS)7427\rLocal 949.387.1940\rwww.picsauditing.com\r' 
WHERE id = 51;
