-- -----------------------------------------------------------------------------------------------
-- THIS FILE IS FOR CHANGES TO CONFIG TABLES THAT CANNOT BE APPLIED UNTIL RELEASE TIME
-- EXAMPLES:
-- -- changing app_translation msgKeys
-- REFER TO config_tables.txt FOR A FULL LIST OF CONFIG TABLES
-- SEE upgrade.sql FOR NON-CONFIG CHANGES
-- -----------------------------------------------------------------------------------------------

-- PICS-4825
insert into app_translation(msgKey, msgValue, locale, createdBy, creationDate, applicable, qualityRating, contentDriven)
select concat('AuditQuestion.', id, '.title'), title, 'en', 23157, now(), 1, 2, 1
from audit_question
where length(title) > 0;

-- PICS-4826
insert into app_translation(msgKey, msgValue, locale, createdBy, creationDate, applicable, qualityRating, contentDriven)
select concat('AuditCategory.', id, '.helpText'), helpText, 'en', 23157, now(), 1, 2, 1
from audit_category
where length(helpText) > 0;