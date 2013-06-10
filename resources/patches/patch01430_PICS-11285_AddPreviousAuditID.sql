ALTER TABLE `contractor_audit`
  CHANGE `auditTypeID` `auditTypeID` INT(11) NOT NULL,
  CHANGE `conID` `conID` INT(11) NOT NULL,
  CHANGE `employeeID` `employeeID` INT(11) NULL,
  CHANGE `auditorID` `auditorID` INT(11) NULL,
  CHANGE `requestedByOpID` `requestedByOpID` INT(11) NULL,
  CHANGE `closingAuditorID` `closingAuditorID` INT(11) NULL,
  ADD COLUMN `previousAuditID` INT(11) NULL AFTER `closingAuditorID`;
