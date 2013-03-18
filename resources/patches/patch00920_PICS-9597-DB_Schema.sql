ALTER TABLE `report` 
ADD COLUMN `ownerID` int(11) NOT NULL after `id`, 
CHANGE `createdBy` `createdBy` int(11) NULL after `ownerID`, 
CHANGE `updatedBy` `updatedBy` int(11) NULL after `createdBy`, 
CHANGE `creationDate` `creationDate` datetime NULL after `updatedBy`, 
CHANGE `updateDate` `updateDate` datetime NULL after `creationDate`, 
CHANGE `modelType` `modelType` varchar(30) COLLATE utf8_general_ci NOT NULL after `updateDate`, 
CHANGE `name` `name` varchar(100) COLLATE utf8_general_ci NOT NULL after `modelType`, 
CHANGE `description` `description` text COLLATE utf8_general_ci NULL after `name`, 
CHANGE `parameters` `parameters` text COLLATE utf8_general_ci NULL after `description`, 
CHANGE `filterExpression` `filterExpression` varchar(100) COLLATE utf8_general_ci NULL after `parameters`, COMMENT='';

CREATE TABLE `report_permission_group`(
`id` int(11) NOT NULL auto_increment , 
`createdBy` int(11) NULL , 
`updatedBy` int(11) NULL , 
`creationDate` datetime NULL , 
`updateDate` datetime NULL , 
`reportID` int(11) NOT NULL , 
`userID` int(11) NULL , 
`editable` tinyint(1) NOT NULL DEFAULT '0' , 
`owner` tinyint(1) NOT NULL DEFAULT '0' , 
`permissionLevel` varchar(20) COLLATE utf8_general_ci NOT NULL DEFAULT ''View'' , 
PRIMARY KEY (`id`) , 
UNIQUE KEY `user_report`(`userID`,`reportID`) , 
KEY `user_report_report`(`reportID`) , 
KEY `user_report_user`(`userID`) 
) ENGINE=InnoDB DEFAULT CHARSET='utf8';

Truncate table report_permission_group;
