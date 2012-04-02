-- -----------------------------------------------------------------------------------------------
-- THIS FILE IS FOR CHANGES TO NON-CONFIG TABLES THAT CANNOT BE APPLIED UNTIL RELEASE TIME
-- EXAMPLES:
-- -- data conversion
-- REFER TO config_tables.txt FOR A FULL LIST OF CONFIG TABLES
-- SEE upgradeConfig.sql FOR CONFIG CHANGES
-- -----------------------------------------------------------------------------------------------

-- PICS-5331 Unable close out / complete welcome calls
-- update category to applicable
UPDATE
audit_cat_data acd
set acd.applies=1
where acd.categoryID=130
And acd.applies=0;

-- update PICS-Global to be visible
UPDATE 
contractor_audit_operator cao
join contractor_audit ca on ca.id = cao.auditID
set cao.visible=1
where ca.auditTypeID = 9
And cao.opID = 4 and cao.visible=0;