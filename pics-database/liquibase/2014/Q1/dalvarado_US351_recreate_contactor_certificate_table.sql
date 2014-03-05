--liquibase formatted sql

--changeset mdo:17
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
DROP TABLE IF EXISTS contractor_certificate;

CREATE TABLE contractor_certificate( id INT(11) NOT NULL AUTO_INCREMENT, contractorId INT(11) NOT NULL, expirationDate DATETIME NOT NULL, issueDate DATETIME NOT NULL, certificateType VARCHAR(20) NOT NULL, certificationMethod VARCHAR(20) NOT NULL, PRIMARY KEY (id) );
