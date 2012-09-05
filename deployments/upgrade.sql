-- -----------------------------------------------------------------------------------------------
-- THIS FILE IS FOR CHANGES TO NON-CONFIG TABLES THAT CANNOT BE APPLIED UNTIL RELEASE TIME
-- EXAMPLES:
-- -- data conversion
-- REFER TO config_tables.txt FOR A FULL LIST OF CONFIG TABLES
-- SEE upgradeConfig.sql FOR CONFIG CHANGES
-- -----------------------------------------------------------------------------------------------
-- PICS-5193 New LC COR features
insert ignore into app_properties (property, value) values ('ToggleLcCor', 0);
