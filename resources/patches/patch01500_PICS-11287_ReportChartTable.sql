DROP TABLE IF EXISTS report_chart;
CREATE TABLE `report_chart`(
	`id` INT(11) NOT NULL  AUTO_INCREMENT ,
	`createdBy` INT(11) NULL  ,
	`updatedBy` INT(11) NULL  ,
	`creationDate` DATETIME NULL  ,
	`updateDate` DATETIME NULL  ,
	`reportID` INT(11) NOT NULL  ,
	`widgetID` INT(11) NOT NULL  ,
	`chartType` VARCHAR(20) COLLATE latin1_swedish_ci NULL  ,
	`chartFormat` VARCHAR(20) COLLATE latin1_swedish_ci NULL  ,
	`series` VARCHAR(20) COLLATE latin1_swedish_ci NULL  ,
	PRIMARY KEY (`id`) ,
	UNIQUE KEY `reportUser`(`reportID`,`widgetID`)
) ENGINE=INNODB DEFAULT CHARSET=utf8, COLLATE=utf8_general_ci;
