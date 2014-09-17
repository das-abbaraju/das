--liquibase formatted sql
--changeset sshacter:11

--
--  NOTE: This must be ran in the pics_translations database!
--
--  Archive tables
CREATE TABLE IF NOT EXISTS	log_event
(
  id SERIAL,	-- id bigINT(20) NOT NULL AUTO_INCREMENT,
  logStart DATETIME NOT NULL 	COMMENT "Log transaction date.",
  logFinish DATETIME NOT NULL	COMMENT "Log transaction finish date for period history.",
  validStart DATE NOT NULL 	COMMENT "Start date this fact was valid in reality.",
  validFinish DATE NOT NULL 	COMMENT "Finish date this fact was valid in reality.",
  dmlType ENUM("INSERT", "UPDATE", "DELETE", "VIEW")	COMMENT "Type of data manipulation (INSERT, UPDATE, DELETE, VIEW).",
  ddlName VARCHAR(128) DEFAULT NULL 	COMMENT "Name of data definition (table) being logged.",
  ddlKey INT(11) NOT NULL 	COMMENT "The primary key value of the table being logged (PK from ddlName).",
  logSeq INT(11) DEFAULT NULL 	COMMENT "The log sequence number for contiguous history.",
  userName VARCHAR(128) NOT NULL 	COMMENT "The user logging the changes.",
  logYear INT(11) DEFAULT NULL 	COMMENT "Log transaction date year.",
  logMonth INT(11) DEFAULT NULL 	COMMENT "Log transaction date month.",
  logWeek INT(11) DEFAULT NULL 	COMMENT "Log transaction date week.",
  logDay INT(11) DEFAULT NULL 	COMMENT "Log transaction date day.",
  logQtr INT(11) DEFAULT NULL 	COMMENT "Log transaction date quarter.",
  logEntry TEXT DEFAULT NULL 	COMMENT "The XML description of changes."
) ENGINE=ARCHIVE AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci
;

CREATE TABLE IF NOT EXISTS	log_msg_key
(
  logID BIGINT(20) NOT NULL,
  id INT(11) NOT NULL,
  msgKey VARCHAR(100) NOT NULL,
  description VARCHAR(255) DEFAULT NULL,
  js TINYINT(1) NOT NULL DEFAULT '0' COMMENT '1 indicates translation applies to javascript',
  firstUsed DATE DEFAULT NULL,
  lastUsed DATE DEFAULT NULL,
  createdBy INT(11) DEFAULT NULL,
  updatedBy INT(11) DEFAULT NULL,
  creationDate DATETIME DEFAULT NULL,
  updateDate DATETIME DEFAULT NULL
)
ENGINE=ARCHIVE DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci
;

CREATE TABLE IF NOT EXISTS	log_msg_locale
(
  logID BIGINT(20) NOT NULL,
  id INT(11) NOT NULL,
  keyID INT(11) NOT NULL,
  locale VARCHAR(8) NOT NULL,
  msgValue TEXT,
  firstUsed DATE DEFAULT NULL,
  lastUsed DATE DEFAULT NULL,
  qualityRating INT(4) NOT NULL DEFAULT '0',
  createdBy INT(11) DEFAULT NULL,
  updatedBy INT(11) DEFAULT NULL,
  creationDate DATETIME DEFAULT NULL,
  updateDate DATETIME DEFAULT NULL
)
ENGINE=ARCHIVE DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci
;
