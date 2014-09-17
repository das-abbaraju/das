--liquibase formatted sql

--changeset pschlesinger:10
update
  ref_sap_business_unit
set
  businessUnit = 'EMEA'
where
  businessUnit = 'UK_EU'
;
