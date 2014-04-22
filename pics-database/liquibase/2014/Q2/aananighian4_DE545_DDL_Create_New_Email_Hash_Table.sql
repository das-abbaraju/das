--liquibase formatted sql

--changeset aananighian:4
CREATE TABLE IF NOT EXISTS `employee_email_hash` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Email Hash ID',
  `hashCode` varchar(256) NOT NULL COMMENT 'Hash Code for the email',
  `employeeID` int(11) NOT NULL COMMENT 'Employee ID',
  `email` varchar(128) NOT NULL COMMENT 'Email address the sign-up email was sent to',
  `createdDate` datetime NOT NULL COMMENT 'Date the hashCode was created',
  `expirationDate` datetime NOT NULL COMMENT 'Date the hashCode expires',
  PRIMARY KEY (`id`),
  UNIQUE KEY `ak2Employee_Email_Hash` (`employeeID`,`email`),
  KEY `akEmployee_Email_Hash` (`hashCode`),
  CONSTRAINT `fk1_employee_email_hash` FOREIGN KEY (`employeeID`) REFERENCES `account_employee` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
