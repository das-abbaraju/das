ALTER TABLE `users` 
	ADD COLUMN `API` tinyint(4)   NOT NULL DEFAULT '0' after `usingDynamicReports`, 
	ADD COLUMN `APIKey` varchar(20)  COLLATE latin1_swedish_ci NULL after `API`, COMMENT='';