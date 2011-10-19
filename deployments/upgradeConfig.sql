-- -----------------------------------------------------------------------------------------------
-- THIS FILE IS FOR CHANGES TO CONFIG TABLES THAT CANNOT BE APPLIED UNTIL RELEASE TIME
-- EXAMPLES:
-- -- changing app_translation msgKeys
-- REFER TO config_tables.txt FOR A FULL LIST OF CONFIG TABLES
-- SEE upgrade.sql FOR NON-CONFIG CHANGES
-- -----------------------------------------------------------------------------------------------

-- PICS-3414 removing spaces from the msgKey in these translations
update app_translation set msgKey = "ContractorEdit.error.BrochureFormat" where msgKey = "ContractorEdit.error.Brochure Format"

-- PICS-2600 Allow Audit Types to be hidden from Operators but not contractors
update audit_type set canOperatorView=0 where id=9 or id=232 or id=269 or id=270 or id =272 or id=281;

-- PICS-3450
insert into `audit_question_function_watcher` (`functionID`, `questionID`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('55','8867','fatalities','38586','38586','2011-09-27 16:19:35','2011-09-27 16:19:35');
insert into `audit_question_function_watcher` (`functionID`, `questionID`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('55','8991','majorInjuries','38586','38586','2011-09-27 16:19:37','2011-09-27 16:19:37');
insert into `audit_question_function_watcher` (`functionID`, `questionID`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('55','8869','overThreeDays','38586','38586','2011-09-27 16:19:39','2011-09-27 16:19:39');
insert into `audit_question_function_watcher` (`functionID`, `questionID`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('55','8868','underThreeDays','38586','38586','2011-09-27 16:19:42','2011-09-27 16:19:42');
insert into `audit_question_function_watcher` (`functionID`, `questionID`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('55','8872','diseases','38586','38586','2011-09-27 16:19:42','2011-09-27 16:19:42');
insert into `audit_question_function_watcher` (`functionID`, `questionID`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('55','8870','nearMisses','38586','38586','2011-09-27 16:19:44','2011-09-27 16:19:44');
insert into `audit_question_function_watcher` (`functionID`, `questionID`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('55','7691','gas','38586','38586','2011-09-27 16:19:47','2011-09-27 16:19:47');
insert into `audit_question_function_watcher` (`functionID`, `questionID`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('55','9098','employees','38586','38586','2011-09-28 15:32:37','2011-09-28 15:32:37');
insert into `audit_question_function_watcher` (`functionID`, `questionID`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('56','8867','fatalities','38586','38586','2011-10-18 10:23:47','2011-10-18 10:23:47');
insert into `audit_question_function_watcher` (`functionID`, `questionID`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('56','8991','majorInjuries','38586','38586','2011-10-18 10:23:47','2011-10-18 10:23:47');
insert into `audit_question_function_watcher` (`functionID`, `questionID`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('56','8869','overThreeDays','38586','38586','2011-10-18 10:23:47','2011-10-18 10:23:47');
insert into `audit_question_function_watcher` (`functionID`, `questionID`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('56','8868','underThreeDays','38586','38586','2011-10-18 10:23:47','2011-10-18 10:23:47');
insert into `audit_question_function_watcher` (`functionID`, `questionID`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('56','8872','diseases','38586','38586','2011-10-18 10:23:47','2011-10-18 10:23:47');
insert into `audit_question_function_watcher` (`functionID`, `questionID`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('56','8870','nearMisses','38586','38586','2011-10-18 10:23:47','2011-10-18 10:23:47');
insert into `audit_question_function_watcher` (`functionID`, `questionID`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('56','7691','gas','38586','38586','2011-10-18 10:23:47','2011-10-18 10:23:47');
insert into `audit_question_function_watcher` (`functionID`, `questionID`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('56','9099','totalHours','38586','38586','2011-10-18 10:23:47','2011-10-18 10:23:47');
insert into `audit_question_function` (`questionID`, `type`, `function`, `expression`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`, `overwrite`) values('9060','Calculation','AIR',NULL,'38586','38586','2011-09-27 15:41:20','2011-09-27 15:41:20','1');
insert into `audit_question_function` (`questionID`, `type`, `function`, `expression`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`, `overwrite`) values('9062','Calculation','AFR',NULL,'38586','38586','2011-09-27 15:42:00','2011-09-27 15:42:00','1');

