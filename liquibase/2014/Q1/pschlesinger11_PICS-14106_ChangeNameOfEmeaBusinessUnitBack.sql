--liquibase formatted sql

--changeset pschlesinger:11
update
  ref_sap_business_unit
set
  businessUnit = 'UK_EU'
where
  businessUnit = 'EMEA'
;
