/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;

select os.applicable, os.auditid, pcd.applies from osha_audit os 
join pqfcatdata pcd on pcd.auditid = os.auditid
where os.applicable = 1
and pcd.applies = 'No'
and pcd.catID = 151; 

select os.applicable, os.auditid, pcd.applies from osha_audit os 
join pqfcatdata pcd on pcd.auditid = os.auditid
join contractor_audit ca on ca.id = os.auditid 
where os.applicable = 0
and pcd.applies = 'Yes'
and pcd.catID = 151;
**/

/* Alter table in target */
ALTER TABLE `contractor_registration_request` 
	CHANGE `createdBy` `createdBy` mediumint(8) unsigned   NULL after `name`, 
	CHANGE `updatedBy` `updatedBy` mediumint(8) unsigned   NULL after `createdBy`, 
	CHANGE `creationDate` `creationDate` datetime   NULL after `updatedBy`, 
	CHANGE `updateDate` `updateDate` datetime   NULL after `creationDate`, 
	CHANGE `requestedByID` `requestedByID` mediumint(9)   NOT NULL after `updateDate`, 
	ADD COLUMN `requestedByUserID` mediumint(11)   NULL after `requestedByID`, 
	ADD COLUMN `requestedByUser` varchar(20)  COLLATE latin1_swedish_ci NULL after `requestedByUserID`, 
	ADD COLUMN `handledBy` varchar(10)  COLLATE latin1_swedish_ci NOT NULL after `requestedByUser`, 
	ADD COLUMN `open` tinyint(4)   NULL DEFAULT '1' after `handledBy`, 
	CHANGE `contact` `contact` varchar(30)  COLLATE latin1_swedish_ci NOT NULL after `open`, 
	CHANGE `phone` `phone` varchar(20)  COLLATE latin1_swedish_ci NULL after `contact`, 
	CHANGE `email` `email` varchar(50)  COLLATE latin1_swedish_ci NULL after `phone`, 
	ADD COLUMN `taxID` varchar(9)  COLLATE latin1_swedish_ci NULL after `email`, 
	ADD COLUMN `address` varchar(100)  COLLATE latin1_swedish_ci NULL after `taxID`, 
	ADD COLUMN `city` varchar(50)  COLLATE latin1_swedish_ci NULL after `address`, 
	ADD COLUMN `state` char(2)  COLLATE latin1_swedish_ci NOT NULL after `city`, 
	ADD COLUMN `zip` varchar(10)  COLLATE latin1_swedish_ci NULL after `state`, 
	ADD COLUMN `country` char(2)  COLLATE latin1_swedish_ci NULL after `zip`, 
	ADD COLUMN `deadline` date   NULL after `country`, 
	ADD COLUMN `lastContactedBy` mediumint(9)   NULL after `deadline`, 
	ADD COLUMN `lastContactDate` datetime   NULL after `lastContactedBy`, 
	ADD COLUMN `contactCount` tinyint(4)   NULL DEFAULT '0' after `lastContactDate`, 
	ADD COLUMN `matchCount` tinyint(4)   NULL DEFAULT '0' after `contactCount`, 
	ADD COLUMN `notes` varchar(1000)  COLLATE latin1_swedish_ci NULL after `matchCount`, 
	ADD COLUMN `conID` mediumint(9)   NULL after `notes`, 
	DROP COLUMN `comment`, 
	DROP COLUMN `followup`, 
	DROP COLUMN `status`, 
	ADD KEY `status`(`open`,`country`,`state`), COMMENT='';

/* Alter table in target */
ALTER TABLE `ref_country` 
	ADD COLUMN `csrID` mediumint(9)   NULL after `french`, COMMENT='';

/* Alter table in target */
ALTER TABLE `users` 
	CHANGE `resetHash` `resetHash` varchar(100)  COLLATE latin1_swedish_ci NULL after `lockUntil`, 
	CHANGE `phone` `phone` varchar(50)  COLLATE latin1_swedish_ci NULL after `resetHash`, 
	CHANGE `fax` `fax` varchar(15)  COLLATE latin1_swedish_ci NULL after `phone`, 
	CHANGE `phoneIndex` `phoneIndex` varchar(11)  COLLATE latin1_swedish_ci NULL after `fax`, 
	CHANGE `passwordChanged` `passwordChanged` date   NULL after `phoneIndex`, 
	CHANGE `createdBy` `createdBy` int(11)   NULL after `passwordChanged`, 
	CHANGE `updatedBy` `updatedBy` int(11)   NULL after `createdBy`, 
	CHANGE `creationDate` `creationDate` datetime   NULL after `updatedBy`, 
	CHANGE `updateDate` `updateDate` datetime   NULL after `creationDate`, 
	CHANGE `emailConfirmedDate` `emailConfirmedDate` date   NULL after `updateDate`, 
	CHANGE `timezone` `timezone` varchar(50)  COLLATE latin1_swedish_ci NULL after `emailConfirmedDate`, 
	ADD COLUMN `forcePasswordReset` tinyint(4)   NOT NULL DEFAULT '0' after `timezone`, 
	DROP COLUMN `newPassword`;

-- update passwords to their hashed versions (sha-1)
UPDATE users SET password = sha1(concat(password, id)) WHERE username not like 'GROUP%';

-- update the CSR for State and country
update ref_state set csrID = 940
where csrID is null;

update ref_country set csrID = 940
where isocode not in ('US','CA');

-- update country on PICS account
update accounts set country = 'US'
where id = 1100;

-- remove old password reminder email template
delete from email_template where id = 24;

update email_template set body = 'Attn: <DisplayName>
This is an automatically generated email that will allow you to set or reset your password. Please click the following link and set your password on the following page.
${confirmLink}
If you would like to generate a new password reset link, please click below:
${resetLink}
If you did not request that this email be sent to you or if you have any questions, please contact us.
<PICSSignature>' where id = 85;

update email_template set body = 'Attn: <DisplayName>
This is an automatically generated email to remind you of your username(s) to log in to the PICS website.
The usernames and the accounts associated with this email address are:
#foreach( $user in $users )
${user.getUsername()} on ${user.getAccount().getName()}
#end
If you have any questions or did not request that this email be sent to you, please let us know.
<PICSSignature>' where id = 86;

update email_template set body = 'Hello <DisplayName>,
<MyName> has issued you a login for the ${accountname} account on PICS.
Please log in using the following link to set your password.
${confirmLink}
Have a great week,
<PICSSignature>' where id = 5;