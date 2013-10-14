--liquibase formatted sql

--changeset pschlesinger:3
update ref_sap_business_unit set sapCode = 'US' where businessUnit = 'US';
update ref_sap_business_unit set sapCode = 'CA' where businessUnit = 'Canada';
update ref_sap_business_unit set sapCode = 'EMEA' where businessUnit = 'UK_EU';
