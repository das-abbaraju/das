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


insert into site_assignment (roleID, siteID, employeeID, createdBy, createdDate)
(select corporate_role.id, site_role.accountID, site_role_employee.employeeID, site_role.createdBy, site_role.createdDate from account_group corporate_role
	join account_group site_role on corporate_role.name = site_role.name
	join accounts a on corporate_role.accountID = a.id and a.type = 'Corporate'
	join account_group_employee site_role_employee on site_role_employee.groupID = site_role.id
	where corporate_role.accountID != site_role.accountID)
on duplicate key update site_assignment.createdBy = site_role.createdBy;