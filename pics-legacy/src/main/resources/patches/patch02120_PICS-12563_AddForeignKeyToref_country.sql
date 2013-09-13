-- per ticket instructions. set this column to nullable instead of adding a FK
ALTER TABLE ref_country
  CHANGE `businessUnitID` `businessUnitID` INT(8) DEFAULT 1  NULL
 ;
ALTER TABLE ref_country
  ADD CONSTRAINT `fk1_ref_country` FOREIGN KEY (`businessUnitID`)
  REFERENCES ref_sap_business_unit(`id`) ON UPDATE RESTRICT ON DELETE RESTRICT
 ;
