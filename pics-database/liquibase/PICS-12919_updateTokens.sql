--liquibase formatted sql

--changeset kchase:2
-- update tokens
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

update app_translation set msgValue="${contractor.currentCsr.name}" where msgKey="Token.31.velocityCode";
update app_translation set msgValue="${contractor.country.csrPhone}" where msgKey="Token.32.velocityCode";
update app_translation set msgValue="${contractor.country.csrFax}" where msgKey="Token.34.velocityCode";
update app_translation set msgValue="${contractor.country.csrEmail}" where msgKey="Token.33.velocityCode";
update app_translation set msgValue="${contractor.country.csrCountry}" where msgKey="Token.85.velocityCode";
update app_translation set msgValue="${contractor.country.businessUnit.displayName}" where msgKey="Token.46.velocityCode";
update app_translation set msgValue="${contractor.country.businessUnit.addressSingleLine}" where msgKey="Token.47.velocityCode";
update app_translation set msgValue="${contractor.country.phone}" where msgKey="Token.43.velocityCode";
update app_translation set msgValue="${contractor.country.salesPhone}" where msgKey="Token.44.velocityCode";
update app_translation set msgValue="${contractor.country.picsEmail}" where msgKey="Token.45.velocityCode";
update app_translation set msgValue="${contractor.country.csrAddress}" where msgKey="Token.67.velocityCode";
update app_translation set msgValue="${contractor.country.csrAddress2}" where msgKey="Token.68.velocityCode";
update app_translation set msgValue="${contractor.country.csrCity}" where msgKey="Token.69.velocityCode";
update app_translation set msgValue="${contractor.country.csrCountrySubdivision}" where msgKey="Token.70.velocityCode";
update app_translation set msgValue="${contractor.country.csrZip}" where msgKey="Token.71.velocityCode";
update app_translation set msgValue="${contractor.country.currency}" where msgKey="Token.72.velocityCode";
update app_translation set msgValue="${contractor.country.isrPhone}" where msgKey="Token.73.velocityCode";
update app_translation set msgValue="${contractor.country.isrFax}" where msgKey="Token.74.velocityCode";
update app_translation set msgValue="${contractor.country.isrEmail}" where msgKey="Token.75.velocityCode";


