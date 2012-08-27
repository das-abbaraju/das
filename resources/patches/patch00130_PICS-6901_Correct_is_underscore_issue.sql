ALTER TABLE `report_user` 
	ADD COLUMN `lastOpened` datetime   NULL after `updateDate`, 
	CHANGE `userID` `userID` int(11)   NOT NULL after `lastOpened`, 
	ADD COLUMN `editable` tinyint(1)   NULL after `reportID`, 
	ADD COLUMN `favorite` tinyint(1)   NULL after `editable`, 
	ADD COLUMN `favoriteSortIndex` int(11)   NOT NULL after `favorite`, 
	DROP COLUMN `lastUsed`, 
	DROP COLUMN `is_editable`, 
	DROP COLUMN `is_favorite`, COMMENT='';