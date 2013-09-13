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
