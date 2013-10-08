--liquibase formatted sql

--changeset gmeurer:1
CREATE TABLE IF NOT EXISTS `translation_usage` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `msgKey` varchar(100) NOT NULL,
  `msgLocale`   varchar(8) NOT NULL,
  `pageName` varchar(100) NOT NULL,
  `environment` varchar(20) DEFAULT NULL,
  `firstUsed` date DEFAULT NULL,
  `lastUsed` date DEFAULT NULL,
  `synchronizedBatch` varchar(40) DEFAULT NULL,
  `synchronizedDate` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `keyPageEnv` (`msgKey`, `msgLocale`, `pageName`,`environment`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;