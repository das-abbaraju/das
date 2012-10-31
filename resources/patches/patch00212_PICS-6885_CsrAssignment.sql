ALTER IGNORE TABLE `users` DROP `dontReassign`;
ALTER IGNORE TABLE `users` DROP assignmentCapacity;
ALTER IGNORE TABLE `users` add column `assignmentCapacity` TINYINT UNSIGNED NOT NULL DEFAULT 0;
ALTER IGNORE TABLE `contractor_info` DROP `dontReassign`;
ALTER IGNORE TABLE `contractor_info` add column `dontReassign` TINYINT NOT NULL DEFAULT 0;