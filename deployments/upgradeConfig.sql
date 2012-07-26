-- -----------------------------------------------------------------------------------------------
-- THIS FILE IS FOR CHANGES TO CONFIG TABLES THAT CANNOT BE APPLIED UNTIL RELEASE TIME
-- EXAMPLES:
-- -- changing app_translation msgKeys
-- REFER TO config_tables.txt FOR A FULL LIST OF CONFIG TABLES
-- SEE upgrade.sql FOR NON-CONFIG CHANGES
-- -----------------------------------------------------------------------------------------------

-- PICS-6485 Open Task Updates
update app_translation 
set qualityRating=2, msgValue='<a href="https://www.picsorganizer.com/Audit.action?auditID={0,number,#}">Please fix issues with your {1}{2,choice,0#|1# for {3}} Policy</a>' 
where msgKey='ContractorWidget.message.FixPolicyIssues' and locale='en';

update app_translation 
set qualityRating=1
where msgKey='ContractorWidget.message.FixPolicyIssues' and locale!='en';

update app_translation 
set qualityRating=2, msgValue='Please <a href="https://www.picsorganizer.com/Audit.action?auditID={0,number,#}">upload and submit your {1}{2,choice,0#|1# for {3}} Policy</a>' 
where msgKey='ContractorWidget.message.UploadAndSubmitPolicy' and locale='en';

update app_translation 
set qualityRating=1
where msgKey='ContractorWidget.message.UploadAndSubmitPolicy' and locale!='en';
