/*
replace into ref_state (isoCode, countryCode, english) select concat(isoCode,'-'), isoCode, 'N/A' from ref_country;
replace into ref_country_subdivision (isoCode, countryCode, english) select concat(isoCode,'-'), isoCode, 'N/A' from ref_country;
insert into ref_country_subdivision (isoCode, countryCode, english) values ('PR', 'PR', 'Puerto Rico');
insert into ref_country_subdivision (isoCode, countryCode, english) values ('GU', 'GU', 'Guam');
insert into ref_country_subdivision (isoCode, countryCode, english) values ('UM', 'UM', 'United States Minor Islands');
*/