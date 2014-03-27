CREATE TABLE address_verification (
  id INT(11) NOT NULL AUTO_INCREMENT,
  createdBy INT(11) NULL DEFAULT null,
  creationDate DATETIME NULL DEFAULT null,
  updatedBy INT(11) NULL DEFAULT null,
  updateDate DATETIME NULL DEFAULT null,
  verificationDate DATETIME NULL,
  status VARCHAR(50) NULL,
  entityType VARCHAR(50) NULL,
  PRIMARY KEY (id));

ALTER TABLE accounts
ADD COLUMN addressVerificationId INT(11) NULL DEFAULT '0' AFTER passwordSecurityLevelId;
