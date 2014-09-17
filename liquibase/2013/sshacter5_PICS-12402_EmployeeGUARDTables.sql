--liquibase formatted sql
--changeset sshacter:5

SET FOREIGN_KEY_CHECKS  = 0;

CREATE TABLE IF NOT EXISTS  accountemployeeguard(
  accountID INT(11) NOT NULL,
  PRIMARY KEY (accountID)
)
ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci CHECKSUM=0 DELAY_KEY_WRITE=0
;

CREATE TABLE IF NOT EXISTS  account_employee(
  id INT(11) NOT NULL AUTO_INCREMENT COMMENT 'Employee ID',
  accountID INT(11) NOT NULL COMMENT 'Account ID',
  profileID INT(11) NULL COMMENT 'Profile ID from profile table',
  documentID INT(11) NULL  COMMENT 'Any employee account document (employee photo etc.)',
  slug VARCHAR(64) NOT NULL COMMENT 'Unique employee code',
  firstName VARCHAR(64) NOT NULL COMMENT 'Employee first name',
  lastName VARCHAR(64) NOT NULL COMMENT 'Employee last name',
  positionType VARCHAR(32) COMMENT 'The type of position (Full Time, Part Time, Contract)',
  positionName VARCHAR(64) COMMENT 'The position job title',
  email VARCHAR(128) NOT NULL COMMENT 'The employee email address',
  phone VARCHAR(24) COMMENT 'The employee phone number',
  emailToken VARCHAR(64) COMMENT 'The invite email token value',
  createdBy INT(11) NOT NULL COMMENT 'The user ID that created the row',
  updatedBy INT(11) COMMENT 'The user ID that updated the row',
  deletedBy INT(11) COMMENT 'The user ID that deleted the row',
  createdDate DATETIME NOT NULL COMMENT 'The date the row was created',
  updatedDate DATETIME COMMENT 'The date the row was updated',
  deletedDate DATETIME COMMENT 'The date the row was deleted',
  PRIMARY KEY (id),
  UNIQUE INDEX akAccount_Employee (accountID, slug),
  UNIQUE INDEX ak2Account_Employee (email, accountID)
)
ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci CHECKSUM=0 DELAY_KEY_WRITE=0
;

CREATE TABLE IF NOT EXISTS  account_skill(
  id INT(11) NOT NULL AUTO_INCREMENT COMMENT 'Skill ID',
  accountID INT(11) NOT NULL COMMENT 'Account ID',
  skillType VARCHAR(32) NOT NULL COMMENT 'Skill type (training, certification, etc.)',
  name VARCHAR(64) NOT NULL COMMENT 'Skill name for this type and account',
  ruleType VARCHAR(32) NOT NULL DEFAULT 'Optional' COMMENT 'Skill rule type (mandatory for all employees, optional etc.)',
  intervalType VARCHAR(32) NOT NULL DEFAULT 'Training' COMMENT 'The skill expiration interval type (days, weeks, months, years etc.)',
  intervalPeriod INT(11) NOT NULL DEFAULT 0 COMMENT 'The skill expiration interval value',
  description VARCHAR(256) NULL COMMENT 'Skill description',
  createdBy INT(11) NOT NULL COMMENT 'The user ID that created the row',
  updatedBy INT(11) COMMENT 'The user ID that updated the row',
  deletedBy INT(11) COMMENT 'The user ID that deleted the row',
  createdDate DATETIME NOT NULL COMMENT 'The date the row was created',
  updatedDate DATETIME COMMENT 'The date the row was updated',
  deletedDate DATETIME COMMENT 'The date the row was deleted',
  PRIMARY KEY (id),
  UNIQUE INDEX akAccount_Skill (accountID, skillType, NAME)
)
ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci CHECKSUM=0 DELAY_KEY_WRITE=0
;

CREATE TABLE IF NOT EXISTS  account_group(
  id INT(11) NOT NULL AUTO_INCREMENT COMMENT 'Group ID',
  accountID INT(11) NOT NULL COMMENT 'Account ID',
  name VARCHAR(64) NOT NULL COMMENT 'Group name for this type and account',
  description VARCHAR(256) NULL COMMENT 'Group description',
  createdBy INT(11) NOT NULL COMMENT 'The user ID that created the row',
  updatedBy INT(11) COMMENT 'The user ID that updated the row',
  deletedBy INT(11) COMMENT 'The user ID that deleted the row',
  createdDate DATETIME NOT NULL COMMENT 'The date the row was created',
  updatedDate DATETIME COMMENT 'The date the row was updated',
  deletedDate DATETIME COMMENT 'The date the row was deleted',
  PRIMARY KEY (id),
  UNIQUE INDEX akAccount_Group (accountID, name)
)
ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci CHECKSUM=0 DELAY_KEY_WRITE=0
;

CREATE TABLE IF NOT EXISTS  account_skill_group(
  id INT(11) NOT NULL AUTO_INCREMENT COMMENT 'Row Number',
  skillID INT(11) NOT NULL COMMENT 'Unique Skill ID from account_skill',
  groupID INT(11) NOT NULL COMMENT 'Unique Group ID from account_group',
  createdBy INT(11) NOT NULL COMMENT 'The user ID that created the row',
  updatedBy INT(11) COMMENT 'The user ID that updated the row',
  deletedBy INT(11) COMMENT 'The user ID that deleted the row',
  createdDate DATETIME NOT NULL COMMENT 'The date the row was created',
  updatedDate DATETIME COMMENT 'The date the row was updated',
  deletedDate DATETIME COMMENT 'The date the row was deleted',
  PRIMARY KEY (id),
  UNIQUE INDEX akAccount_Skill_Group (skillID, groupID)
)
ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci CHECKSUM=0 DELAY_KEY_WRITE=0
;

CREATE TABLE IF NOT EXISTS  account_skill_employee(
  id INT(11) NOT NULL AUTO_INCREMENT COMMENT 'Row Number',
  skillID INT(11) NOT NULL COMMENT 'Unique Skill ID from account_skill',
  employeeID INT(11) NOT NULL COMMENT 'Unique Employee ID from account_employee',
  documentID INT(11) NULL COMMENT 'Document ID from profileDocument table',
  startDate DATETIME NOT NULL COMMENT 'The date an employee started a skill for this account',
  finishDate DATETIME NULL COMMENT 'The date the skill will expire for this employee for this account',
  createdBy INT(11) NOT NULL COMMENT 'The user ID that created the row',
  updatedBy INT(11) COMMENT 'The user ID that updated the row',
  deletedBy INT(11) COMMENT 'The user ID that deleted the row',
  createdDate DATETIME NOT NULL COMMENT 'The date the row was created',
  updatedDate DATETIME COMMENT 'The date the row was updated',
  deletedDate DATETIME COMMENT 'The date the row was deleted',
  PRIMARY KEY (id),
  UNIQUE INDEX akAccount_Skill_Employee (skillID, employeeID)
)
ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci CHECKSUM=0 DELAY_KEY_WRITE=0
;

CREATE TABLE IF NOT EXISTS  account_group_employee(
  id INT(11) NOT NULL AUTO_INCREMENT COMMENT 'Row Number',
  groupID INT(11) NOT NULL COMMENT 'Unique Skill ID from account_skill',
  employeeID INT(11) NOT NULL COMMENT 'Unique Employee ID from account_employee',
  createdBy INT(11) NOT NULL COMMENT 'The user ID that created the row',
  updatedBy INT(11) COMMENT 'The user ID that updated the row',
  deletedBy INT(11) COMMENT 'The user ID that deleted the row',
  createdDate DATETIME NOT NULL COMMENT 'The date the row was created',
  updatedDate DATETIME COMMENT 'The date the row was updated',
  deletedDate DATETIME COMMENT 'The date the row was deleted',
  PRIMARY KEY (id),
  UNIQUE INDEX akAccount_Group_Employee (groupID, employeeID)
)
ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci CHECKSUM=0 DELAY_KEY_WRITE=0
;

CREATE TABLE IF NOT EXISTS  email_hash
(
  id INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  hash VARCHAR(256) COLLATE utf8_bin DEFAULT NULL,
  account_employeeID INT(11) UNSIGNED NOT NULL,
  emailAddress VARCHAR(128) COLLATE utf8_bin NOT NULL,
  creationDate DATETIME DEFAULT NULL,
  expirationDate DATETIME DEFAULT NULL,
  PRIMARY KEY (id)
)
ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci CHECKSUM=0 DELAY_KEY_WRITE=0
;

CREATE TABLE IF NOT EXISTS  `profile` (
  id int(11) NOT NULL AUTO_INCREMENT COMMENT 'Profile ID',
  appUserID int(11) NOT NULL COMMENT 'ID from app_user table',
  slug varchar(64) NOT NULL COMMENT 'Unique person profile code or name',
  firstName varchar(64) NOT NULL COMMENT 'person profile first name',
  lastName varchar(64) NOT NULL COMMENT 'person profile last name',
  email varchar(128) NOT NULL COMMENT 'The person profile email address',
  phone varchar(24) DEFAULT NULL COMMENT 'The person profile phone number',
  createdBy int(11) NOT NULL COMMENT 'The user ID that created the row',
  updatedBy int(11) NOT NULL DEFAULT '0' COMMENT 'The user ID that updated the row',
  deletedBy int(11) NOT NULL DEFAULT '0' COMMENT 'The user ID that deleted the row',
  createdDate datetime NOT NULL COMMENT 'The date the row was created',
  updatedDate datetime DEFAULT NULL COMMENT 'The date the row was updated',
  deletedDate datetime DEFAULT NULL COMMENT 'The date the row was deleted',
  PRIMARY KEY (id),
  UNIQUE KEY akProfile (slug,email)
)
ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci CHECKSUM=0 DELAY_KEY_WRITE=0
;

CREATE TABLE IF NOT EXISTS profiledocument (
  id INT(11) NOT NULL AUTO_INCREMENT COMMENT 'Document ID',
  profileID INT(11) NOT NULL COMMENT 'Profile ID',
  documentType VARCHAR(32) NOT NULL DEFAULT 'Certificate' COMMENT 'The type of document (certificates, photos, images etc.)',
  name VARCHAR(64) NOT NULL COMMENT 'Document display (friendly) name',
  startDate DATETIME NOT NULL COMMENT 'The date document was uploaded',
  finishDate DATETIME DEFAULT NULL COMMENT 'The expiration date of the document',
  fileName VARCHAR(255) DEFAULT NULL COMMENT 'The file physical location (path) and unique file name on the file system.',
  fileType VARCHAR(32) DEFAULT NULL COMMENT 'The file type (.pdf, .jpg, .doc etc.)',
  fileSize INT(11) DEFAULT NULL COMMENT 'The size of the uploaded file in mb.',
  createdBy INT(11) NOT NULL COMMENT 'The user ID that created the row',
  updatedBy INT(11) DEFAULT NULL COMMENT 'The user ID that updated the row',
  deletedBy INT(11) DEFAULT NULL COMMENT 'The user ID that deleted the row',
  createdDate DATETIME NOT NULL COMMENT 'The date the row was created',
  updatedDate DATETIME DEFAULT NULL COMMENT 'The date the row was updated',
  deletedDate DATETIME DEFAULT NULL COMMENT 'The date the row was deleted',
  PRIMARY KEY (id),
  UNIQUE INDEX akProfileDocument (profileID, documentType, NAME)
)
ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci CHECKSUM=0 DELAY_KEY_WRITE=0
;

--  FK's
ALTER TABLE accountemployeeguard
  ADD CONSTRAINT fk1_accountemployeeguard FOREIGN KEY (accountID) REFERENCES accounts(id) ON
UPDATE RESTRICT ON DELETE RESTRICT
;

ALTER TABLE account_employee
  ADD CONSTRAINT fk1_account_employee FOREIGN KEY (accountID) REFERENCES accountemployeeguard
(accountID) ON UPDATE RESTRICT ON DELETE RESTRICT,
  ADD CONSTRAINT fk2_account_employee FOREIGN KEY (profileID) REFERENCES `profile`(id) ON UPDATE
RESTRICT ON DELETE RESTRICT
;

ALTER TABLE account_skill
  ADD CONSTRAINT fk1_account_skill FOREIGN KEY (accountID) REFERENCES accountemployeeguard
(accountID) ON UPDATE RESTRICT ON DELETE RESTRICT
;

ALTER TABLE account_group
  ADD CONSTRAINT fk1_account_group FOREIGN KEY (accountID) REFERENCES accountemployeeguard
(accountID) ON UPDATE RESTRICT ON DELETE RESTRICT
;

ALTER TABLE account_skill_group
  ADD CONSTRAINT fk1_account_skill_group FOREIGN KEY (skillID) REFERENCES account_skill(id) ON
UPDATE RESTRICT ON DELETE RESTRICT,
  ADD CONSTRAINT fk2_account_skill_group FOREIGN KEY (groupID) REFERENCES account_group(id) ON
UPDATE RESTRICT ON DELETE RESTRICT
;

ALTER TABLE account_skill_employee
  ADD CONSTRAINT fk1_account_skill_employee FOREIGN KEY (skillID) REFERENCES account_skill(id) ON
UPDATE RESTRICT ON DELETE RESTRICT,
  ADD CONSTRAINT fk2_account_skill_employee FOREIGN KEY (employeeID) REFERENCES account_employee
(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
  ADD CONSTRAINT fk3_account_skill_employee FOREIGN KEY (documentID) REFERENCES profileDocument
(id) ON UPDATE RESTRICT ON DELETE RESTRICT
;

ALTER TABLE account_group_employee
  ADD CONSTRAINT fk1_account_group_employee FOREIGN KEY (groupID) REFERENCES account_group(id) ON
UPDATE RESTRICT ON DELETE RESTRICT,
  ADD CONSTRAINT fk2_account_group_employee FOREIGN KEY (employeeID) REFERENCES account_employee
(id) ON UPDATE RESTRICT ON DELETE RESTRICT
;

ALTER TABLE `profile`
  ADD CONSTRAINT fk1_profile FOREIGN KEY (appUserID) REFERENCES app_user(id) ON UPDATE RESTRICT
ON DELETE RESTRICT
;

ALTER TABLE profiledocument
  ADD CONSTRAINT fk1_profiledocument FOREIGN KEY (profileID) REFERENCES `profile`(id) ON UPDATE
RESTRICT ON DELETE RESTRICT
;

