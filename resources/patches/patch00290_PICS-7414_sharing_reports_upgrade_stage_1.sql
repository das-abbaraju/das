CREATE TABLE IF NOT EXISTS `report_permission_account`(
	`id` int(11) NOT NULL  auto_increment , 
	`createdBy` int(11) NULL  , 
	`updatedBy` int(11) NULL  , 
	`creationDate` datetime NULL  , 
	`updateDate` datetime NULL  , 
	`reportID` int(11) NOT NULL  , 
	`accountID` int(11) NULL  , 
	PRIMARY KEY (`id`) , 
	UNIQUE KEY `user_report`(`accountID`,`reportID`) , 
	KEY `user_report_report`(`reportID`) , 
	KEY `user_report_user`(`accountID`) 
) ENGINE=InnoDB DEFAULT CHARSET='utf8';

CREATE TABLE IF NOT EXISTS `report_permission_user`(
	`id` int(11) NOT NULL  auto_increment , 
	`createdBy` int(11) NULL  , 
	`updatedBy` int(11) NULL  , 
	`creationDate` datetime NULL  , 
	`updateDate` datetime NULL  , 
	`reportID` int(11) NOT NULL  , 
	`userID` int(11) NULL  , 
	`editable` tinyint(1) NOT NULL  , 
	PRIMARY KEY (`id`) , 
	UNIQUE KEY `user_report`(`userID`,`reportID`) , 
	KEY `user_report_report`(`reportID`) , 
	KEY `user_report_user`(`userID`) 
) ENGINE=InnoDB DEFAULT CHARSET='utf8';

ALTER TABLE `report_user` 
	CHANGE `reportID` `reportID` int(11)   NOT NULL after `updateDate`, 
	CHANGE `userID` `userID` int(11)   NOT NULL after `reportID`, 
	ADD COLUMN `lastViewedDate` datetime   NULL after `userID`, 
	CHANGE `lastOpened` `lastOpened` datetime   NULL after `lastViewedDate`, 
	CHANGE `editable` `editable` tinyint(1)   NOT NULL DEFAULT '0' after `lastOpened`, 
	CHANGE `favorite` `favorite` tinyint(1)   NOT NULL DEFAULT '0' after `editable`, 
	ADD COLUMN `sortOrder` int(11)   NOT NULL DEFAULT '100' after `favorite`, 
	CHANGE `favoriteSortIndex` `favoriteSortIndex` int(11)   NOT NULL DEFAULT '100' after `sortOrder`, 
	ADD COLUMN `viewCount` int(11)   NOT NULL DEFAULT '0' after `favoriteSortIndex`, COMMENT='';

ALTER TABLE `report_user`
ADD CONSTRAINT `FK_report_user_user` 
FOREIGN KEY (`userID`) REFERENCES `users` (`id`);

ALTER TABLE `report_user`
ADD CONSTRAINT `FK_report_user` 
FOREIGN KEY (`reportID`) REFERENCES `report` (`id`) ON DELETE CASCADE;
