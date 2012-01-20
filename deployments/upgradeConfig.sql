-- -----------------------------------------------------------------------------------------------
-- THIS FILE IS FOR CHANGES TO CONFIG TABLES THAT CANNOT BE APPLIED UNTIL RELEASE TIME
-- EXAMPLES:
-- -- changing app_translation msgKeys
-- REFER TO config_tables.txt FOR A FULL LIST OF CONFIG TABLES
-- SEE upgrade.sql FOR NON-CONFIG CHANGES
-- -----------------------------------------------------------------------------------------------

-- PICS-3967
update audit_type set classType='Employee' where classType='IM' or id in (29, 99, 100);


-- PICS-4157 
insert into `app_translation` (`msgKey`, `locale`, `msgValue`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`, `lastUsed`) values('RegistrationMakePayment.Processing','en','Processing Payment - Please wait.','941','941','2012-01-09 11:33:35','2012-01-09 11:33:35',NULL);

-- PICS-4070
insert into `app_translation` (`msgKey`, `locale`, `msgValue`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`, `lastUsed`, `qualityRating`, `sourceLanguage`) values('JS.global.print','en','Print','941','941','2012-01-19 17:03:27','2012-01-19 17:03:27',NULL,'4','en');