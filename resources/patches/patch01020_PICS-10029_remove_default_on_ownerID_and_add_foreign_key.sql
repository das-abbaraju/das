ALTER TABLE `report` CHANGE COLUMN `ownerID` `ownerID` INT(11) NOT NULL  ,
  ADD CONSTRAINT `FK_report_owner`
  FOREIGN KEY (`ownerID` )
  REFERENCES `users` (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION
, ADD INDEX `FK_report_owner` (`ownerID` ASC) ;