-- -----------------------------------------------------------------------------------------------
-- THIS FILE IS FOR CHANGES TO NON-CONFIG TABLES THAT CANNOT BE APPLIED UNTIL RELEASE TIME
-- EXAMPLES:
-- -- data conversion
-- REFER TO config_tables.txt FOR A FULL LIST OF CONFIG TABLES
-- SEE upgradeConfig.sql FOR CONFIG CHANGES
-- -----------------------------------------------------------------------------------------------

-- PICS-4756 Remove orphaned questions
-- NOTE: This should be done before the config upgrade
-- Step 1 of 3
DELETE pd from pqfdata pd
join audit_question aq on aq.id=pd.questionID
Left join  audit_category ac on aq.categoryID=ac.id
where ac.id is null;

-- PICS-5416 update insurance policies to be day after answer of expiration date, bump contractors
-- update auto 
update contractor_audit ca 
join contractor_info ci on ci.id=ca.conID 
join pqfdata pd on pd.auditID = ca.id 
set ca.expiresDate=DATE_ADD(STR_TO_DATE(pd.answer, '%m/%d/%Y'), INTERVAL 1 DAY), 
ci.needsRecalculation=ci.needsRecalculation + 1, 
ci.lastRecalculation=NULL 
where ca.auditTypeID = 15 
and ca.expiresDate > NOW() 
and pd.questionID=2111 
and STR_TO_DATE(pd.answer, '%m/%d/%Y') != '0000-00-00' 
AND Date_Format(ca.expiresDate, '%Y-%m-%d')>DATE_ADD(STR_TO_DATE(pd.answer, '%m/%d/%Y'), INTERVAL 1 DAY); 

-- update general 
update contractor_audit ca 
join contractor_info ci on ci.id=ca.conID 
join pqfdata pd on pd.auditID = ca.id 
set ca.expiresDate=DATE_ADD(STR_TO_DATE(pd.answer, '%m/%d/%Y'), INTERVAL 1 DAY), 
ci.needsRecalculation=ci.needsRecalculation + 1, 
ci.lastRecalculation=NULL 
where ca.auditTypeID= 13 
and ca.expiresDate > NOW() 
and pd.questionID=2082 
and STR_TO_DATE(pd.answer, '%m/%d/%Y') != '0000-00-00' 
AND Date_Format(ca.expiresDate, '%Y-%m-%d')>DATE_ADD(STR_TO_DATE(pd.answer, '%m/%d/%Y'), INTERVAL 1 DAY); 

-- update excess 
update contractor_audit ca 
join contractor_info ci on ci.id=ca.conID 
join pqfdata pd on pd.auditID = ca.id 
set ca.expiresDate=DATE_ADD(STR_TO_DATE(pd.answer, '%m/%d/%Y'), INTERVAL 1 DAY), 
ci.needsRecalculation=ci.needsRecalculation + 1, 
ci.lastRecalculation=NULL 
where ca.auditTypeID = 16 
and ca.expiresDate > NOW() 
and pd.questionID=2117 
and STR_TO_DATE(pd.answer, '%m/%d/%Y') != '0000-00-00' 
AND Date_Format(ca.expiresDate, '%Y-%m-%d')>DATE_ADD(STR_TO_DATE(pd.answer, '%m/%d/%Y'), INTERVAL 1 DAY); 

-- update workers comp 
update contractor_audit ca 
join contractor_info ci on ci.id=ca.conID 
join pqfdata pd on pd.auditID = ca.id 
set ca.expiresDate=DATE_ADD(STR_TO_DATE(pd.answer, '%m/%d/%Y'), INTERVAL 1 DAY), 
ci.needsRecalculation=ci.needsRecalculation + 1, 
ci.lastRecalculation=NULL 
where ca.auditTypeID = 14 
and ca.expiresDate > NOW() 
and pd.questionID=2105 
and STR_TO_DATE(pd.answer, '%m/%d/%Y') != '0000-00-00' 
AND Date_Format(ca.expiresDate, '%Y-%m-%d')>DATE_ADD(STR_TO_DATE(pd.answer, '%m/%d/%Y'), INTERVAL 1 DAY);
