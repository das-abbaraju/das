DROP TABLE IF EXISTS `app_translation`;

CREATE TABLE `app_translation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `msgKey` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `locale` varchar(8) CHARACTER SET latin1 NOT NULL,
  `msgValue` text,
  `createdBy` int(11) DEFAULT NULL,
  `updatedBy` int(11) DEFAULT NULL,
  `creationDate` datetime DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL,
  `lastUsed` date DEFAULT NULL,
  `qualityRating` int(4) NOT NULL DEFAULT '0',
  `applicable` tinyint(4) NOT NULL DEFAULT '1',
  `sourceLanguage` varchar(8) CHARACTER SET latin1 DEFAULT 'en',
  `contentDriven` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `keyLocale` (`msgKey`,`locale`)
) ENGINE=InnoDB AUTO_INCREMENT=304006 DEFAULT CHARSET=utf8;
