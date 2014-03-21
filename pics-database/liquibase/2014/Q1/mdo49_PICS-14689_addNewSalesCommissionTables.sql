--liquibase formatted sql

--changeset mdo:49
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
CREATE TABLE invoice_operator_commission(
	id INT(11) NOT NULL  AUTO_INCREMENT ,
	invoiceID INT(11) NOT NULL  ,
	opID INT(11) NOT NULL  ,
	createdBy INT(11) NOT NULL  ,
	creationDate DATETIME NOT NULL  ,
	updatedBy INT(11) NOT NULL  ,
	updateDate DATETIME NOT NULL  ,
	revenue DECIMAL(11,7) NOT NULL  ,
	PRIMARY KEY (id) ,
	KEY invoiceID(invoiceID)
) ENGINE=INNODB DEFAULT CHARSET='utf8';

CREATE TABLE payment_operator_commission(
	id INT(11) NOT NULL  AUTO_INCREMENT ,
	invoiceOperatorCommissionID INT(11) NOT NULL  ,
	paymentID INT(11) NOT NULL  ,
	createdBy INT(11) NOT NULL  ,
	creationDate DATETIME NOT NULL  ,
	updatedBy INT(11) NOT NULL  ,
	updateDate DATETIME NOT NULL  ,
	paymentAmount DECIMAL(9,2) NOT NULL  ,
	PRIMARY KEY (id) ,
	KEY payment_commission(paymentID,invoiceOperatorCommissionID) ,
	KEY commission(invoiceOperatorCommissionID)
) ENGINE=INNODB DEFAULT CHARSET='utf8';
