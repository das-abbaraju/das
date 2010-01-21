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

-- remove old password reminder email template
delete from email_template where id = 24;

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