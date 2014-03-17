--liquibase formatted sql

--changeset kchase:33

-- update delimiters
update ref_sap_business_unit
set address = replace(address, '\\n', ' | ');
