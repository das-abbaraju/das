--liquibase formatted SQL
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
CREATE TABLE IF NOT EXISTS	project_account_group_employee
(
  id INT(11) NOT NULL AUTO_INCREMENT COMMENT 'Row Number',
  projectGroupID INT(11) NOT NULL COMMENT 'Unique ID (technically Project ID, Group ID) from project_account_group',
  employeeID INT(11) NOT NULL COMMENT 'Unique Employee ID from account_employee',
  createdBy INT(11) NOT NULL COMMENT 'The user ID that created the row',
  updatedBy INT(11) DEFAULT NULL COMMENT 'The user ID that updated the row',
  deletedBy INT(11) DEFAULT NULL COMMENT 'The user ID that deleted the row',
  createdDate DATETIME NOT NULL COMMENT 'The date the row was created',
  updatedDate DATETIME DEFAULT NULL COMMENT 'The date the row was updated',
  deletedDate DATETIME DEFAULT NULL COMMENT 'The date the row was deleted',

  PRIMARY KEY (id),
  UNIQUE KEY ak1_project_account_group_employee (projectGroupID, employeeID),
  CONSTRAINT fk1_project_account_group_employee FOREIGN KEY (projectGroupID) REFERENCES project_account_group (id),
  CONSTRAINT fk2_project_account_group_employee FOREIGN KEY (employeeID) REFERENCES account_employee (id)
)
ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci
;

CREATE TABLE IF NOT EXISTS	project_account_skill_group
(
  id INT(11) NOT NULL AUTO_INCREMENT COMMENT 'Row Number',
  projectGroupID INT(11) NOT NULL COMMENT 'Unique ID (technically Project ID, Group ID) from project_account_group',
  projectSkillID INT(11) NOT NULL COMMENT 'Unique ID (technically Project ID, Skill ID) from project_account_skill',
  createdBy INT(11) NOT NULL COMMENT 'The user ID that created the row',
  updatedBy INT(11) DEFAULT NULL COMMENT 'The user ID that updated the row',
  deletedBy INT(11) DEFAULT NULL COMMENT 'The user ID that deleted the row',
  createdDate DATETIME NOT NULL COMMENT 'The date the row was created',
  updatedDate DATETIME DEFAULT NULL COMMENT 'The date the row was updated',
  deletedDate DATETIME DEFAULT NULL COMMENT 'The date the row was deleted',

  PRIMARY KEY (id),
  UNIQUE KEY ak1_project_account_skill_group (projectGroupID, projectSkillID),
  CONSTRAINT fk1_project_account_skill_group FOREIGN KEY (projectGroupID) REFERENCES project_account_group (id),
  CONSTRAINT fk2_project_account_skill_group FOREIGN KEY (projectSkillID) REFERENCES project_account_skill (id)
)
ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci
;

CREATE TABLE IF NOT EXISTS	project_account_skill_employee
(
  id INT(11) NOT NULL AUTO_INCREMENT COMMENT 'Row Number',
  projectID INT(11) NOT NULL COMMENT 'Unique ID from project',
  skillEmployeeID INT(11) NOT NULL COMMENT 'Unique ID (technically Skill ID, Employee ID) from account_skill_employee',
  createdBy INT(11) NOT NULL COMMENT 'The user ID that created the row',
  updatedBy INT(11) DEFAULT NULL COMMENT 'The user ID that updated the row',
  deletedBy INT(11) DEFAULT NULL COMMENT 'The user ID that deleted the row',
  createdDate DATETIME NOT NULL COMMENT 'The date the row was created',
  updatedDate DATETIME DEFAULT NULL COMMENT 'The date the row was updated',
  deletedDate DATETIME DEFAULT NULL COMMENT 'The date the row was deleted',

  PRIMARY KEY (id),
  UNIQUE KEY ak1_project_account_skill_employee (projectID, skillEmployeeID),
  CONSTRAINT fk1_project_account_skill_employee FOREIGN KEY (projectID) REFERENCES project (id),
  CONSTRAINT fk2_project_account_skill_employee FOREIGN KEY (skillEmployeeID) REFERENCES account_skill_employee (id)
)
ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci
;

CREATE TABLE IF NOT EXISTS	site_account_skill
(
  id INT(11) NOT NULL AUTO_INCREMENT,
  siteID INT(11) NOT NULL,
  skillID INT(11) NOT NULL,
  createdBy INT(11) NOT NULL,
  updatedBy INT(11) DEFAULT '0',
  deletedBy INT(11) DEFAULT '0',
  createdDate DATETIME NOT NULL,
  updatedDate DATETIME DEFAULT NULL,
  deletedDate DATETIME DEFAULT NULL,

  PRIMARY KEY (id),
  UNIQUE KEY site_account_skill (siteID, skillID),
  CONSTRAINT fk1_site_account_skill FOREIGN KEY (siteID) REFERENCES account_skill(accountID),
  CONSTRAINT fk2_site_account_skill FOREIGN KEY (skillID) REFERENCES account_skill(id)
)
ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci
;
