ALTER TABLE contractor_info
  CHANGE `requestedByID` `requestedByID` INT(11) DEFAULT 0  NULL;

ALTER TABLE contractor_info
  ADD CONSTRAINT `fk2_contractor_info` FOREIGN KEY (`requestedByID`) REFERENCES accounts(`id`) ON UPDATE RESTRICT ON DELETE RESTRICT;
