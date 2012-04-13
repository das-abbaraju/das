-- -----------------------------------------------------------------------------------------------
-- THIS FILE IS FOR CHANGES TO CONFIG TABLES THAT CANNOT BE APPLIED UNTIL RELEASE TIME
-- EXAMPLES:
-- -- changing app_translation msgKeys
-- REFER TO config_tables.txt FOR A FULL LIST OF CONFIG TABLES
-- SEE upgrade.sql FOR NON-CONFIG CHANGES
-- -----------------------------------------------------------------------------------------------

-- PICS-5137
insert into app_translation (msgKey, msgValue, locale, qualityRating, applicable, sourceLanguage, createdBy, creationDate, updatedBy, updateDate, contentDriven) 
select CONCAT('AuditQuestion.', aq.id, '.columnHeader'), aq.columnHeader, 'en', 2, 1, 'en', 941, NOW(), 941, NOW(), 1 
from audit_question aq 
where 1 
and aq.columnHeader is not NULL 
and aq.columnHeader !='';
