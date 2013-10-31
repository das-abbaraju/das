--liquibase formatted sql
--changeset sshacter:6

SET FOREIGN_KEY_CHECKS  = 0;

CREATE TABLE IF NOT EXISTS project (
  id INT(11) NOT NULL AUTO_INCREMENT,
  accountID INT(11) NOT NULL,
  NAME VARCHAR(50) COLLATE utf8_bin NOT NULL,
  location VARCHAR(50) COLLATE utf8_bin DEFAULT NULL,
  startDate DATE DEFAULT NULL,
  endDate DATE DEFAULT NULL,
  createdBy INT(11) NOT NULL,
  createdDate DATETIME NOT NULL,
  updatedBy INT(11) DEFAULT NULL,
  updatedDate DATETIME DEFAULT NULL,
  deletedBy INT(11) DEFAULT NULL,
  deletedDate DATETIME DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE INDEX ak1_Project (id, accountID)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci
;

CREATE TABLE IF NOT EXISTS project_account_group (
  id INT(10) NOT NULL AUTO_INCREMENT,
  projectID INT(11) NOT NULL,
  groupID INT(11) NOT NULL,
  createdBy INT(11) NOT NULL,
  createdDate DATETIME NOT NULL,
  updatedBy INT(11) DEFAULT NULL,
  updatedDate DATETIME DEFAULT NULL,
  deletedBy INT(11) DEFAULT NULL,
  deletedDate DATETIME DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE INDEX ak1_project_account_group (projectID, groupID)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci
;

CREATE TABLE IF NOT EXISTS	project_account_skill (
  id INT(10) NOT NULL AUTO_INCREMENT,
  projectID INT(11) NOT NULL,
  skillID INT(11) NOT NULL,
  createdBy INT(11) NOT NULL,
  createdDate DATETIME NOT NULL,
  updatedBy INT(11) DEFAULT NULL,
  updatedDate DATETIME DEFAULT NULL,
  deletedBy INT(11) DEFAULT NULL,
  deletedDate DATETIME DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE INDEX ak1_project_account_skill (projectID, skillID)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci
;

CREATE TABLE IF NOT EXISTS  project_account (
  id INT(11) NOT NULL AUTO_INCREMENT,
  projectID INT(11) NOT NULL,
  accountID INT(11) NOT NULL,
  createdBy INT(11) NOT NULL,
  createdDate DATETIME NOT NULL,
  updatedBy INT(11) DEFAULT NULL,
  updatedDate DATETIME DEFAULT NULL,
  deletedBy INT(11) DEFAULT NULL,
  deletedDate DATETIME DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE INDEX ak1_project_account (projectID, accountID)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci
;

ALTER TABLE project
  ADD CONSTRAINT fk1_project FOREIGN KEY (accountID) REFERENCES accountemployeeguard
(accountID) ON UPDATE RESTRICT ON DELETE RESTRICT
;
ALTER TABLE project_account_group
  ADD CONSTRAINT fk1_project_account_group FOREIGN KEY (projectID) REFERENCES project(id) ON
UPDATE RESTRICT ON DELETE RESTRICT
;
ALTER TABLE project_account_skill
  ADD CONSTRAINT fk1_project_account_skill FOREIGN KEY (projectID) REFERENCES project(id) ON
UPDATE RESTRICT ON DELETE RESTRICT
;
ALTER TABLE project_account
  ADD CONSTRAINT fk1_project_account FOREIGN KEY (projectID) REFERENCES project(id) ON UPDATE
RESTRICT ON DELETE RESTRICT
;
