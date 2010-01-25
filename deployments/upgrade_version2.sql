/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;

select os.applicable, os.auditid, pcd.applies from osha_audit os 
join pqfcatdata pcd on pcd.auditid = os.auditid
where os.applicable = 1
and pcd.applies = 'No'
and pcd.catID = 151; 

select os.applicable, os.auditid, pcd.applies from osha_audit os 
join pqfcatdata pcd on pcd.auditid = os.auditid
join contractor_audit ca on ca.id = os.auditid 
where os.applicable = 0
and pcd.applies = 'Yes'
and pcd.catID = 151;
**/

ALTER TABLE `accounts` ADD COLUMN `contactID` mediumint(9)   NULL after `fax`;

-- 8400+ accounts
update accounts, users set accounts.contactID = users.id
where users.accountID = accounts.id and users.isGroup = 'No' and accounts.contactID is null and accounts.type in ('Corporate', 'Operator', 'Contractor')
and users.name = accounts.contact;

-- 39 accounts
update accounts, users set accounts.contactID = users.id
where users.accountID = accounts.id and users.isGroup = 'No' and accounts.contactID is null and accounts.type in ('Corporate', 'Operator', 'Contractor')
and left(accounts.contact, 3) = LEFT(users.name,3) and accounts.email = users.email;


-- 311*2 accounts/users
update accounts, users set accounts.contactID = users.id, users.name = accounts.contact
where users.accountID = accounts.id and users.isGroup = 'No' and accounts.contactID is null and accounts.type = 'Contractor'
and left(contact, 1) = left(users.email,1) and accounts.email = users.email;

-- 56 accounts
update accounts, users set accounts.contactID = users.id
where users.accountID = accounts.id and users.isGroup = 'No' and accounts.contactID is null and accounts.type = 'Contractor'
and accounts.email = users.email;

-- insert 21 new users

insert into note (accountid, creationDate, createdBy, updateDate, updatedBy, summary,noteCategory, priority)
select id, now(), 1, now(),1, concat("Added a secondary Account Manager user ", contact), "Other", 1 from accounts a
where contactID is null and a.type = 'Contractor';

insert into users (username, email, name, accountID, phone, fax, createdBy, creationDate, updatedBy, updateDate)
select email, email, contact, id, phone, fax, 1, now(), 1, now() from accounts a
where contactID is null and a.type = 'Contractor';

-- update those same 21 accounts
update accounts, users set accounts.contactID = users.id
where users.accountID = accounts.id and users.isGroup = 'No' and accounts.contactID is null and accounts.type = 'Contractor'
and users.name = accounts.contact;


-- should be done with all contractors
-- select * from accounts where contactID is null order by type;

-- move over the phone for 3331 primary contact users who are missing phone
update users, accounts
set users.phone = accounts.phone
where users.id = accounts.contactID
and users.phone is null and accounts.phone is not null;

DROP table IF EXISTS temp_user;
create table temp_user(
	`id` int UNSIGNED NOT NULL AUTO_INCREMENT ,
	`status` char(1) , 
	`email` varchar(100) NOT NULL ,
	`username` varchar(100) , 
	`name` varchar(100) , 
	`phone` varchar(50) , 
	`fax` varchar(50) , 
	`userID` mediumint , 
	`accountID` int NOT NULL , 
	unique `username` (`username`),
	index `email` (`email`),
	PRIMARY KEY (`id`)
);


INSERT INTO temp_user (`status`, email, username, `name`, phone, accountID, userID)
select 'P', trim(u.email), username, u.name, u.phone, accountID, u.id from users u;

INSERT INTO temp_user (`status`, email, `name`, phone, accountID)
select 'S', trim(secondEmail), secondContact, secondPhone, c.id from contractor_info c
left join temp_user u on trim(secondEmail) = u.email and u.accountID = c.id
where secondEmail like '%@%'
and u.id is null;

INSERT INTO temp_user (`status`, email, `name`, phone, accountID)
select 'B', trim(billingEmail), billingContact, billingPhone,c.id from contractor_info c
left join temp_user u on trim(billingEmail) = u.email and u.accountID = c.id
where length(billingEmail) > 1
and billingEmail like '%@%'
and u.id is null;


update temp_user set `name` = email
where length(trim(name)) = 0;

-- Cleanup the dirty data
update temp_user set username = email where email = 'jpeel@ryko.com'; -- previous
update temp_user set username = email where email = 'cmozena@ryko.com'; -- new
update temp_user set username = email where email = 'scott.wozniak@uop.com'; -- previous
update temp_user set username = email where email = 'lisa.wichtendahl@uop.com'; -- new
update temp_user set username = email where email = 'willie@trinitytx.com'; -- previous
update temp_user set username = email where email = 'lydia@trinitytx.com'; -- new
update temp_user set username = email where email = 'kbender@bendercomm.com'; -- previous
update temp_user set username = email where email = 'donb@bendercomm.com'; -- new
update temp_user set username = email where email = 'hayden@oldsfiltration.com'; -- previous
update temp_user set username = email where email = 'beth@oldsfiltration.com'; -- new
update temp_user set username = email where email = 'jbrock@devoge.com'; -- previous
update temp_user set username = email where email = 'slwright@devoge.com'; -- new
update temp_user set username = email where email = 'wheeler@gregpoole.com'; -- previous
update temp_user set username = email where email = 'Erin.Perdun@GregPoole.com'; -- new
update temp_user set username = email where email = 'mike@cacradio.com'; -- previous
update temp_user set username = concat(email,accountID) where email = 'barb_kccom@msn.com' and accountID = 7499; -- new

update temp_user set username = concat(email,accountID) where email IN ('jp2way@vci.net','jp2way105@vci.net');
update temp_user set username = concat(email,accountID) where email = 'rob@atlantech-online.com' and accountID = 6364;
update temp_user set username = 'tricia@cambriacontracting.com', email = 'tricia@cambriacontracting.com' where email = 'tricia@cambriacontracting.com, kevin@cambriacontra' and accountID = 8247;
update temp_user set username = email where email = 'kevin@cambriacontracting.com' and accountID = 8247;
delete from temp_user WHERE email = 'johna@johnapapalasco.com';
delete from temp_user WHERE email = 'tricia@cambriacontracting.com' and status = 'B';

-- Check to see if there are any duplicates remaining
select * from temp_user t1
join temp_user t2 on t1.email = t2.username and t1.id != t2.id
where t1.username is null;
-- done with dirty data

DROP table IF EXISTS temp_user_duplicates;
create table temp_user_duplicates as
select email, min(accountID) account1, mAX(accountID) account2, count(*) total from temp_user group by email having count(*) > 1;

update temp_user set username = email
where username is null and email not in (select email from temp_user_duplicates);

update temp_user set username = concat(email,accountID)
where username is null;

-- update the 11 P users who have changed
update users, temp_user
set users.username = temp_user.username
where temp_user.userID = users.id and temp_user.username != users.username;

-- insert 7582 users
insert into users (username, email, name, accountID, phone, createdBy, creationDate, updatedBy, updateDate)
select username, email, `name`, accountID, phone, 1, now(), 1, now() from temp_user WHERE status != 'P';

-- Inserting notes for 4824 secondary contacts not already in users
insert into note (noteCategory, summary, priority, accountid, creationDate, createdBy, updateDate, updatedBy)
SELECT "Other" noteCategory, concat("Added admin user for [", email, "] from legacy secondary contact fields") summary, 1, accountID, now(), 1, now(), 1
FROM temp_user WHERE status = 'S'
UNION
SELECT "Other" noteCategory, concat("Added billing for [", email, "] from legacy billing contact fields") summary, 1, accountID, now(), 1, now(), 1
FROM temp_user WHERE status = 'B';

insert into useraccess (userID, accessType, viewFlag,editFlag, deleteFlag, grantFlag, lastUpdate, grantedByID)
select u.id, "ContractorAdmin", 1, 0, 0, 0, Now(), 1
FROM users u join temp_user t on u.username = t.username and t.status = 'S'
UNION
select u.id, "ContractorSafety", 1, 0, 0, 0, Now(), 1
FROM users u join temp_user t on u.username = t.username and t.status = 'S'
UNION
select u.id, "ContractorInsurance", 1, 0, 0, 0, Now(), 1
FROM users u join temp_user t on u.username = t.username and t.status = 'S'
UNION
select u.id, "ContractorBilling", 1, 0, 0, 0, Now(), 1
FROM users u join temp_user t on u.username = t.username and t.status IN ('S','B');


ALTER TABLE accounts DROP COLUMN contact, DROP COLUMN email;
ALTER TABLE contractor_info DROP COLUMN secondContact, DROP COLUMN secondPhone, DROP COLUMN secondEmail, DROP COLUMN billingContact, DROP COLUMN billingPhone, DROP COLUMN billingEmail;



-- update users set password = SHA1(CONCAT('@Irvine1',id));
-- update users set email = 'tester@picsauditing.com' where email > '';



-- remove old password reminder email template for Contractor and User
delete from email_template where id IN (3,24);

update email_template set body = 'Attn: <DisplayName>
This is an automatically generated email that will allow you to set or reset your password. Please click the following link and set your password on the following page.
${confirmLink}
If you did not request that this email be sent to you or if you have any questions, please contact us.
<PICSSignature>' where id = 85;

update email_template set body = 'Attn: <DisplayName>
This is an automatically generated email to remind you of your username(s) to log in to the PICS website.
The usernames and the accounts associated with this email address are:
#foreach( $user in $users )
${user.getUsername()} on ${user.getAccount().getName()}
#end
If you have any questions or did not request that this email be sent to you, please let us know.
<PICSSignature>' where id = 86;

update email_template set body = 'Hello <DisplayName>,
<MyName> has issued you a login for the ${accountname} account on PICS.
Please log in using the following link to set your password.
${confirmLink}
Have a great week,
<PICSSignature>' where id = 5;

-- Deleting the permissions "CHANGE PASSWORD" FROM THE UserAccess
delete from useraccess where accessType = 'ChangePassword'; 


-- Updating the tokens on the Emails
update token set velocityCode = '${contractor.primaryContact.email}'
where tokenId = 2; -- PrimaryEmail

update token set velocityCode = '${contractor.primaryContact.name}'
where tokenId = 3; -- ContactName

update token set velocityCode = '${contractor.primaryContact.username}'
where tokenId = 9; -- Username

update token set velocityCode = '${contractor.primaryContact.phone}'
where tokenId = 23; -- PrimaryPhone

delete from token where tokenId = 4 and tokenName = 'BillingEmail';
