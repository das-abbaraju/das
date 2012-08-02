-- -----------------------------------------------------------------------------------------------
-- THIS FILE IS FOR CHANGES TO CONFIG TABLES THAT CANNOT BE APPLIED UNTIL RELEASE TIME
-- EXAMPLES:
-- -- changing app_translation msgKeys
-- REFER TO config_tables.txt FOR A FULL LIST OF CONFIG TABLES
-- SEE upgrade.sql FOR NON-CONFIG CHANGES
-- -----------------------------------------------------------------------------------------------

-- PICS-6465 AFR Updates
-- Update UK DOFR to exposure hours
update audit_question_function_watcher set questionID=9100 where id=190;
-- Update Germany AFR
update audit_question_function_watcher SET questionID=9840, uniqueCode='totalHours' where id=166;
