-- -----------------------------------------------------------------------------------------------
-- THIS FILE IS FOR CHANGES TO NON-CONFIG TABLES THAT CANNOT BE APPLIED UNTIL RELEASE TIME
-- EXAMPLES:
-- -- data conversion
-- REFER TO config_tables.txt FOR A FULL LIST OF CONFIG TABLES
-- SEE upgradeConfig.sql FOR CONFIG CHANGES
-- -----------------------------------------------------------------------------------------------

-- PICS-5567 COHS Stattistics table
update pqfdata pd 
join contractor_audit ca on ca.id = pd.auditID 
join pqfdata pd2 on pd2.auditID = pd.auditID 
LEFT join pqfdata pd3 on pd3.auditID = pd.auditID 
set pd3.answer='0.00' 
where pd.questionID=8840 and pd.answer='No' 
and pd2.questionID=2066 and pd2.answer='Yes' 
and (pd3.questionID=11117 OR pd3.questionID=11118);