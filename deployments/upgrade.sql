-- -----------------------------------------------------------------------------------------------
-- THIS FILE IS FOR CHANGES TO NON-CONFIG TABLES THAT CANNOT BE APPLIED UNTIL RELEASE TIME
-- EXAMPLES:
-- -- data conversion
-- REFER TO config_tables.txt FOR A FULL LIST OF CONFIG TABLES
-- SEE upgradeConfig.sql FOR CONFIG CHANGES
-- -----------------------------------------------------------------------------------------------

-- PICS-6188 Update Notes
Update note n 
join app_translation a on a.msgKey='FlagCriteria.837.description' and a.locale='en' 
set n.body=a.msgValue 
where n.body='FlagCriteria.837.description'; 

Update note n 
join app_translation a on a.msgKey='FlagCriteria.833.description' and a.locale='en' 
set n.body=a.msgValue 
where n.body='FlagCriteria.833.description'; 

Update note n 
join app_translation a on a.msgKey='FlagCriteria.832.description' and a.locale='en' 
set n.body=a.msgValue 
where n.body='FlagCriteria.832.description'; 

Update note n 
join app_translation a on a.msgKey='FlagCriteria.796.description' and a.locale='en' 
set n.body=a.msgValue 
where n.body='FlagCriteria.796.description';

-- PICS-6158 0 TRIR, set to inherit
update ref_trade set naicsTRIR=null where id=247;

-- PICS-6307 Set PICS UK to complete if needed
update
contractor_audit_operator cao
join contractor_audit_operator caoOld on cao.auditID = caoOld.auditID
set cao.status=caoOld.status, cao.percentComplete = caoOld.percentComplete, cao.percentVerified=caoOld.percentVerified
where cao.opID=9 
and cao.visible=1 
AND cao.status = 'Pending'
and caoOld.opID=23675 
and caoOld.visible=0
AND caoOld.status='Complete';

