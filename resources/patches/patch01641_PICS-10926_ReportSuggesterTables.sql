CREATE TABLE `calc_inherited_user_group`(
	`userID` int(11) NOT NULL  ,
	`groupID` int(11) NOT NULL  ,
	`usersInGroup` int(11) unsigned NOT NULL  DEFAULT 0 ,
	UNIQUE KEY `userGroup`(`userID`,`groupID`) ,
	KEY `group`(`groupID`) ,
	CONSTRAINT `FK_calc_inherited_group`
	FOREIGN KEY (`groupID`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE ,
	CONSTRAINT `FK_calc_inherited_user`
	FOREIGN KEY (`userID`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET='utf8';


CREATE TABLE `report_group_suggestion`(
	`groupID` int(11) NOT NULL  ,
	`reportID` int(11) NOT NULL  ,
	`score` float NOT NULL  DEFAULT 0 ,
	UNIQUE KEY `groupReport`(`groupID`,`reportID`) ,
	KEY `report`(`reportID`) ,
	CONSTRAINT `FK_group_suggestion_group`
	FOREIGN KEY (`groupID`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE ,
	CONSTRAINT `FK_group_suggestion_report`
	FOREIGN KEY (`reportID`) REFERENCES `report` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET='utf8';
