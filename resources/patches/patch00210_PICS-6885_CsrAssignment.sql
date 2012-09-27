drop table if exists user_language;
drop table if exists ref_language;
drop table if exists csr_info;

CREATE TABLE `user_language` (
        `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
        `userID` INT NOT NULL,
        `locale` VARCHAR(5) DEFAULT 'en' NOT NULL,
        PRIMARY KEY(`id`),
        FOREIGN KEY (`userID`) REFERENCES users(`id`)        
) ENGINE=INNODB DEFAULT CHARSET=utf8;

alter table `users` add column `assignmentCapacity` TINYINT UNSIGNED NOT NULL DEFAULT 100;
alter table `users` add column `shiftStartHour` TINYINT UNSIGNED DEFAULT 9;
alter table `users` add column `shiftEndHour` TINYINT UNSIGNED DEFAULT 17;
alter table `users` add column `workdays` char(7) DEFAULT 'xMTWTFx';
alter table `users` add column `dontReassign` TINYINT NOT NULL DEFAULT 0;

alter table contractor_info drop column shiftStart;
alter table contractor_info drop column shiftEnd;
alter table contractor_info drop column languageID;