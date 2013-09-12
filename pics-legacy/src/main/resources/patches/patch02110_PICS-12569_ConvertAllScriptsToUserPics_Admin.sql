-- VIEWS
ALTER DEFINER =`pics_admin`@`%` VIEW v_audits AS SELECT `t`.`id` AS `auditID`,`translate`('AuditType',`t`.`id`) AS `auditName`,`t`.`classType` AS `auditClass`,`c`.`id` AS `categoryID`,`translate`('AuditCategory',`c`.`id`) AS `category`,`q`.`id` AS `questionID`,`translate`('AuditQuestion',`q`.`id`) AS `question` FROM ((`audit_type` `t` JOIN `audit_category` `c` ON((`c`.`auditTypeID` = `t`.`id`))) JOIN `audit_question` `q` ON((`q`.`categoryID` = `c`.`id`))) ORDER BY `t`.`classType`,`t`.`displayOrder`,`c`.`number`,`q`.`number`;
ALTER DEFINER =`pics_admin`@`%` VIEW vwcurrentinvoicedate AS SELECT `accounts`.`id` AS `AccountID`,`accounts`.`type` AS `AccountType`,`accounts`.`name` AS `AccountName`,`accounts`.`status` AS `AccountStatus`,`accounts`.`currencyCode` AS `CurrencyCode`,`contractor_info`.`paymentExpires` AS `RenewalDate`,MAX(`invoice`.`creationDate`) AS `InvoiceDate` FROM ((((`accounts` JOIN `contractor_info` ON((`contractor_info`.`id` = `accounts`.`id`))) JOIN `invoice` ON((`accounts`.`id` = `invoice`.`accountID`))) JOIN `invoice_item` ON((`invoice_item`.`invoiceID` = `invoice`.`id`))) JOIN `invoice_fee` ON((`invoice_fee`.`id` = `invoice_item`.`feeID`))) GROUP BY `accounts`.`id`,`accounts`.`type`,`accounts`.`name`,`accounts`.`status`,`accounts`.`currencyCode`,`contractor_info`.`paymentExpires`;
ALTER DEFINER =`pics_admin`@`%` VIEW vwemployee_competency_jobrole AS SELECT `employee_competency`.`employeeID` AS `employeeID`,`employee_competency`.`competencyID` AS `competencyID`,`employee_competency`.`skilled` AS `skilled`,`employee_role`.`jobRoleID` AS `jobRoleID`,`job_role`.`accountID` AS `accountID`,`job_role`.`name` AS `NAME`,`job_role`.`active` AS `active` FROM (((`employee_competency` JOIN `employee_role` ON((`employee_role`.`employeeID` = `employee_competency`.`employeeID`))) JOIN `job_competency` ON(((`job_competency`.`competencyID` = `employee_competency`.`competencyID`) AND (`job_competency`.`jobRoleID` = `employee_role`.`jobRoleID`)))) JOIN `job_role` ON(((`job_role`.`id` = `job_competency`.`jobRoleID`) AND (`job_role`.`active` = 1))));
ALTER DEFINER =`pics_admin`@`%` VIEW vwemployee_jobrole_jobcompetency AS SELECT `employee_role`.`employeeID` AS `employeeID`,`employee_role`.`jobRoleID` AS `jobRoleID`,`job_role`.`name` AS `NAME`,`job_role`.`accountID` AS `accountID`,`job_role`.`active` AS `active`,`job_competency`.`competencyID` AS `competencyID` FROM ((`employee_role` JOIN `job_role` ON((`job_role`.`id` = `employee_role`.`jobRoleID`))) LEFT JOIN `job_competency` ON((`job_competency`.`jobRoleID` = `employee_role`.`jobRoleID`)));
ALTER DEFINER =`pics_admin`@`%` VIEW vwhsecompetenciesrequiredcount AS SELECT `vwemployee_jobrole_jobcompetency`.`employeeID` AS `employeeID`,`vwemployee_jobrole_jobcompetency`.`NAME` AS `NAME`,COUNT(`vwemployee_jobrole_jobcompetency`.`competencyID`) AS `counts` FROM `vwemployee_jobrole_jobcompetency` GROUP BY `vwemployee_jobrole_jobcompetency`.`employeeID`;
ALTER DEFINER =`pics_admin`@`%` VIEW vwhsecompetenciesskilledcount AS SELECT `vwemployee_competency_jobrole`.`employeeID` AS `employeeID`,COUNT(`vwemployee_competency_jobrole`.`competencyID`) AS `counts` FROM `vwemployee_competency_jobrole` WHERE (`vwemployee_competency_jobrole`.`active` = 1) GROUP BY `vwemployee_competency_jobrole`.`employeeID`;

-- STORED PROCEDURES
UPDATE `mysql`.`proc` p SET DEFINER = 'pics_admin@%';

-- FUNCTIONS
DELIMITER $$
DROP FUNCTION IF EXISTS `splitString`$$
CREATE DEFINER=`pics_admin`@`%` FUNCTION `splitString` (
	X 	TEXT
,	delim 	VARCHAR(12)
,	pos 	INT
) RETURNS TEXT CHARSET utf8
DETERMINISTIC
RETURN 	REPLACE(SUBSTRING(SUBSTRING_INDEX(X, delim, pos)
,	CHAR_LENGTH(SUBSTRING_INDEX(X, delim, pos -1)) + 1)
,	delim, '')$$
DELIMITER ;

DELIMITER $$
DROP FUNCTION IF EXISTS `substrCount`$$
CREATE DEFINER=`pics_admin`@`%` FUNCTION `substrCount`(s VARCHAR(255), ss VARCHAR(255)) RETURNS TINYINT(3) UNSIGNED
    READS SQL DATA
BEGIN
DECLARE COUNT TINYINT(3) UNSIGNED;
DECLARE OFFSET TINYINT(3) UNSIGNED;
DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET s = NULL;
SET COUNT = 0;
SET OFFSET = 1;
REPEAT
IF NOT ISNULL(s) AND OFFSET > 0 THEN
SET OFFSET = LOCATE(ss, s, OFFSET);
IF OFFSET > 0 THEN
SET COUNT = COUNT + 1;
SET OFFSET = OFFSET + 1;
END IF;
END IF;
UNTIL ISNULL(s) OR OFFSET = 0 END REPEAT;
RETURN COUNT;
END$$
DELIMITER ;

DELIMITER $$
DROP FUNCTION IF EXISTS `translate`$$
CREATE DEFINER=`pics_admin`@`%` FUNCTION `translate`(entity VARCHAR(100), id INT) RETURNS VARCHAR(50) CHARSET utf8
    READS SQL DATA
    DETERMINISTIC
BEGIN
	DECLARE translated VARCHAR(50) CHARACTER SET utf8;
	SELECT LEFT(msgValue,50) INTO translated FROM app_translation WHERE locale = 'en' AND msgKey = CONCAT(entity,'.',id,'.name') LIMIT 1;
	RETURN translated;
END$$
DELIMITER ;

-- TRIGGERS
DELIMITER $$
DROP TRIGGER /*!50032 IF EXISTS */ `generalcontractors_two_ids_before_insert`$$
CREATE
    /*!50017 DEFINER = 'pics_admin'@'%' */
    TRIGGER `generalcontractors_two_ids_before_insert` BEFORE INSERT ON `generalcontractors`
    FOR EACH ROW BEGIN
	SET @cnt = 0;
	IF new.gcID IS NULL THEN
	  SET @cnt = @cnt + 1;
	END IF;
	IF new.subID IS NULL THEN
	  SET @cnt = @cnt + 1;
	END IF;
	IF new.genID IS NULL THEN
	  SET @cnt = @cnt + 1;
	END IF;
	IF @cnt > 1 THEN
	  SIGNAL SQLSTATE '45000'
		SET MESSAGE_TEXT = 'A contractor_operator object must have at least 2 ids between subID, genID and gcID not equal to NULL';
	END IF;
    END;
$$
DELIMITER ;

DELIMITER $$
DROP TRIGGER /*!50032 IF EXISTS */ `generalcontractors_two_ids_before_update`$$
CREATE
    /*!50017 DEFINER = 'pics_admin'@'%' */
    TRIGGER `generalcontractors_two_ids_before_update` BEFORE UPDATE ON `generalcontractors`
    FOR EACH ROW BEGIN
	SET @cnt = 0;
	IF new.gcID IS NULL THEN
	  SET @cnt = @cnt + 1;
	END IF;
	IF new.subID IS NULL THEN
	  SET @cnt = @cnt + 1;
	END IF;
	IF new.genID IS NULL THEN
	  SET @cnt = @cnt + 1;
	END IF;
	IF @cnt > 1 THEN
	  SIGNAL SQLSTATE '45000'
		SET MESSAGE_TEXT = 'A contractor_operator object must have at least 2 ids between subID, genID and gcID not equal to NULL';
	END IF;
    END;
$$
DELIMITER ;

DELIMITER $$
DROP TRIGGER /*!50032 IF EXISTS */ `pqfdata_hist_trig_after_insert`$$
CREATE
    /*!50017 DEFINER = 'pics_admin'@'%' */
    TRIGGER `pqfdata_hist_trig_after_insert` AFTER INSERT ON `pqfdata`
    FOR EACH ROW BEGIN
	INSERT INTO pqfdata_hist (histID, auditID, questionID, answer, COMMENT, dateVerified, auditorID, wasChanged, histCreatedBy, histCreationDate, histUpdatedBy, histUpdateDate,
					createdBy, creationDate, updatedBy, updateDate)
	SELECT 	new.id, new.auditID, new.questionID, new.answer, new.comment, new.dateVerified, new.auditorID, new.wasChanged, new.createdBy, new.creationDate,
		new.updatedBy, new.updateDate, new.createdBy, NOW(), new.updatedBy, NOW();
    END;
$$
DELIMITER ;

DELIMITER $$
DROP TRIGGER /*!50032 IF EXISTS */ `pqfdata_hist_trig_after_update`$$
CREATE
    /*!50017 DEFINER = 'pics_admin'@'%' */
    TRIGGER `pqfdata_hist_trig_after_update` AFTER UPDATE ON `pqfdata`
    FOR EACH ROW BEGIN
	INSERT INTO pqfdata_hist (histID, auditID, questionID, answer, COMMENT, dateVerified, auditorID, wasChanged, histCreatedBy, histCreationDate, histUpdatedBy, histUpdateDate,
					createdBy, creationDate, updatedBy, updateDate)
	SELECT 	new.id, new.auditID, new.questionID, new.answer, new.comment, new.dateVerified, new.auditorID, new.wasChanged, new.createdBy, new.creationDate,
		new.updatedBy, new.updateDate, new.createdBy, NOW(), new.updatedBy, NOW();
    END;
$$
DELIMITER ;

