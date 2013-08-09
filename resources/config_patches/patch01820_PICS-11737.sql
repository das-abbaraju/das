-- add email field to ref_country
ALTER TABLE ref_country ADD COLUMN picsEmail VARCHAR(50) DEFAULT 'info@picsauditing.com';

-- add address field(s) to ref_sap_business_unit
ALTER TABLE ref_sap_business_unit ADD COLUMN displayName VARCHAR(50) DEFAULT 'PICS Auditing, LLC';
ALTER TABLE ref_sap_business_unit ADD COLUMN address VARCHAR(100) DEFAULT '17701 Cowan #140, Irvine, CA 92614';

-- add tokens
INSERT INTO token (tokenName, listType, velocityCode) VALUES
('CountrySpecificMainPhone'    , 'Contractor', '$contractor.country.phone'),
('CountrySpecificSalesPhone'   , 'Contractor', '$contractor.country.salesPhone'),
('CountrySpecificEmail'        , 'Contractor', '$contractor.country.picsEmail'),
('CountrySpecificPicsName'     , 'Contractor', '$contractor.country.businessUnit.displayName'),
('CountrySpecificOfficeAddress', 'Contractor', '$contractor.country.businessUnit.address');

-- find new token IDs
SELECT t.tokenID INTO @mainPhone  FROM token t WHERE t.tokenName = 'CountrySpecificMainPhone';
SELECT t.tokenID INTO @salesPhone FROM token t WHERE t.tokenName = 'CountrySpecificSalesPhone';
SELECT t.tokenID INTO @picsEmail  FROM token t WHERE t.tokenName = 'CountrySpecificEmail';
SELECT t.tokenID INTO @picsName   FROM token t WHERE t.tokenName = 'CountrySpecificPicsName';
SELECT t.tokenID INTO @address    FROM token t WHERE t.tokenName = 'CountrySpecificOfficeAddress';

-- insert translations for new tokens
INSERT INTO app_translation (msgKey, locale, msgValue) VALUES
(CONCAT('Token.', @mainPhone , '.velocityCode'), 'en', '$contractor.country.phone'),
(CONCAT('Token.', @salesPhone, '.velocityCode'), 'en', '$contractor.country.salesPhone'),
(CONCAT('Token.', @picsEmail , '.velocityCode'), 'en', '$contractor.country.picsEmail'),
(CONCAT('Token.', @picsName  , '.velocityCode'), 'en', '$contractor.country.businessUnit.displayName'),
(CONCAT('Token.', @address   , '.velocityCode'), 'en', '$contractor.country.businessUnit.address');