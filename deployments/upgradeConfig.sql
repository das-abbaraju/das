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

-- PICS-5672 Add ability to add help text to workflow state buttons on audit
-- Note: There are no steps to go to pending or resubmit
insert into app_translation 
(msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage, contentDriven)
select CONCAT('WorkflowStep.', w.id, '.name') as msgKey, t2.locale, t2.msgValue, 37745, 37745, NOW(),  NOW(), t2.qualityRating, t2.applicable, t2.sourceLanguage, t2.contentDriven   
from app_translation t2 
join workflow_step w on w.newStatus = 'Incomplete'
where t2.msgKey='AuditStatus.Incomplete.button';

insert into app_translation 
(msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage, contentDriven)
select CONCAT('WorkflowStep.', w.id, '.name') as msgKey, t2.locale, t2.msgValue, 37745, 37745, NOW(),  NOW(), t2.qualityRating, t2.applicable, t2.sourceLanguage, t2.contentDriven   
from app_translation t2 
join workflow_step w on w.newStatus = 'Submitted'
where t2.msgKey='AuditStatus.Submitted.button';

insert into app_translation 
(msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage, contentDriven)
select CONCAT('WorkflowStep.', w.id, '.name') as msgKey, t2.locale, t2.msgValue, 37745, 37745, NOW(),  NOW(), t2.qualityRating, t2.applicable, t2.sourceLanguage, t2.contentDriven   
from app_translation t2 
join workflow_step w on w.newStatus = 'Resubmitted'
where t2.msgKey='AuditStatus.Resubmitted.button';

insert into app_translation 
(msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage, contentDriven)
select CONCAT('WorkflowStep.', w.id, '.name') as msgKey, t2.locale, t2.msgValue, 37745, 37745, NOW(),  NOW(), t2.qualityRating, t2.applicable, t2.sourceLanguage, t2.contentDriven   
from app_translation t2 
join workflow_step w on w.newStatus = 'Complete'
where t2.msgKey='AuditStatus.Complete.button';

insert into app_translation 
(msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage, contentDriven)
select CONCAT('WorkflowStep.', w.id, '.name') as msgKey, t2.locale, t2.msgValue, 37745, 37745, NOW(),  NOW(), t2.qualityRating, t2.applicable, t2.sourceLanguage, t2.contentDriven   
from app_translation t2 
join workflow_step w on w.newStatus = 'Approved'
where t2.msgKey='AuditStatus.Approved.button';

insert into app_translation 
(msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage, contentDriven)
select CONCAT('WorkflowStep.', w.id, '.name') as msgKey, t2.locale, t2.msgValue, 37745, 37745, NOW(),  NOW(), t2.qualityRating, t2.applicable, t2.sourceLanguage, t2.contentDriven   
from app_translation t2 
join workflow_step w on w.newStatus = 'NotApplicable'
where t2.msgKey='AuditStatus.NotApplicable.button';

