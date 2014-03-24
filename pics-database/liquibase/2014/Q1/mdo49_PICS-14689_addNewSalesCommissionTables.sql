CREATE TABLE invoice_operator_commission(
	id int(11) NOT NULL  auto_increment ,
	invoiceID int(11) NOT NULL  ,
	opID int(11) NOT NULL  ,
	createdBy int(11) NOT NULL  ,
	creationDate datetime NOT NULL  ,
	updatedBy int(11) NOT NULL  ,
	updateDate datetime NOT NULL  ,
	revenue decimal(11,7) NOT NULL  ,
	PRIMARY KEY (id) ,
	UNIQUE KEY invoiceOpID(invoiceID,opID) ,
	KEY operator(opID) ,
	CONSTRAINT invoice
	FOREIGN KEY (invoiceID) REFERENCES invoice (id) ON DELETE CASCADE ON UPDATE CASCADE ,
	CONSTRAINT operator
	FOREIGN KEY (opID) REFERENCES accounts (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET='utf8';

CREATE TABLE payment_operator_commission(
	id int(11) NOT NULL  auto_increment ,
	invoiceOperatorCommissionID int(11) NOT NULL  ,
	paymentID int(11) NOT NULL  ,
	createdBy int(11) NOT NULL  ,
	creationDate datetime NOT NULL  ,
	updatedBy int(11) NOT NULL  ,
	updateDate datetime NOT NULL  ,
	paymentAmount decimal(9,2) NOT NULL  ,
	PRIMARY KEY (id) ,
	UNIQUE KEY payment_commission(paymentID,invoiceOperatorCommissionID) ,
	KEY invoiceOperatorCommission(invoiceOperatorCommissionID) ,
	CONSTRAINT payment
	FOREIGN KEY (paymentID) REFERENCES invoice (id) ON DELETE CASCADE ON UPDATE CASCADE ,
	CONSTRAINT invoiceOperatorCommission
	FOREIGN KEY (invoiceOperatorCommissionID) REFERENCES invoice_operator_commission (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET='utf8';
