-- -----------------------------------------------------------------------------------------------
-- THIS FILE IS FOR CHANGES TO NON-CONFIG TABLES THAT CANNOT BE APPLIED UNTIL RELEASE TIME
-- EXAMPLES:
-- -- data conversion
-- REFER TO config_tables.txt FOR A FULL LIST OF CONFIG TABLES
-- SEE upgradeConfig.sql FOR CONFIG CHANGES
-- -----------------------------------------------------------------------------------------------
-- PICS-5193 New LC COR features
-- insert feature toggle
insert ignore into app_properties (property, value) values ('Toggle.LcCor', 0);
-- initial selection of canadian contractors
Update contractor_info ci
join accounts a on a.id=ci.id
set ci.lcCorPhase='RemindMeLater', ci.lcCorNotification=CURDATE();
where a.country='CA' and a.type='Contractor' and a.status='Active';
