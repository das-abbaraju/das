DROP TABLE IF EXISTS country_contact;
CREATE TABLE country_contact AS
SELECT
  1 AS createdBy, NOW() AS creationDate, 1 AS updatedBy, NOW() AS updateDate, 
  isoCode AS country, 
-- CSR
  phone AS csrPhone, fax AS csrFax, picsEmail AS csrEmail, NULL AS csrAddress, NULL AS csrCity, NULL AS csrCountrySubdivision, NULL AS csrZip, 
-- ISR
  salesPhone AS isrPhone, fax AS isrFax, picsEmail AS isrEmail, NULL AS isrAddress, NULL AS isrCity, NULL AS isrCountrySubdivision, NULL AS isrZip, 
  businessUnitID
FROM ref_country
LIMIT 1000;

ALTER TABLE `country_contact`   
  ADD COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT FIRST, 
  ADD PRIMARY KEY (`id`),
  ADD  UNIQUE INDEX `Country` (`country`);

ALTER TABLE `pics_alpha1`.`country_contact`
  CHANGE `csrAddress` `csrAddress` VARCHAR(50) NULL,
  CHANGE `csrCity` `csrCity` VARCHAR(35) NULL,
  CHANGE `csrCountrySubdivision` `csrCountrySubdivision` VARCHAR(10) NULL,
  CHANGE `csrZip` `csrZip` VARCHAR(15) NULL,
  CHANGE `isrAddress` `isrAddress` VARCHAR(50) NULL,
  CHANGE `isrCity` `isrCity` VARCHAR(35) NULL,
  CHANGE `isrCountrySubdivision` `isrCountrySubdivision` VARCHAR(10) NULL,
  CHANGE `isrZip` `isrZip` VARCHAR(15) NULL;
