CREATE TABLE `password_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userID` int(11) NOT NULL,
  `passwordHash` varchar(50) NOT NULL,
  `endDate` datetime DEFAULT NULL,
  `createdBy` int(11) DEFAULT NULL,
  `updatedBy` int(11) DEFAULT NULL,
  `creationDate` datetime DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `secondaryKey` (`userID`,`passwordHash`,`endDate`)
)  ENGINE=INNODB DEFAULT CHARSET=utf8;


ALTER TABLE `password_history`
ADD CONSTRAINT `FK_password_history_user` FOREIGN KEY (`userID`) REFERENCES `users` (`id`) ON DELETE CASCADE;

ALTER TABLE `users`
DROP COLUMN `passwordHistory`;

ALTER TABLE `accounts`
ADD COLUMN `passwordSecurityLevelId` varchar(4) DEFAULT 0;

-- Set PasswordSecurityLevel = High for all Nassco acccounts
UPDATE accounts SET passwordSecurityLevelId = 2 WHERE id IN (33250, 33251, 33252, 33253, 34502);

