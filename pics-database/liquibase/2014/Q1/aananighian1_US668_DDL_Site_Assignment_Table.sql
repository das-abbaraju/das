--liquibase formatted sql

--changeset aananighian:1
CREATE TABLE IF NOT EXISTS `site_assignment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `roleID` int(11) NOT NULL,
  `siteID` int(11) NOT NULL,
  `employeeID` int(11) NOT NULL,
  `createdBy` int(11) NOT NULL,
  `updatedBy` int(11) NOT NULL DEFAULT '0',
  `deletedBy` int(11) NOT NULL DEFAULT '0',
  `createdDate` datetime NOT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ak1_site_assignment` (`roleID`,`siteID`,`employeeID`),
  KEY `fk2_site_assignment_idx` (`siteID`),
  KEY `fk3_site_assignment_idx` (`employeeID`),
  CONSTRAINT `fk1_site_assignment` FOREIGN KEY (`roleID`) REFERENCES `account_group` (`id`),
  CONSTRAINT `fk2_site_assignment` FOREIGN KEY (`siteID`) REFERENCES `accountemployeeguard` (`accountID`),
  CONSTRAINT `fk3_site_assignment` FOREIGN KEY (`employeeID`) REFERENCES `account_employee` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
