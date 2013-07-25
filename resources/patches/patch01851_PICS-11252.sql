ALTER TABLE `email_subscription` 
	DROP KEY `secondaryKey`, ADD UNIQUE KEY `secondaryKey`(`userID`,`subscription`,`reportID`) ;