--liquibase formatted sql

--changeset aananighian:5
insert into `email_template`
(`id`, `accountID`, `templateName`, `subject`, `body`, `createdBy`, `creationDate`, `updatedBy`, `updateDate`, `listType`, `allowsVelocity`, `html`, `recipient`, `translated`, `requiredLanguages`)
values
('385','1100','EmployeeGUARD Feedback','EmployeeGUARD Feedback',' Account Name: ${accountName}\r\n appUserId: ${appUserId}\r\n User Email Address: ${userEmailAddress}\r\n Feedback: ${feedback}\r\n\r\n','53137',CURDATE(),'53137',CURDATE(),'User','1','1','undefined','0','[\"en\"]')
on duplicate key update accountID = accountID;