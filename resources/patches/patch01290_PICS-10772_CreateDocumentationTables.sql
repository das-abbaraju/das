CREATE TABLE IF NOT EXISTS `operator_competency_course` (
  `id`           INT(11)     NOT NULL AUTO_INCREMENT,
  `competencyID` INT(11)     NOT NULL,
  `courseType`   VARCHAR(50) NOT NULL,
  `createdBy`    INT(11)     NOT NULL,
  `updatedBy`    INT(11)     NULL,
  `creationDate` TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateDate`   TIMESTAMP   NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `competencyID_courseType` (`competencyID`, `courseType`),
  CONSTRAINT `fk_operator_competency` FOREIGN KEY (`competencyID`) REFERENCES `operator_competency` (`id`)
    ON UPDATE NO ACTION
    ON DELETE CASCADE
)
  COLLATE ='utf8_general_ci'
  ENGINE =InnoDB;

CREATE TABLE IF NOT EXISTS `operator_competency_employee_file` (
  `id`           INT(11)     NOT NULL AUTO_INCREMENT,
  `competencyID` INT(11)     NOT NULL,
  `employeeID`   INT(11)     NOT NULL,
  `fileName`     VARCHAR(50) NOT NULL,
  `fileType`     VARCHAR(10) NOT NULL,
  `fileImage`    BLOB        NOT NULL,
  `expiration`   DATE        NOT NULL,
  `createdBy`    INT(11)     NOT NULL,
  `updatedBy`    INT(11)     NULL DEFAULT NULL,
  `creationDate` TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateDate`   TIMESTAMP   NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_file_competency` (`competencyID`),
  INDEX `fk_file_employee` (`employeeID`),
  UNIQUE INDEX `competencyID_employeeID_fileName` (`competencyID`, `employeeID`, `fileName`),
  CONSTRAINT `fk_file_competency` FOREIGN KEY (`competencyID`) REFERENCES `operator_competency` (`id`)
    ON UPDATE NO ACTION
    ON DELETE CASCADE,
  CONSTRAINT `fk_file_employee` FOREIGN KEY (`employeeID`) REFERENCES `employee` (`id`)
    ON UPDATE NO ACTION
    ON DELETE CASCADE
)
  COLLATE ='utf8_general_ci'
  ENGINE =InnoDB;
