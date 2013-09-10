ALTER TABLE `generalcontractors`   
  DROP INDEX `subID`,
  DROP INDEX `genID`,
  DROP FOREIGN KEY `FK_generalcontractors_con`,
  DROP FOREIGN KEY `FK_generalcontractors_op`;

ALTER TABLE `generalcontractors`   
  CHANGE `genID` `opID` INT(11) NULL,
  CHANGE `subID` `conID` INT(11) NULL, 
  ADD  UNIQUE INDEX `conID` (`conID`, `opID`),
  ADD  INDEX `opID` (`opID`),
  ADD CONSTRAINT `FK_contractor_operator_op` FOREIGN KEY (`opID`) REFERENCES `accounts`(`id`),
  ADD CONSTRAINT `FK_contractor_operator_con` FOREIGN KEY (`conID`) REFERENCES `accounts`(`id`);

RENAME TABLE `generalcontractors` TO `contractor_operator`;

DROP VIEW IF EXISTS generalcontractors;
CREATE VIEW `generalcontractors` AS
(SELECT id,opID AS genID,conID AS subID,gcID,TYPE,createdBy,creationDate,updatedBy,updateDate,workStatus,flag,lastStepToGreenDate,waitingOn,forceFlag,forceBegin,forceEnd,forcedBy,relationshipType,processCompletion,flagLastUpdated,forceReason,contractorType,baselineFlag,baselineApprover,baselineApproved,flagDetail,baselineFlagDetail,requestedByUserID,requestedByUser,deadline,reasonForRegistration
FROM contractor_operator);
