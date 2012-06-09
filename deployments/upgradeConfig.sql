-- -----------------------------------------------------------------------------------------------
-- THIS FILE IS FOR CHANGES TO CONFIG TABLES THAT CANNOT BE APPLIED UNTIL RELEASE TIME
-- EXAMPLES:
-- -- changing app_translation msgKeys
-- REFER TO config_tables.txt FOR A FULL LIST OF CONFIG TABLES
-- SEE upgrade.sql FOR NON-CONFIG CHANGES
-- -----------------------------------------------------------------------------------------------

-- PICS-4756 Remove orphaned questions
-- NOTE: step 1 is data and therefore needs to be run on live before step 3, removing the orphaned questions
-- Step 1 of 3
-- DELETE pd from pqfdata pd
-- join audit_question aq on aq.id=pd.questionID
-- Left join  audit_category ac on aq.categoryID=ac.id
-- where ac.id is null;

-- Step 2 of 3
DELETE tran from audit_question aq
join app_translation tran on tran.msgKey = concat('AuditQuestion.', aq.id, ".name")
left join audit_category ac on aq.categoryID=ac.id
where ac.id is null;

-- Step 3 of 3: Perrform last
DELETE aq from audit_question aq
Left join  audit_category ac on aq.categoryID=ac.id
where ac.id is null;

