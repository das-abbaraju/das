--liquibase formatted sql

--changeset lkam:10
--preConditions onFail MARK_RAN
CREATE TABLE IF NOT EXISTS eula(
  id INT NOT NULL AUTO_INCREMENT,
  NAME VARCHAR(200) NOT NULL,
  creationDate DATETIME NOT NULL,
  createdBy INT NOT NULL,
  updateDate DATETIME NOT NULL,
  updatedBy INT NOT NULL,
  versionNumber INT NOT NULL,
  isoCode VARCHAR(11) NOT NULL,
  eulaBody TEXT NOT NULL,
  PRIMARY KEY (id)
);

ALTER TABLE eula
  ADD CONSTRAINT FK_EULA_REF_COUNTRY FOREIGN KEY (isoCode) REFERENCES ref_country(isoCode);

CREATE TABLE IF NOT EXISTS eula_agreement(
  id INT NOT NULL AUTO_INCREMENT,
  creationDate DATETIME NOT NULL,
  createdBy INT NOT NULL,
  updateDate DATETIME NOT NULL,
  updatedBy INT NOT NULL,
  userId INT NOT NULL,
  eulaId INT NOT NULL,
  PRIMARY KEY (id)
);


ALTER TABLE  eula_agreement
  ADD CONSTRAINT FK_EULA_AGREEMENT_EULA FOREIGN KEY (eulaId) REFERENCES eula(id);


ALTER TABLE  eula_agreement
  ADD CONSTRAINT FK_EULA_AGREEMENT_USERS FOREIGN KEY (userId) REFERENCES users(id);
