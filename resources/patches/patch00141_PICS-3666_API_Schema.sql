-- PICS-3666 
-- Adding support for an API key to be used with the REST API to link the request to a (special) user account.

alter table `users` 
    add column `apiKey` varchar(36) DEFAULT NULL,
    add key `apiKey` (`apiKey`);    

-- Various corrections to the usergroup schema to bring it up to snuff
delete from `usergroup` 
where `userId` not in (select `id` from `users`)
or `groupId` not in (select `id` from `users`);
    
alter table `usergroup` 
	change `userID` `userID` int(11) NOT NULL, 
	change `groupID` `groupID` int(11) NOT NULL,
	change `creationDate` `creationDate` datetime NULL, 
	change `createdBy` `createdBy` int(11) NULL,
	add constraint `FK_usergroup_user` FOREIGN KEY (`userID`) REFERENCES `users` (`id`),
	add constraint `FK_usergroup_group` FOREIGN KEY (`groupID`) REFERENCES `users` (`id`);