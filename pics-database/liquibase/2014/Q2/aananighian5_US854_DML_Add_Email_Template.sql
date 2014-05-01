--liquibase formatted sql

--changeset aananighian:5
insert into `email_template`
(`id`, `accountID`, `templateName`, `subject`, `body`, `createdBy`, `creationDate`, `updatedBy`, `updateDate`, `listType`, `allowsVelocity`, `html`, `recipient`, `translated`, `requiredLanguages`)
values
('385','1100','EmployeeGUARD Feedback','EmployeeGUARD Feedback','Account Name: ${accountName}\r\nappUserId: ${appUserId}\r\nUser Email Address: ${userEmailAddress}\r\nFeedback: ${feedback}','53137',CURDATE(),'53137',CURDATE(),'User','1','1','undefined','0','[\"en\"]')
on duplicate key update accountID = accountID;