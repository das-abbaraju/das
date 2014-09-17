--liquibase formatted sql

--changeset dalvarado:7

-- Add a new email template for the Message Your CSR email

insert into `email_template`
(`id`, `accountID`, `templateName`, `subject`, `body`, `createdBy`, `creationDate`, `updatedBy`, `updateDate`, `listType`, `allowsVelocity`, `html`, `recipient`, `translated`, `requiredLanguages`)
values
('381','1100','Message Your CSR','Message Your CSR','This message was sent via the Message Your CSR function on the Contact Us page.\r\nContractor name: ${contractor.name}\r\nUser name: $!{user.name}\r\nUser email: $!{user.email}\r\n\r\n','79689',CURDATE(),'79689',CURDATE(),'Contractor','1','1','undefined','0','[\"en\"]');
