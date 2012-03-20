-- -----------------------------------------------------------------------------------------------
-- THIS FILE IS FOR CHANGES TO CONFIG TABLES THAT CANNOT BE APPLIED UNTIL RELEASE TIME
-- EXAMPLES:
-- -- changing app_translation msgKeys
-- REFER TO config_tables.txt FOR A FULL LIST OF CONFIG TABLES
-- SEE upgrade.sql FOR NON-CONFIG CHANGES
-- -----------------------------------------------------------------------------------------------
update app_translation t set t.msgValue = 'PICS#if($invoice.status.paid) Payment Receipt for#end Invoice $invoice.id#if($invoice.status.unpaid && !${contractor.ccExpired}) - THE CREDIT CARD ON FILE WILL BE CHARGED ON THE DUE DATE#end' where t.msgKey like '%EmailTemplate.45.translatedSubject%' and t.locale = 'en';
update app_translation t set t.msgValue = replace(t.msgValue,'${billingUser.','${user.') where t.msgKey like '%EmailTemplate.45.translatedBody%';
update app_translation t set t.msgValue = replace(t.msgValue,'${contractor.state}','#if(${contractor.state}) ${contractor.state}#end');
update app_translation t set t.msgValue = replace(t.msgValue,'${contractor.billingState}','#if(${contractor.billingState}) ${contractor.billingState}#end') where t.msgKey like '%EmailTemplate.45.translatedBody%';
update app_translation t set t.msgValue = replace(t.msgValue,'width="100"','width="100%"') where t.msgKey like '%EmailTemplate.45.translatedBody%';
update app_translation t set t.msgValue = replace(t.msgValue,'height="100"','height="100%"') where t.msgKey like '%EmailTemplate.45.translatedBody%';