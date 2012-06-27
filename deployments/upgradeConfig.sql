-- -----------------------------------------------------------------------------------------------
-- THIS FILE IS FOR CHANGES TO CONFIG TABLES THAT CANNOT BE APPLIED UNTIL RELEASE TIME
-- EXAMPLES:
-- -- changing app_translation msgKeys
-- REFER TO config_tables.txt FOR A FULL LIST OF CONFIG TABLES
-- SEE upgrade.sql FOR NON-CONFIG CHANGES
-- -----------------------------------------------------------------------------------------------

-- PICS-5940
insert into pics_alpha1.audit_question_function_watcher 
(functionID, questionID, uniqueCode, createdBy, updatedBy, creationDate, updateDate)
values
(56, 9099, 'totalHours', 37745, 37745, NOW(), NOW());
