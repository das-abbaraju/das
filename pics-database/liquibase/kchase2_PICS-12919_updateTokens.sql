--liquibase formatted sql

--changeset kchase:2
-- insert update tokens
Insert ignore into token (tokenID, tokenName, listType, velocityCode) values (31, 'CSRName', 'Contractor', '${contractor.currentCsr.name}');
Insert ignore into token (tokenID, tokenName, listType, velocityCode) values (32, 'CSRPhone', 'Contractor', '${contractor.country.csrPhone}');
Insert ignore into token (tokenID, tokenName, listType, velocityCode) values (34, 'CSRFax', 'Contractor', '${contractor.country.csrFax}');
Insert ignore into token (tokenID, tokenName, listType, velocityCode) values (33, 'CSREmail', 'Contractor', '${contractor.country.csrEmail}');
Insert ignore into token (tokenID, tokenName, listType, velocityCode) values (46, 'CountrySpecificPicsName', 'Contractor', '${contractor.country.businessUnit.displayName}');
Insert ignore into token (tokenID, tokenName, listType, velocityCode) values (47, 'CountrySpecificOfficeAddress', 'Contractor', '${contractor.country.businessUnit.addressSingleLine}');
Insert ignore into token (tokenID, tokenName, listType, velocityCode) values (43, 'CountrySpecificMainPhone', 'Contractor', '${contractor.country.phone}');
Insert ignore into token (tokenID, tokenName, listType, velocityCode) values (44, 'CountrySpecificSalesPhone', 'Contractor', '${contractor.country.salesPhone}');
Insert ignore into token (tokenID, tokenName, listType, velocityCode) values (45, 'CountrySpecificEmail', 'Contractor', '${contractor.country.picsEmail}');
Insert ignore into token (tokenID, tokenName, listType, velocityCode) values (85, 'CSRCountry', 'Contractor', '${contractor.country.csrCountry}');
Insert ignore into token (tokenID, tokenName, listType, velocityCode) values (67, 'CSRAddress', 'Contractor', '${contractor.country.csrAddress}');
Insert ignore into token (tokenID, tokenName, listType, velocityCode) values (68, 'CSRAddress2', 'Contractor', '${contractor.country.csrAddress2}');
Insert ignore into token (tokenID, tokenName, listType, velocityCode) values (69, 'CSRCity', 'Contractor', '${contractor.country.csrCity}');
Insert ignore into token (tokenID, tokenName, listType, velocityCode) values (70, 'CSRState', 'Contractor', '${contractor.country.csrCountrySubdivision}');
Insert ignore into token (tokenID, tokenName, listType, velocityCode) values (71, 'CSRZip', 'Contractor', '${contractor.country.csrZip}');
Insert ignore into token (tokenID, tokenName, listType, velocityCode) values (72, 'CountryCurrency', 'Contractor', '${contractor.country.currency}');
Insert ignore into token (tokenID, tokenName, listType, velocityCode) values (73, 'ISRPhone', 'Contractor', '${contractor.country.isrPhone}');
Insert ignore into token (tokenID, tokenName, listType, velocityCode) values (74, 'ISRFax', 'Contractor', '${contractor.country.isrFax}');
Insert ignore into token (tokenID, tokenName, listType, velocityCode) values (75, 'ISREmail', 'Contractor', '${contractor.country.isrEmail}');

update token set velocityCode='${contractor.currentCsr.name}' where tokenName='CSRName';
update token set velocityCode='${contractor.country.csrPhone}' where tokenName='CSRPhone';
update token set velocityCode='${contractor.country.csrFax}' where tokenName='CSRFax';
update token set velocityCode='${contractor.country.csrEmail}' where tokenName='CSREmail';
update token set velocityCode='${contractor.country.businessUnit.displayName}' where tokenName='CountrySpecificPicsName';
update token set velocityCode='${contractor.country.businessUnit.addressSingleLine}' where tokenName='CountrySpecificOfficeAddress';
update token set velocityCode='${contractor.country.phone}' where tokenName='CountrySpecificMainPhone';
update token set velocityCode='${contractor.country.salesPhone}' where tokenName='CountrySpecificSalesPhone';
update token set velocityCode='${contractor.country.picsEmail}' where tokenName='CountrySpecificEmail';
update token set velocityCode='${contractor.country.csrCountry}' where tokenName='CSRCountry';
update token set velocityCode='${contractor.country.csrAddress}' where tokenName='CSRAddress';
update token set velocityCode='${contractor.country.csrAddress2}' where tokenName='CSRAddress2';
update token set velocityCode='${contractor.country.csrCity}' where tokenName='CSRCity';
update token set velocityCode='${contractor.country.csrCountrySubdivision}' where tokenName='CSRState';
update token set velocityCode='${contractor.country.csrZip}' where tokenName='CSRZip';
update token set velocityCode='${contractor.country.currency}' where tokenName='CountryCurrency';
update token set velocityCode='${contractor.country.isrPhone}' where tokenName='ISRPhone';
update token set velocityCode='${contractor.country.isrFax}' where tokenName='ISRFax';
update token set velocityCode='${contractor.country.isrEmail}' where tokenName='ISREmail';

-- insert update app translations
Insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage) values ('Token.31.name', 'en', '${contractor.currentCsr.name}', 37745, 37745, NOW(), NOW(), 2, 1,'en');
Insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage) values ('Token.32.name', 'en', '${contractor.country.csrPhone}', 37745, 37745, NOW(), NOW(), 2, 1,'en');
Insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage) values ('Token.34.name', 'en', '${contractor.country.csrFax}', 37745, 37745, NOW(), NOW(), 2, 1,'en');
Insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage) values ('Token.33.name', 'en', '${contractor.country.csrEmail}', 37745, 37745, NOW(), NOW(), 2, 1,'en');
Insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage) values ('Token.46.name', 'en', '${contractor.country.businessUnit.displayName}', 37745, 37745, NOW(), NOW(), 2, 1,'en');
Insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage) values ('Token.47.name', 'en', '${contractor.country.businessUnit.addressSingleLine}', 37745, 37745, NOW(), NOW(), 2, 1,'en');
Insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage) values ('Token.43.name', 'en', '${contractor.country.phone}', 37745, 37745, NOW(), NOW(), 2, 1,'en');
Insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage) values ('Token.44.name', 'en', '${contractor.country.salesPhone}', 37745, 37745, NOW(), NOW(), 2, 1,'en');
Insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage) values ('Token.45.name', 'en', '${contractor.country.picsEmail}', 37745, 37745, NOW(), NOW(), 2, 1,'en');
Insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage) values ('Token.85.name', 'en', '${contractor.country.csrCountry}', 37745, 37745, NOW(), NOW(), 2, 1,'en');
Insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage) values ('Token.67.name', 'en', '${contractor.country.csrAddress}', 37745, 37745, NOW(), NOW(), 2, 1,'en');
Insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage) values ('Token.68.name', 'en', '${contractor.country.csrAddress2}', 37745, 37745, NOW(), NOW(), 2, 1,'en');
Insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage) values ('Token.69.name', 'en', '${contractor.country.csrCity}', 37745, 37745, NOW(), NOW(), 2, 1,'en');
Insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage) values ('Token.70.name', 'en', '${contractor.country.csrCountrySubdivision}', 37745, 37745, NOW(), NOW(), 2, 1,'en');
Insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage) values ('Token.71.name', 'en', '${contractor.country.csrZip}', 37745, 37745, NOW(), NOW(), 2, 1,'en');
Insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage) values ('Token.72.name', 'en', '${contractor.country.currency}', 37745, 37745, NOW(), NOW(), 2, 1,'en');
Insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage) values ('Token.73.name', 'en', '${contractor.country.isrPhone}', 37745, 37745, NOW(), NOW(), 2, 1,'en');
Insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage) values ('Token.74.name', 'en', '${contractor.country.isrFax}', 37745, 37745, NOW(), NOW(), 2, 1,'en');
Insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage) values ('Token.75.name', 'en', '${contractor.country.isrEmail}', 37745, 37745, NOW(), NOW(), 2, 1,'en');

update app_translation set msgValue='${contractor.currentCsr.name}' where msgKey='Token.31.velocityCode';
update app_translation set msgValue='${contractor.country.csrPhone}' where msgKey='Token.32.velocityCode';
update app_translation set msgValue='${contractor.country.csrFax}' where msgKey='Token.34.velocityCode';
update app_translation set msgValue='${contractor.country.csrEmail}' where msgKey='Token.33.velocityCode';
update app_translation set msgValue='${contractor.country.businessUnit.displayName}' where msgKey='Token.46.velocityCode';
update app_translation set msgValue='${contractor.country.businessUnit.addressSingleLine}' where msgKey='Token.47.velocityCode';
update app_translation set msgValue='${contractor.country.phone}' where msgKey='Token.43.velocityCode';
update app_translation set msgValue='${contractor.country.salesPhone}' where msgKey='Token.44.velocityCode';
update app_translation set msgValue='${contractor.country.picsEmail}' where msgKey='Token.45.velocityCode';
update app_translation set msgValue='${contractor.country.csrCountry}' where msgKey='Token.85.velocityCode';
update app_translation set msgValue='${contractor.country.csrAddress}' where msgKey='Token.67.velocityCode';
update app_translation set msgValue='${contractor.country.csrAddress2}' where msgKey='Token.68.velocityCode';
update app_translation set msgValue='${contractor.country.csrCity}' where msgKey='Token.69.velocityCode';
update app_translation set msgValue='${contractor.country.csrCountrySubdivision}' where msgKey='Token.70.velocityCode';
update app_translation set msgValue='${contractor.country.csrZip}' where msgKey='Token.71.velocityCode';
update app_translation set msgValue='${contractor.country.currency}' where msgKey='Token.72.velocityCode';
update app_translation set msgValue='${contractor.country.isrPhone}' where msgKey='Token.73.velocityCode';
update app_translation set msgValue='${contractor.country.isrFax}' where msgKey='Token.74.velocityCode';
update app_translation set msgValue='${contractor.country.isrEmail}' where msgKey='Token.75.velocityCode';
