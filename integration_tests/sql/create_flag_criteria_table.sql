DROP TABLE IF EXISTS `flag_criteria`;

CREATE TABLE `flag_criteria` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category` varchar(50) NOT NULL,
  `questionID` smallint(6) unsigned DEFAULT NULL,
  `auditTypeID` smallint(6) unsigned DEFAULT NULL,
  `oshaType` varchar(6) DEFAULT NULL,
  `oshaRateType` varchar(30) DEFAULT NULL,
  `createdBy` int(11) DEFAULT NULL,
  `updatedBy` int(11) DEFAULT NULL,
  `creationDate` datetime DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL,
  `label` varchar(30) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `comparison` varchar(10) DEFAULT NULL,
  `dataType` varchar(10) NOT NULL DEFAULT 'boolean',
  `defaultValue` varchar(100) DEFAULT NULL,
  `multiYearScope` varchar(25) DEFAULT NULL,
  `allowCustomValue` tinyint(4) NOT NULL DEFAULT '1',
  `flaggableWhenMissing` tinyint(4) NOT NULL DEFAULT '0',
  `insurance` tinyint(4) NOT NULL DEFAULT '0',
  `displayOrder` smallint(6) NOT NULL DEFAULT '0',
  `requiredStatus` varchar(15) DEFAULT NULL,
  `optionCode` varchar(25) DEFAULT NULL,
  `requiredStatusComparison` varchar(10) DEFAULT NULL,
  `requiredLanguages` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `category` (`category`),
  KEY `questionID` (`questionID`),
  KEY `auditTypeID` (`auditTypeID`)
) ENGINE=InnoDB AUTO_INCREMENT=868 DEFAULT CHARSET=latin1;