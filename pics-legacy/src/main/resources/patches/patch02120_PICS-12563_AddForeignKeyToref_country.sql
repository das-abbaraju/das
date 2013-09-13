-- per ticket instructions. set this column to nullable instead of adding a FK
ALTER TABLE ref_country
  CHANGE `businessUnitID` `businessUnitID` INT(8) DEFAULT 1  NULL;
ALTER TABLE `pics_alpha1`.`ref_country`
  ADD CONSTRAINT `fk1_ref_country` FOREIGN KEY (`businessUnitID`) REFERENCES `pics_alpha1`.`ref_sap_business_unit`(`id`) ON DELETE SET NULL;
