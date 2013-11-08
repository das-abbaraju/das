--liquibase formatted sql
--changeset sshacter:9

--  Set up foreign key relationships
ALTER TABLE	project_account_group
ADD CONSTRAINT 	fk2_project_account_group
FOREIGN KEY 	(groupID) REFERENCES account_group(id)
;

ALTER TABLE	project_account_skill
ADD CONSTRAINT 	fk2_project_account_skill
FOREIGN KEY 	(skillID) REFERENCES account_skill(id)
;

--  Create new tables
DROP TABLE IF EXISTS	project_account_group_employee
;
CREATE TABLE IF NOT EXISTS	project_account_group_employee
(
  id INT(11) NOT NULL AUTO_INCREMENT COMMENT 'Row Number',
  projectID INT(11) NOT NULL COMMENT 'Unique Project ID from project_account_group',
  groupID INT(11) NOT NULL COMMENT 'Unique Group ID from project_account_group',
  employeeID INT(11) NOT NULL COMMENT 'Unique Employee ID from account_employee',
  createdBy INT(11) NOT NULL COMMENT 'The user ID that created the row',
  updatedBy INT(11) DEFAULT NULL COMMENT 'The user ID that updated the row',
  deletedBy INT(11) DEFAULT NULL COMMENT 'The user ID that deleted the row',
  createdDate DATETIME NOT NULL COMMENT 'The date the row was created',
  updatedDate DATETIME DEFAULT NULL COMMENT 'The date the row was updated',
  deletedDate DATETIME DEFAULT NULL COMMENT 'The date the row was deleted',

  PRIMARY KEY (id),
  UNIQUE KEY ak1_project_account_group_employee (projectID, groupID, employeeID),
  CONSTRAINT fk1_project_account_group_employee FOREIGN KEY (projectID, groupID) REFERENCES project_account_group (projectID, groupID),
  CONSTRAINT fk2_project_account_group_employee FOREIGN KEY (employeeID) REFERENCES account_employee (id)
)
ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci
;

DROP TABLE IF EXISTS	project_account_skill_group
;
CREATE TABLE IF NOT EXISTS	project_account_skill_group
(
  id INT(11) NOT NULL AUTO_INCREMENT COMMENT 'Row Number',
  projectID INT(11) NOT NULL COMMENT 'Unique Project ID from project_account_group',
  skillID INT(11) NOT NULL COMMENT 'Unique Skill ID from project_account_skill',
  groupID INT(11) NOT NULL COMMENT 'Unique Group ID from project_account_group',
  createdBy INT(11) NOT NULL COMMENT 'The user ID that created the row',
  updatedBy INT(11) DEFAULT NULL COMMENT 'The user ID that updated the row',
  deletedBy INT(11) DEFAULT NULL COMMENT 'The user ID that deleted the row',
  createdDate DATETIME NOT NULL COMMENT 'The date the row was created',
  updatedDate DATETIME DEFAULT NULL COMMENT 'The date the row was updated',
  deletedDate DATETIME DEFAULT NULL COMMENT 'The date the row was deleted',

  PRIMARY KEY (id),
  UNIQUE KEY ak1_project_account_skill_group (projectID, skillID, groupID),
  CONSTRAINT fk1_project_account_skill_group FOREIGN KEY (projectID, groupID) REFERENCES project_account_group (projectID, groupID),
  CONSTRAINT fk2_project_account_skill_group FOREIGN KEY (projectID, skillID) REFERENCES project_account_skill (projectID, skillID)
)
ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci
;
