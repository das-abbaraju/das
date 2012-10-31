insert ignore into app_translation (`msgKey`,`locale`,`msgValue`,`createdBy`,`updatedBy`,`creationDate`,`updateDate`,`qualityRating`,`applicable`)
	values 
	("ContractorOperatorNumberType.Oracle","en","Oracle",47739,47739,now(),now(),2,1),
	("ProfileEdit.label.ApiKey","en","API Key",47739,47739,now(),now(),2,1),
	("ProfileEdit.button.GenerateApiKey","en","Generate API Key",47739,47739,now(),now(),2,1),
	("ProfileEdit.alert.ExistingApiKeyWarning","en","Are you sure?  Changing the API key will require changing all of the API calls that rely on this API key.",47739,47739,now(),now(),2,1),
	("OpPerms.RestApi.description","en","REST API",47739,47739,now(),now(),2,1),
	("OpPerms.RestApi.helpText","en","Allows for machine-to-machine interfacing via certain API calls.",47739,47739,now(),now(),2,1);

-- This line creates a user group called PICS API Approver
insert ignore into `users` (`username`, `isGroup`, `name`, `isActive`, `accountID`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`,`timezone`, `forcePasswordReset`, `needsIndexing`, `locale`) 
	values('GROUP1100PICS API Approver','Yes','PICS API Approver','Yes','1100','63932','63932',now(),now(),'US/Central','0','1','en_US');

-- This line gives the PICS API Approver group the right to grant the RestApi flag to other users/groups.	
insert ignore into useraccess (`userId`,`accessType`,`viewFlag`,`grantFlag`,`lastUpdate`,`grantedByID`) 
	values ((select id from `users` where `name` = "PICS API Approver"),"RestApi",0,1,now(),1);
	
-- This line makes Trevor Allred (tallred, #941) a member of the PICS API Approver group.	
insert ignore into usergroup (userID,groupID,`createdBy`, `updatedBy`, `creationDate`, `updateDate`) 
	values (941,(select id from `users` where `name` = "PICS API Approver"),'63932','63932',now(),now());	
	
-- This line gives the "TXI Corporate Admin" group the right to grant the RestApi flag to other users/groups.	
insert ignore into useraccess (`userId`,`accessType`,`viewFlag`,`grantFlag`,`lastUpdate`,`grantedByID`) 
	values ((select id from users where `name` = "TXI Corporate Admin" and isGroup = 1),"RestApi",0,1,now(),1);
