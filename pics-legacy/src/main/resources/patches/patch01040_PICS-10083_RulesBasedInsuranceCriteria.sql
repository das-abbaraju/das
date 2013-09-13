CREATE TABLE IF NOT EXISTS flag_criteria_contractor_operator (
  id INT(11) NOT NULL AUTO_INCREMENT,
  contractorID INT(11) NOT NULL,
  operatorID INT(11) NOT NULL,
  criteriaID INT(11) NOT NULL,
  insuranceLimit INT(11) DEFAULT NULL,
  updatedBy INT(11) DEFAULT NULL,
  creationDate DATETIME DEFAULT NULL,
  updateDate DATETIME DEFAULT NULL,
  createdBy INT(11) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY fcco (contractorID,operatorID,criteriaID)
) ENGINE=INNODB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

INSERT IGNORE INTO app_properties (property, VALUE, ticklerDate) VALUES('Toggle.RBIC','operatorId in []',NULL);