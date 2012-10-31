ALTER TABLE `report` 
	DROP COLUMN `private`, COMMENT='';

ALTER TABLE `report_user` 
	CHANGE `favorite` `favorite` tinyint(1)   NOT NULL DEFAULT '0' after `lastViewedDate`, 
	CHANGE `sortOrder` `sortOrder` int(11)   NOT NULL DEFAULT '100' after `favorite`, 
	CHANGE `viewCount` `viewCount` int(11)   NOT NULL DEFAULT '0' after `sortOrder`, 
	DROP COLUMN `lastOpened`, 
	DROP COLUMN `editable`, 
	DROP COLUMN `favoriteSortIndex`, COMMENT=''; 
