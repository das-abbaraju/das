DROP TABLE IF EXISTS `email_template`;

CREATE TABLE `email_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accountID` mediumint(8) unsigned NOT NULL,
  `templateName` varchar(50) NOT NULL,
  `subject` varchar(150) DEFAULT NULL,
  `body` text,
  `createdBy` int(11) DEFAULT NULL,
  `creationDate` datetime DEFAULT NULL,
  `updatedBy` int(11) DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL,
  `listType` varchar(50) DEFAULT 'Contractor',
  `allowsVelocity` tinyint(4) NOT NULL DEFAULT '0',
  `html` tinyint(4) NOT NULL DEFAULT '0',
  `recipient` varchar(10) DEFAULT NULL,
  `translated` tinyint(4) NOT NULL DEFAULT '0',
  `requiredLanguages` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `accountID` (`accountID`,`templateName`,`listType`)
) ENGINE=InnoDB AUTO_INCREMENT=234 DEFAULT CHARSET=latin1;