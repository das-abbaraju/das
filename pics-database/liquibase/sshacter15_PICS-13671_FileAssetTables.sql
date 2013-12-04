--liquibase formatted sql
--changeset sshacter:15

CREATE TABLE IF NOT EXISTS	file_asset
(
  id INT(11) NOT NULL AUTO_INCREMENT,
  guid VARCHAR(100) NOT NULL,
  mimeType VARCHAR(100) NOT NULL,
  uploadedBy VARCHAR(100) DEFAULT NULL,
  uploadedDate DATE DEFAULT NULL,
  etlStatus ENUM('PENDING','COMPLETE','FAILED','IMPORTED') DEFAULT 'PENDING',
  processDate DATETIME DEFAULT NULL,
  allRows INT(11) DEFAULT NULL,
  insRows INT(11) DEFAULT NULL,
  updRows INT(11) DEFAULT NULL,
  delRows INT(11) DEFAULT NULL,
  errRows INT(11) DEFAULT NULL,
  dupRows INT(11) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY guid (guid)
)
ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci
;

CREATE TABLE IF NOT EXISTS	file_asset_item
(
  id INT(11) NOT NULL AUTO_INCREMENT,
  fileAssetID INT(11) NOT NULL,
  msgKey VARCHAR(100) DEFAULT NULL,
  locale VARCHAR(8) DEFAULT NULL,
  msgValue TEXT,
  etlAction ENUM('INSERT','UPDATE','DELETE','VERIFY','REJECT','IGNORE') DEFAULT 'INSERT',
  etlActionResult MEDIUMTEXT,
  PRIMARY KEY (id),
  KEY fk1_file_asset_item (fileAssetID),
  CONSTRAINT fk1_file_asset_item FOREIGN KEY (fileAssetID) REFERENCES file_asset (id)
)
ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci
;
