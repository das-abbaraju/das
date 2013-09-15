RENAME TABLE `generalcontractors` TO `contractor_operator`;

ALTER TABLE `contractor_operator`
  DROP INDEX `subID`,
  DROP INDEX `genID`,
  DROP FOREIGN KEY `FK_generalcontractors_con`,
  DROP FOREIGN KEY `FK_generalcontractors_op`;

ALTER TABLE `contractor_operator`
  CHANGE `genID` `opID` INT(11) NULL,
  CHANGE `subID` `conID` INT(11) NULL, 
  ADD  UNIQUE INDEX `conID` (`conID`, `opID`),
  ADD  INDEX `opID` (`opID`),
  ADD CONSTRAINT `FK_contractor_operator_op` FOREIGN KEY (`opID`) REFERENCES `accounts`(`id`),
  ADD CONSTRAINT `FK_contractor_operator_con` FOREIGN KEY (`conID`) REFERENCES `accounts`(`id`);

DROP VIEW IF EXISTS generalcontractors;
CREATE VIEW `generalcontractors` AS
(SELECT id,opID AS genID,conID AS subID,gcID,TYPE,createdBy,creationDate,updatedBy,updateDate,workStatus,flag,lastStepToGreenDate,waitingOn,forceFlag,forceBegin,forceEnd,forcedBy,relationshipType,processCompletion,flagLastUpdated,forceReason,contractorType,baselineFlag,baselineApprover,baselineApproved,flagDetail,baselineFlagDetail,requestedByUserID,requestedByUser,deadline,reasonForRegistration
FROM contractor_operator);

DROP TRIGGER IF EXISTS `generalcontractors_two_ids_before_insert` ;

DELIMITER //
CREATE DEFINER = `pics_admin`@`%` TRIGGER `generalcontractors_two_ids_before_insert` BEFORE INSERT ON `contractor_operator`
FOR EACH
ROW BEGIN
SET @cnt =0;

IF new.gcID IS NULL THEN SET @cnt = @cnt +1;

END IF ;

IF new.conID IS NULL THEN SET @cnt = @cnt +1;

END IF ;

IF new.opID IS NULL THEN SET @cnt = @cnt +1;

END IF ;

IF @cnt >1 THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A contractor_operator object must have at least 2 ids between conID, opID and gcID not equal to NULL';

END IF ;

END; //
DELIMITER ;

DROP TRIGGER IF EXISTS `generalcontractors_two_ids_before_update` ;

DELIMITER //
CREATE DEFINER = `pics_admin`@`%` TRIGGER `generalcontractors_two_ids_before_update` BEFORE UPDATE ON `contractor_operator` FOR EACH ROW BEGIN SET @cnt =0;

IF new.gcID IS NULL THEN SET @cnt = @cnt +1;

END IF ;

IF new.conID IS NULL THEN SET @cnt = @cnt +1;

END IF ;

IF new.opID IS NULL THEN SET @cnt = @cnt +1;

END IF ;

IF @cnt >1 THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A contractor_operator object must have at least 2 ids between conID, opID and gcID not equal to NULL';

END IF ;

END;//
DELIMITER ;
