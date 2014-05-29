--liquibase formatted sql

--changeset dalvarado:11
--preConditions onFail MARK_RAN
CREATE TABLE IF NOT EXISTS contractor_location(
  id INT NOT NULL AUTO_INCREMENT,
  conId INT NOT NULL,
  latitude FLOAT(10,6) NOT NULL,
  longitude FLOAT(10,6) NOT NULL,
  creationDate DATETIME NOT NULL,
  createdBy INT NOT NULL,
  updateDate DATETIME NOT NULL,
  updatedBy INT NOT NULL,
  PRIMARY KEY (id)
);

ALTER TABLE contractor_location
  ADD CONSTRAINT FK_CON_LOC_CON_INFO FOREIGN KEY (conId) REFERENCES contractor_info(id);
