-- per ticket instructions. set this column to nullable instead of adding a FK
ALTER TABLE ref_country
  CHANGE `businessUnitID` `businessUnitID` INT(8) DEFAULT 1  NULL;
