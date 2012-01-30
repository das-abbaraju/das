-- -----------------------------------------------------------------------------------------------
-- THIS FILE IS FOR CHANGES TO CONFIG TABLES THAT CANNOT BE APPLIED UNTIL RELEASE TIME
-- EXAMPLES:
-- -- changing app_translation msgKeys
-- REFER TO config_tables.txt FOR A FULL LIST OF CONFIG TABLES
-- SEE upgrade.sql FOR NON-CONFIG CHANGES
-- -----------------------------------------------------------------------------------------------

-- PICS-1525
update app_translation a 
set a.msgValue = '{0} to {1}' 
where a.msgKey = 'ScheduleAudit.link.DateSelector'
and a.locale = 'en';

insert into `app_translation` (`msgKey`, `locale`, `msgValue`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`, `lastUsed`, `qualityRating`, `sourceLanguage`) 
values('global.timezone','en','Time Zone','941','941','2012-01-27 12:00:00','2012-01-27 12:00:00',NULL,'4','en');

insert into `app_translation` (`msgKey`, `locale`, `msgValue`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`, `lastUsed`, `qualityRating`, `sourceLanguage`) 
values('TimeZone.Asia.Japan','en','Japan Standard Time','941','941','2012-01-27 12:00:00','2012-01-27 12:00:00',NULL,'4','en');

insert into `app_translation` (`msgKey`, `locale`, `msgValue`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`, `lastUsed`, `qualityRating`, `sourceLanguage`) 
values('TimeZone.Asia.Calcutta','en','Calcutta Time','941','941','2012-01-27 12:00:00','2012-01-27 12:00:00',NULL,'4','en');

insert into `app_translation` (`msgKey`, `locale`, `msgValue`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`, `lastUsed`, `qualityRating`, `sourceLanguage`) 
values('TimeZone.Asia.Singapore','en','Peoples Republic of China Time','941','941','2012-01-27 12:00:00','2012-01-27 12:00:00',NULL,'4','en');