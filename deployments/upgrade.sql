-- Copied all these tables from LIVE to Alpha2 (5/27)
app_translation audit_category audit_category_rule audit_question audit_type audit_type_rule pqfoptions workflow workflow_step
-- Need to copy this all back to Live when we release
ref_trade ref_trade_alt

-- Move over the ref trades somehow
SET foreign_key_checks = 0;
INSERT INTO pics_alpha2.ref_trade
SELECT * FROM pics_alpha1.ref_trade;

INSERT INTO pics_alpha2.ref_trade_alt
SELECT * FROM pics_alpha1.ref_trade_alt;
SET foreign_key_checks = 1;

-- PICS-1639
update invoice_fee invf set invf.minFacilities = -1, invf.maxFacilities = -1, invf.feeClass = 'Deprecated', invf.visible = 0, invf.fee = concat('Old ',invf.fee) where invf.id in (1,2,4,5,6,7,8,9,10,11,50,51,52,54,55,100,101,104,105);
update invoice_fee invf set invf.feeClass = 'Misc' where invf.feeClass = 'Other';
update invoice_fee invf set invf.feeClass = 'Activation' where invf.id in (1,104);
update invoice_fee invf set invf.feeClass = 'DocuGUARD' where invf.id = 4;
update invoice_fee invf set invf.feeClass = 'AuditGUARD' where invf.id in (5,6,7,8,9,10,11,105);
update invoice_fee invf set invf.feeClass = 'BidOnly' where invf.id = 100;
update invoice_fee invf set invf.feeClass = 'GST', invf.minFacilities = 0, invf.maxFacilities = 10000 where invf.id = 200;

insert into invoice_fee 
	(id, 
	fee, 
	defaultAmount, 
	visible, 
	feeClass, 
	minFacilities,
	maxFacilities,
	qbFullName, 
	createdBy, 
	updatedBy, 
	creationDate, 
	updateDate, 
	displayOrder
	)
values 	(297, 'List Only 0 Operators', 0.00, 1, 'ListOnly', 0, 0, 'LTVEN0', 20952, 20952, now(), now(), 1),
	(298, 'List Only', 25.00, 1, 'ListOnly', 1, 10000, 'LTVEN25', 20952, 20952, now(), now(), 1),
	(299, 'Bid Only 0 Operators', 0.00, 1, 'BidOnly', 0, 0, 'BTVEN0', 20952, 20952, now(), now(), 1),
	(300, 'Bid Only', 25.00, 1, 'BidOnly', 1, 10000, 'BTVEN25', 20952, 20952, now(), now(), 1),
	(301, 'DocuGUARD for 0 Operators', 0.00, 1, 'DocuGUARD', 0, 0, 'DGVEN0', 20952, 20952, now(), now(), 2),
	(302, 'DocuGUARD for 1 Operator', 99.00, 1, 'DocuGUARD', 1, 1, 'DGVEN1', 20952, 20952, now(), now(), 3),
	(303, 'DocuGUARD for 2-4 Operators', 99.00, 1, 'DocuGUARD', 2, 4, 'DGVEN2', 20952, 20952, now(), now(), 4),
	(304, 'DocuGUARD for 5-8 Operators', 99.00, 1, 'DocuGUARD', 5, 8, 'DGVEN5', 20952, 20952, now(), now(), 5),
	(305, 'DocuGUARD for 9-12 Operators', 99.00, 1, 'DocuGUARD', 9, 12, 'DGVEN9', 20952, 20952, now(), now(), 6),
	(306, 'DocuGUARD for 13-19 Operators', 99.00, 1, 'DocuGUARD', 13, 19, 'DGVEN13', 20952, 20952, now(), now(), 7),
	(307, 'DocuGUARD for 20-49 Operators', 99.00, 1, 'DocuGUARD', 20, 49, 'DGVEN20', 20952, 20952, now(), now(), 8),
	(308, 'DocuGUARD for 50+ Operators', 99.00, 1, 'DocuGUARD', 50, 10000, 'DGVEN50', 20952, 20952, now(), now(), 9),
	(309, 'InsureGUARD for 0 Operators', 0.00, 1, 'InsureGUARD', 0, 0, 'IGVEN0', 20952, 20952, now(), now(), 10),
	(310, 'InsureGUARD for 1 Operator', 0.00, 1, 'InsureGUARD', 1, 1, 'IGVEN1', 20952, 20952, now(), now(), 11),
	(311, 'InsureGUARD for 2-4 Operators', 0.00, 1, 'InsureGUARD', 2, 4, 'IGVEN2', 20952, 20952, now(), now(), 12),
	(312, 'InsureGUARD for 5-8 Operators', 0.00, 1, 'InsureGUARD', 5, 8, 'IGVEN5', 20952, 20952, now(), now(), 13),
	(313, 'InsureGUARD for 9-12 Operators', 0.00, 1, 'InsureGUARD', 9, 12, 'IGVEN9', 20952, 20952, now(), now(), 14),
	(314, 'InsureGUARD for 13-19 Operators', 0.00, 1, 'InsureGUARD', 13, 19, 'IGVEN13', 20952, 20952, now(), now(), 15),
	(315, 'InsureGUARD for 20-49 Operators', 0.00, 1, 'InsureGUARD', 20, 49, 'IGVEN20', 20952, 20952, now(), now(), 16),
	(316, 'InsureGUARD for 50+ Operators', 0.00, 1, 'InsureGUARD', 50, 10000, 'IGVEN50', 20952, 20952, now(), now(), 17),
	(317, 'AuditGUARD for 0 Operators', 0.00, 1, 'AuditGUARD', 0, 0, 'AGVEN0', 20952, 20952, now(), now(), 18),
	(318, 'AuditGUARD for 1 Operator', 399.00, 1, 'AuditGUARD', 1, 1, 'AGVEN1', 20952, 20952, now(), now(), 19),
	(319, 'AuditGUARD for 2-4 Operators', 799.00, 1, 'AuditGUARD', 2, 4, 'AGVEN2', 20952, 20952, now(), now(), 20),
	(320, 'AuditGUARD for 5-8 Operators', 1199.00, 1, 'AuditGUARD', 5, 8, 'AGVEN5', 20952, 20952, now(), now(), 21),
	(321, 'AuditGUARD for 9-12 Operators', 1499.00, 1, 'AuditGUARD', 9, 12, 'AGVEN9', 20952, 20952, now(), now(), 22),
	(322, 'AuditGUARD for 13-19 Operators', 1899.00, 1, 'AuditGUARD', 13, 19, 'AGVEN13', 20952, 20952, now(), now(), 23),
	(323, 'AuditGUARD for 20-49 Operators', 2899.00, 1, 'AuditGUARD', 20, 49, 'AGVEN20', 20952, 20952, now(), now(), 24),
	(324, 'AuditGUARD for 50+ Operators', 3899.00, 1, 'AuditGUARD', 50, 10000, 'AGVEN50', 20952, 20952, now(), now(), 25),
	(325, 'EmployeeGUARD for 0 Operators', 0.00, 1, 'EmployeeGUARD', 0, 0, 'EGVEN0', 20952, 20952, now(), now(), 26),
	(326, 'EmployeeGUARD for 1 Operator', 99.00, 1, 'EmployeeGUARD', 1, 1, 'EGVEN1', 20952, 20952, now(), now(), 27),
	(327, 'EmployeeGUARD for 2-4 Operators', 199.00, 1, 'EmployeeGUARD', 2, 4, 'EGVEN2', 20952, 20952, now(), now(), 28),
	(328, 'EmployeeGUARD for 5-8 Operators', 299.00, 1, 'EmployeeGUARD', 5, 8, 'EGVEN5', 20952, 20952, now(), now(), 29),
	(329, 'EmployeeGUARD for 9-12 Operators', 399.00, 1, 'EmployeeGUARD', 9, 12, 'EGVEN9', 20952, 20952, now(), now(), 30),
	(330, 'EmployeeGUARD for 13-19 Operators', 599.00, 1, 'EmployeeGUARD', 13, 19, 'EGVEN13', 20952, 20952, now(), now(), 31),
	(331, 'EmployeeGUARD for 20-49 Operators', 799.00, 1, 'EmployeeGUARD', 20, 49, 'EGVEN20', 20952, 20952, now(), now(), 32),
	(332, 'EmployeeGUARD for 50+ Operators', 999.00, 1, 'EmployeeGUARD', 50, 10000, 'EGVEN50', 20952, 20952, now(), now(), 33),
	(333, 'Activation Fee', 199.00, 1, 'Activation', 1, 10000, 'FVEN10', 20952, 20952, now(), now(), 34),
	(334, 'Reactivation Fee', 199.00, 1, 'Reactivation', 0, 10000, 'FVEN11', 20952, 20952, now(), now(), 35),
	(335, 'Audit Cancellation/Rescheduling Fee', 199.00, 1, 'ReschedulingFee', 0, 10000, 'FVEN12', 20952, 20952, now(), now(), 36),
	(336, 'Late Fee', 0.00, 1, 'LateFee', 0, 10000, 'FVEN13', 20952, 20952, now(), now(), 37),
	(337, 'Safety/Quality Manual Scanning Fee', 99.00, 1, 'ScanningFee', 0, 10000, 'FVEN14', 20952, 20952, now(), now(), 38),
	(338, 'Webcam Replacement Fee', 65.00, 1, 'WebcamFee', 0, 10000, 'FVEN15', 20952, 20952, now(), now(), 39),
	(339, 'Audit Expedite Fee', 99.00, 1, 'ExpediteFee', 0, 10000, 'FVEN16', 20952, 20952, now(), now(), 40),
	(340, 'Data Import Fee', 199.00, 1, 'ImportFee', 0, 10000, 'FVEN17', 20952, 20952, now(), now(), 41),
	(341, 'Activation Fee (Discounted from standard rate of $199)', 99.00, 1, 'Activation', 0, 0, 'FVEN18', 20952, 20952, now(), now(), 42);

-- AuditGUARD
insert into contractor_fee 
	(id, 
	conID, 
	feeClass, 
	newLevel, 
	currentLevel, 
	createdBy, 
	updatedBy, 
	creationDate, 
	updateDate
	)
select null, c.id, 'AuditGUARD', 
  case when c.newMembershipLevelID in (3, 100, 4) or c.newMembershipLevelID is null then 317
	when c.newMembershipLevelID in (5, 105) then 318
	when c.newMembershipLevelID = 6 then 319
	when c.newMembershipLevelID = 7 then 320
	when c.newMembershipLevelID = 8 then 321
	when c.newMembershipLevelID = 9 then 322
	when c.newMembershipLevelID = 10 then 323
	when c.newMembershipLevelID = 11 then 324 end,
  case when c.membershipLevelID in (3, 100, 4) or c.membershipLevelID is null then 317
	when c.membershipLevelID = 5 or c.membershipLevelID = 105 then 318
	when c.membershipLevelID = 6 then 319
	when c.membershipLevelID = 7 then 320
	when c.membershipLevelID = 8 then 321
	when c.membershipLevelID = 9 then 322
	when c.membershipLevelID = 10 then 323
	when c.membershipLevelID = 11 then 324 end,
  20952, 20952, now(), now() from contractor_info c;

-- InsureGUARD (Inserting free levels and letting Cron calculate proper values)
insert into contractor_fee 
	(id, 
	conID, 
	feeClass, 
	newLevel, 
	currentLevel, 
	createdBy, 
	updatedBy, 
	creationDate, 
	updateDate
	)
select null, c.id, 'InsureGUARD', 309, 309, 20952, 20952, now(), now() from contractor_info c;

-- EmployeeGUARD (Inserting free levels and letting Cron calculate proper values)
insert into contractor_fee 
	(id, 
	conID, 
	feeClass, 
	newLevel, 
	currentLevel, 
	createdBy, 
	updatedBy, 
	creationDate, 
	updateDate
	)
select null, c.id, 'EmployeeGUARD', 325, 325, 20952, 20952, now(), now() from contractor_info c;

-- DocuGUARD
insert into contractor_fee 
	(id, 
	conID, 
	feeClass, 
	newLevel, 
	currentLevel, 
	createdBy, 
	updatedBy, 
	creationDate, 
	updateDate
	)
select null, c.id, 'DocuGUARD', 
  case when c.newMembershipLevelID = 3 or c.newMembershipLevelID = 100 or c.newMembershipLevelID is null then 301
	when (c.newMembershipLevelID = 4 and c.payingFacilities in (0,1)) or (c.newMembershipLevelID in (5,105)) then 302
	when (c.newMembershipLevelID = 4 and c.payingFacilities >= 2 and c.payingFacilities <= 4) or (c.newMembershipLevelID = 6) then 303
	when (c.newMembershipLevelID = 4 and c.payingFacilities >= 5 and c.payingFacilities <= 8) or (c.newMembershipLevelID = 7) then 304
	when (c.newMembershipLevelID = 4 and c.payingFacilities >= 9 and c.payingFacilities <= 12) or (c.newMembershipLevelID = 8) then 305
	when (c.newMembershipLevelID = 4 and c.payingFacilities >= 13 and c.payingFacilities <= 19) or (c.newMembershipLevelID = 9) then 306
	when (c.newMembershipLevelID = 4 and c.payingFacilities >= 20 and c.payingFacilities <= 49) OR (c.newMembershipLevelID = 10) then 307
	when (c.newMembershipLevelID = 4 and c.payingFacilities >= 50) or (c.newMembershipLevelID = 11) then 308 end,
  case when c.membershipLevelID = 3 or c.membershipLevelID = 100 or c.membershipLevelID is null then 301
	when (c.membershipLevelID = 4 and c.payingFacilities in (0,1)) or (c.membershipLevelID in (5,105)) then 302
	when (c.membershipLevelID = 4 and c.payingFacilities >= 2 and c.payingFacilities <= 4) or (c.membershipLevelID = 6) then 303
	when (c.membershipLevelID = 4 and c.payingFacilities >= 5 and c.payingFacilities <= 8) or (c.membershipLevelID = 7) then 304
	when (c.membershipLevelID = 4 and c.payingFacilities >= 9 and c.payingFacilities <= 12) or (c.membershipLevelID = 8) then 305
	when (c.membershipLevelID = 4 and c.payingFacilities >= 13 and c.payingFacilities <= 19) or (c.membershipLevelID = 9) then 306
	when (c.membershipLevelID = 4 and c.payingFacilities >= 20 and c.payingFacilities <= 49) OR (c.membershipLevelID = 10) then 307
	when (c.membershipLevelID = 4 and c.payingFacilities >= 50) or (c.membershipLevelID = 11) then 308 end,
  20952, 20952, now(), now() from contractor_info c;
  
-- ListOnly
insert into contractor_fee 
	(id, 
	conID, 
	feeClass, 
	newLevel, 
	currentLevel, 
	createdBy, 
	updatedBy, 
	creationDate, 
	updateDate
	)
select null, c.id, 'ListOnly', 297, 297,
  20952, 20952, now(), now() from contractor_info c;
--

-- BidOnly
insert into contractor_fee 
	(id, 
	conID, 
	feeClass, 
	newLevel, 
	currentLevel, 
	createdBy, 
	updatedBy, 
	creationDate, 
	updateDate
	)
select null, c.id, 'BidOnly', 
  case when c.newMembershipLevelID = 100 then 300 else 299 end,
  case when c.membershipLevelID = 100 then 300 else 299 end,
  20952, 20952, now(), now() from contractor_info c;
--
--

-- PICS-2492
update accounts a join contractor_info c on a.id = c.id set c.accountLevel = 'BidOnly' where a.acceptsBids = 1 and a.type = 'Contractor';
update accounts a join contractor_info c on a.id = c.id set c.accountLevel = 'Full' where a.acceptsBids = 0 and a.type = 'Contractor';
--
  
INSERT INTO contractor_trade(conID, tradeID, createdBy, updatedBy, creationDate, updateDate, selfPerformed,
manufacture, activityPercent)
SELECT ca.conID,
tm.tradeID,
MIN(pd.createdBy),
MIN(pd.updatedBy),
MAX(pd.creationDate),
MAX(pd.updateDate),
IF(LOCATE('C', GROUP_CONCAT(pd.answer)) > 0, 1, 0) AS selfPerformed,
IF(LOCATE('Y', GROUP_CONCAT(tm.product)) > 0, 1, 0) manufacture,
5 AS activityPercent
FROM pqfdata pd
JOIN audit_question aq ON aq.id = pd.questionID
JOIN contractor_audit ca ON ca.id = pd.auditID
JOIN accounts a ON a.id = ca.conID AND a.status IN ('Active', 'Pending')
JOIN pics_temp.tax_mapping tm ON tm.questionID = aq.id
WHERE aq.categoryID = 422 AND length(trim(pd.answer)) > 0
GROUP BY ca.conID, tm.tradeID;

-- PICS-2324
CREATE TABLE temp_con_trades AS 
SELECT DISTINCT ct2.id contractor_trade_id 
FROM contractor_trade ct1 
JOIN ref_trade rt1 ON rt1.id = ct1.tradeID 
JOIN contractor_trade ct2 ON ct1.conID = ct2.conID 
JOIN ref_trade rt2 ON rt2.id = ct2.tradeID 
WHERE rt1.indexStart < rt2.indexStart AND rt2.indexEnd < rt1.indexEnd;

DELETE t1 FROM contractor_trade t1
join temp_con_trades t2 ON t1.id = t2.contractor_trade_id;

DROP TABLE temp_con_trades;

update contractor_info set safetyRisk = riskLevel, productRisk = riskLevel;


-- Convert rules risk level to the Enum value rather than the ordinal
update audit_type_rule
set safetyRisk = case risk when null then null when 0 then 'None' when 1 then 'Low' when 2 then 'Med' when 3 then 'High' end;

update audit_category_rule
set safetyRisk = case risk when null then null when 0 then 'None' when 1 then 'Low' when 2 then 'Med' when 3 then 'High' end;

alter table audit_type_rule drop column `risk`;
alter table audit_category_rule drop column `risk`;

-- PICS-2289
update pqfdata set answer = '1' where answer = 'Sole Owner' and questionID = 63;
update pqfdata set answer = '2' where answer = 'Ltd' and questionID = 63;
update pqfdata set answer = '3' where answer = 'Plc' and questionID = 63;
update pqfdata set answer = '4' where answer = 'Ultd' and questionID = 63;
update pqfdata set answer = '5' where answer = 'AG' and questionID = 63;
update pqfdata set answer = '6' where answer = 'GmbH' and questionID = 63;
update pqfdata set answer = '7' where answer = 'Partnership' and questionID = 63;
update pqfdata set answer = '8' where answer = 'Other' and questionID = 63;
update pqfdata set answer = '9' where answer = 'ULC' and questionID = 63;
update pqfdata set answer = '10' where answer = 'Corporation' and questionID = 63;
update pqfdata set answer = '11' where answer = 'LLP (Limited Liability Partnership)' and questionID = 63;
update pqfdata set answer = '12' where answer = 'LLC (Limited Liability Corporation)' and questionID = 63;
update pqfdata set answer = '470' where answer = 'Interstate' and questionID = 124;
update pqfdata set answer = '471' where answer = 'Intrastate' and questionID = 124;
update pqfdata set answer = '472' where answer = 'Monopolistic State Rate' and questionID = 124;
update pqfdata set answer = '473' where answer = 'Dual Rate' and questionID = 124;
update pqfdata set answer = '470' where answer = 'Interstate' and questionID = 125;
update pqfdata set answer = '471' where answer = 'Intrastate' and questionID = 125;
update pqfdata set answer = '472' where answer = 'Monopolistic State Rate' and questionID = 125;
update pqfdata set answer = '473' where answer = 'Dual Rate' and questionID = 125;
update pqfdata set answer = 'YesWithOffice' where answer = 'Yes with Office';
update pqfdata set answer = '467' where answer = 'First' and questionID = 2244;
update pqfdata set answer = '468' where answer = 'Second' and questionID = 2244;
update pqfdata set answer = '469' where answer = 'Third (invisible)' and questionID = 2244;
update pqfdata set answer = '53' where answer = 'Accepted' and questionID = 3536;
update pqfdata set answer = '54' where answer = 'Conditionally Accepted' and questionID = 3536;
update pqfdata set answer = '55' where answer = 'Not Accepted' and questionID = 3536;
update pqfdata set answer = '53' where answer = 'Accepted' and questionID = 3844;
update pqfdata set answer = '54' where answer = 'Conditionally Accepted' and questionID = 3844;
update pqfdata set answer = '55' where answer = 'Not Accepted' and questionID = 3844;
update pqfdata set answer = '56' where answer = '< 100' and questionID = 4127;
update pqfdata set answer = '60' where answer = 'Abu Dhabi' and questionID = 4166;
update pqfdata set answer = '61' where answer = 'Dubai' and questionID = 4166;
update pqfdata set answer = '62' where answer = 'Sharjah' and questionID = 4166;
update pqfdata set answer = '63' where answer = 'Ajman' and questionID = 4166;
update pqfdata set answer = '64' where answer = 'Umm Al-Quwain' and questionID = 4166;
update pqfdata set answer = '65' where answer = 'Ras Al-Khaimah' and questionID = 4166;
update pqfdata set answer = '66' where answer = 'Fujairah' and questionID = 4166;
update pqfdata set answer = '67' where answer = 'Arab Gulf Cooperation Country (AGCC)' and questionID = 4166;
update pqfdata set answer = '68' where answer = 'Other Country' and questionID = 4166;
update pqfdata set answer = 'NA' where answer = 'N/A';
update pqfdata set answer = '477' where answer = 'ASME' and questionID = 7000;
update pqfdata set answer = '478' where answer = 'API' and questionID = 7000;
update pqfdata set answer = '479' where answer = 'API Monogram' and questionID = 7000;
update pqfdata set answer = '480' where answer = 'Others' and questionID = 7000;
update pqfdata set answer = '484' where answer = 'Heat Treatment' and questionID = 7061;
update pqfdata set answer = '485' where answer = 'Painting' and questionID = 7061;
update pqfdata set answer = '486' where answer = 'Impact tester' and questionID = 7061;
update pqfdata set answer = '487' where answer = 'Hardness tester' and questionID = 7061;
update pqfdata set answer = '488' where answer = 'Magnetic tester' and questionID = 7061;
update pqfdata set answer = '489' where answer = 'Liquid Penetrate' and questionID = 7061;
update pqfdata set answer = '490' where answer = 'Eddy Current' and questionID = 7061;
update pqfdata set answer = '491' where answer = 'Chemical analysis' and questionID = 7061;
update pqfdata set answer = '499' where answer = 'You have a Quality Plan or equivalent' and questionID = 7128;
update pqfdata set answer = '500' where answer = 'You have an Inspection Test Plan (ITP) or equivalent' and questionID = 7128;
update pqfdata set answer = '501' where answer = 'Both of the above.' and questionID = 7128;
update pqfdata set answer = '502' where answer = 'None of the above' and questionID = 7128;
update pqfdata set answer = '514' where answer = 'CanQual' and questionID = 7727;
update pqfdata set answer = '515' where answer = 'ComplyWorks' and questionID = 7727;
update pqfdata set answer = '516' where answer = 'PEC Primier' and questionID = 7727;
update pqfdata set answer = '517' where answer = 'Achilles' and questionID = 7727;
update pqfdata set answer = '518' where answer = 'ISN' and questionID = 7727;
update pqfdata set answer = '519' where answer = 'Browz' and questionID = 7727;
update pqfdata set answer = '520' where answer = 'Other' and questionID = 7727;

-- Audit Question Options don't have a conversion script (copied from sqldump)
insert into `audit_option_group` (`id`, `radio`, `name`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('13','0','Form of Business',NULL,'23157','23157','2011-04-28 13:29:09','2011-05-03 14:12:44');
insert into `audit_option_group` (`id`, `radio`, `name`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('14','0','Safety US Citations',NULL,'23157','23157','2011-04-28 13:52:38','2011-04-28 13:53:39');
insert into `audit_option_group` (`id`, `radio`, `name`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('15','0','Communications',NULL,'23157','23157','2011-04-28 13:52:42','2011-04-28 14:02:56');
insert into `audit_option_group` (`id`, `radio`, `name`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('16','0','Drug and Alcohol Testing Service',NULL,'23157','23157','2011-04-28 14:24:15','2011-04-28 14:24:29');
insert into `audit_option_group` (`id`, `radio`, `name`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('17','0','US Assessment Programs',NULL,'23157','23157','2011-04-28 14:26:55','2011-04-28 14:26:55');
insert into `audit_option_group` (`id`, `radio`, `name`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('18','0','IH Substance Testing',NULL,'23157','23157','2011-04-28 14:28:53','2011-04-28 14:28:53');
insert into `audit_option_group` (`id`, `radio`, `name`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('19','1','Competency Colors','Colors','23157','23157','2011-04-28 14:30:17','2011-05-09 11:05:46');
insert into `audit_option_group` (`id`, `radio`, `name`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('20','1','Low/Medium/High','LowMedHigh','23157','23157','2011-04-28 14:35:00','2011-04-28 14:35:00');
insert into `audit_option_group` (`id`, `radio`, `name`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('21','1','COR Type','COR','23157','23157','2011-04-28 14:42:07','2011-05-03 14:58:37');
insert into `audit_option_group` (`id`, `radio`, `name`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('22','1','Contractor Acceptance',NULL,'23157','23157','2011-04-28 14:49:53','2011-04-28 14:49:53');
insert into `audit_option_group` (`id`, `radio`, `name`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('23','1','UAE Stock Value',NULL,'23157','23157','2011-04-28 14:56:09','2011-04-28 14:56:09');
insert into `audit_option_group` (`id`, `radio`, `name`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('24','1','UAE Company Country',NULL,'23157','23157','2011-04-28 15:01:46','2011-04-28 15:01:46');
insert into `audit_option_group` (`id`, `radio`, `name`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('25','1','Yes/No','YesNo','23157','23157','2011-04-28 15:10:46','2011-04-28 15:10:46');
insert into `audit_option_group` (`id`, `radio`, `name`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('26','1','Yes/No/NA','YesNoNA','23157','23157','2011-04-28 15:12:39','2011-04-28 15:12:39');
insert into `audit_option_group` (`id`, `radio`, `name`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('27','0','Country','Country','23157','23157','2011-04-28 15:21:41','2011-04-28 16:15:07');
insert into `audit_option_group` (`id`, `radio`, `name`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('28','0','State','State','23157',NULL,'2011-05-02 10:07:31',NULL);
insert into `audit_option_group` (`id`, `radio`, `name`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('30','1','Office Location','OfficeLocation','23157','23157','2011-05-03 14:12:27','2011-05-03 14:12:27');
insert into `audit_option_group` (`id`, `radio`, `name`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('31','0','Rating 1-5','Rating','23157','23157','2011-05-03 14:26:56','2011-05-03 14:26:56');
insert into `audit_option_group` (`id`, `radio`, `name`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('32','1','Sample Radio Question',NULL,'23157','23157','2011-05-03 17:40:51','2011-05-04 16:35:36');
insert into `audit_option_group` (`id`, `radio`, `name`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('33','0','EMR Rate Type',NULL,'23157','23157','2011-05-04 08:29:35','2011-05-04 08:29:35');
insert into `audit_option_group` (`id`, `radio`, `name`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('35','0','Suncor QM External Registrations',NULL,'23157','23157','2011-05-04 08:43:59','2011-05-04 08:43:59');
insert into `audit_option_group` (`id`, `radio`, `name`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('36','0','Suncor QM Facility Test Procedures',NULL,'23157','23157','2011-05-04 08:48:01','2011-05-04 08:48:01');
insert into `audit_option_group` (`id`, `radio`, `name`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('37','1','Suncor QM Quality Planning Company Description',NULL,'23157','23157','2011-05-04 08:55:06','2011-05-04 08:55:06');
insert into `audit_option_group` (`id`, `radio`, `name`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('38','1','Suncor QM Scored Yes/No/NA 4pts',NULL,'23157','23157','2011-05-12 16:08:30','2011-05-12 16:08:30');
insert into `audit_option_group` (`id`, `radio`, `name`, `uniqueCode`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`) values('39','1','Suncor QM Scored Yes/No/NA 1pt',NULL,'23157','23157','2011-05-12 16:09:12','2011-05-12 16:09:12');
insert into `audit_option_group` (radio, name, uniqueCode, createdBy, creationDate) values (0, '3rd Party PQF', null, 23157, now());

insert  into `audit_option_value`(`id`,`typeID`,`visible`,`number`,`uniqueCode`,`score`,`createdBy`,`updatedBy`,`creationDate`,`updateDate`)
values
(1,13,1,5,NULL,0,23157,NULL,'2011-04-28 13:33:56',NULL),
(2,13,1,9,NULL,0,23157,NULL,'2011-04-28 13:33:56',NULL),
(3,13,1,8,NULL,0,23157,NULL,'2011-04-28 13:33:56',NULL),
(4,13,1,10,NULL,0,23157,NULL,'2011-04-28 13:33:56',NULL),
(5,13,1,6,NULL,0,23157,NULL,'2011-04-28 13:33:56',NULL),
(6,13,1,7,NULL,0,23157,NULL,'2011-04-28 13:33:56',NULL),
(7,13,1,4,NULL,0,23157,NULL,'2011-04-28 13:33:56',NULL),
(8,13,1,20,NULL,0,23157,NULL,'2011-04-28 13:33:56',NULL),
(9,13,1,11,NULL,0,23157,NULL,'2011-04-28 13:33:56',NULL),
(10,13,1,1,NULL,0,23157,NULL,'2011-04-28 13:33:56',NULL),
(11,13,1,3,NULL,0,23157,NULL,'2011-04-28 13:33:56',NULL),
(12,13,1,2,NULL,0,23157,NULL,'2011-04-28 13:33:56',NULL),
(13,14,0,6,NULL,0,23157,23157,'2011-04-28 13:59:04','2011-05-03 15:36:20'),
(14,14,0,8,NULL,0,23157,23157,'2011-04-28 13:59:04','2011-05-03 15:36:39'),
(15,14,0,10,NULL,0,23157,23157,'2011-04-28 13:59:04','2011-05-03 15:36:53'),
(16,14,0,9,NULL,0,23157,23157,'2011-04-28 13:59:04','2011-05-03 15:36:46'),
(17,14,0,7,NULL,0,23157,23157,'2011-04-28 13:59:04','2011-05-03 15:36:30'),
(18,14,0,5,NULL,0,23157,23157,'2011-04-28 13:59:04','2011-05-03 15:36:06'),
(19,14,0,4,NULL,0,23157,23157,'2011-04-28 13:59:04','2011-05-03 15:12:11'),
(20,14,0,1,NULL,0,23157,NULL,'2011-04-28 13:59:05',NULL),
(21,14,0,2,NULL,0,23157,23157,'2011-04-28 13:59:05','2011-05-03 15:11:40'),
(22,14,0,3,NULL,0,23157,23157,'2011-04-28 13:59:05','2011-05-03 15:11:58'),
(23,15,1,4,NULL,0,23157,NULL,'2011-04-28 14:11:59',NULL),
(24,15,1,1,NULL,0,23157,NULL,'2011-04-28 14:12:00',NULL),
(25,15,1,2,NULL,0,23157,NULL,'2011-04-28 14:12:00',NULL),
(26,15,1,3,NULL,0,23157,NULL,'2011-04-28 14:12:00',NULL),
(27,16,1,2,NULL,0,23157,NULL,'2011-04-28 14:25:02',NULL),
(28,16,1,1,NULL,0,23157,NULL,'2011-04-28 14:25:02',NULL),
(29,16,1,4,NULL,0,23157,NULL,'2011-04-28 14:25:02',NULL),
(30,16,1,3,NULL,0,23157,NULL,'2011-04-28 14:25:02',NULL),
(31,17,0,4,NULL,0,23157,NULL,'2011-04-28 14:27:32',NULL),
(32,17,0,3,NULL,0,23157,NULL,'2011-04-28 14:27:32',NULL),
(33,17,0,2,NULL,0,23157,NULL,'2011-04-28 14:27:32',NULL),
(34,17,0,1,NULL,0,23157,NULL,'2011-04-28 14:27:32',NULL),
(35,17,0,5,NULL,0,23157,NULL,'2011-04-28 14:27:32',NULL),
(36,18,1,9,NULL,0,23157,NULL,'2011-04-28 14:29:11',NULL),
(37,18,1,8,NULL,0,23157,NULL,'2011-04-28 14:29:11',NULL),
(38,18,1,7,NULL,0,23157,NULL,'2011-04-28 14:29:11',NULL),
(39,18,1,5,NULL,0,23157,NULL,'2011-04-28 14:29:11',NULL),
(40,18,1,4,NULL,0,23157,NULL,'2011-04-28 14:29:11',NULL),
(41,18,1,3,NULL,0,23157,NULL,'2011-04-28 14:29:11',NULL),
(42,18,1,2,NULL,0,23157,NULL,'2011-04-28 14:29:11',NULL),
(43,18,1,1,NULL,0,23157,NULL,'2011-04-28 14:29:11',NULL),
(44,18,1,6,NULL,0,23157,NULL,'2011-04-28 14:29:11',NULL),
(45,19,1,5,'Yellow',1,23157,23157,'2011-04-28 14:31:09','2011-05-03 15:02:15'),
(46,19,1,1,'Green',2,23157,23157,'2011-04-28 14:31:09','2011-05-03 15:02:09'),
(47,19,1,10,'Red',0,23157,23157,'2011-04-28 14:31:09','2011-05-03 15:02:20'),
(49,20,1,2,'Medium',0,23157,NULL,'2011-04-28 14:36:46',NULL),
(50,20,1,3,'High',0,23157,NULL,'2011-04-28 14:36:46',NULL),
(51,21,1,1,'COR',0,23157,NULL,'2011-04-28 14:41:43',NULL),
(52,21,1,2,'SECOR',0,23157,NULL,'2011-04-28 14:41:43',NULL),
(53,22,1,1,NULL,0,23157,23157,'2011-04-28 14:54:22','2011-04-28 14:54:22'),
(54,22,1,2,NULL,0,23157,23157,'2011-04-28 14:54:34','2011-04-28 14:54:34'),
(55,22,1,3,NULL,0,23157,23157,'2011-04-28 14:54:43','2011-04-28 14:54:43'),
(56,23,1,1,NULL,0,23157,23157,'2011-04-28 15:00:28','2011-04-28 15:03:18'),
(57,23,1,2,NULL,0,23157,NULL,'2011-04-28 15:00:28',NULL),
(58,23,1,3,NULL,0,23157,NULL,'2011-04-28 15:00:28',NULL),
(59,23,1,4,NULL,0,23157,23157,'2011-04-28 15:00:28','2011-04-28 15:03:26'),
(60,24,1,1,NULL,0,23157,NULL,'2011-04-28 15:02:31',NULL),
(61,24,1,2,NULL,0,23157,NULL,'2011-04-28 15:02:32',NULL),
(62,24,1,3,NULL,0,23157,NULL,'2011-04-28 15:02:32',NULL),
(63,24,1,4,NULL,0,23157,NULL,'2011-04-28 15:02:32',NULL),
(64,24,1,5,NULL,0,23157,NULL,'2011-04-28 15:02:32',NULL),
(65,24,1,6,NULL,0,23157,NULL,'2011-04-28 15:02:32',NULL),
(66,24,1,7,NULL,0,23157,NULL,'2011-04-28 15:02:32',NULL),
(67,24,1,8,NULL,0,23157,NULL,'2011-04-28 15:02:32',NULL),
(68,24,1,9,NULL,0,23157,NULL,'2011-04-28 15:02:32',NULL),
(69,25,1,1,'Yes',0,23157,23157,'2011-04-28 15:10:59','2011-04-28 15:10:59'),
(70,25,1,2,'No',0,23157,23157,'2011-04-28 15:11:12','2011-04-28 15:11:12'),
(71,26,1,1,'Yes',2,23157,23157,'2011-04-28 15:12:56','2011-04-28 15:12:56'),
(72,26,1,2,'No',0,23157,23157,'2011-04-28 15:13:05','2011-04-28 15:13:05'),
(73,26,1,3,'NA',0,23157,23157,'2011-04-28 15:13:17','2011-04-28 15:13:17'),
(74,27,1,1,'US',0,23157,23157,'2011-04-28 16:11:43','2011-04-28 16:11:43'),
(75,27,1,2,'CA',0,23157,23157,'2011-04-28 16:13:25','2011-04-28 16:13:25'),
(76,28,1,6,'CA',0,23157,23157,'2011-05-02 08:40:05','2011-05-02 08:40:05'),
(77,27,1,3,'AF',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(78,27,1,4,'AL',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(79,27,1,5,'DZ',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(80,27,1,6,'AS',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(81,27,1,7,'AD',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(82,27,1,8,'AO',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(83,27,1,9,'AI',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(84,27,1,10,'AQ',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(85,27,1,11,'AG',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(86,27,1,12,'AR',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(87,27,1,13,'AM',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(88,27,1,14,'AW',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(89,27,1,15,'AU',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(90,27,1,16,'AT',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(91,27,1,17,'AZ',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(92,27,1,18,'BS',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(93,27,1,19,'BH',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(94,27,1,20,'BD',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(95,27,1,21,'BB',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(96,27,1,22,'BY',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(97,27,1,23,'BE',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(98,27,1,24,'BZ',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(99,27,1,25,'BJ',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(100,27,1,26,'BM',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(101,27,1,27,'BT',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(102,27,1,28,'BO',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(103,27,1,29,'BA',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(104,27,1,30,'BW',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(105,27,1,31,'BV',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(106,27,1,32,'BR',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(107,27,1,33,'IO',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(108,27,1,34,'VG',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(109,27,1,35,'BN',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(110,27,1,36,'BG',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(111,27,1,37,'BF',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(112,27,1,38,'BI',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(113,27,1,39,'KH',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(114,27,1,40,'CM',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(115,27,1,41,'CV',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(116,27,1,42,'KY',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(117,27,1,43,'CF',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(118,27,1,44,'TD',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(119,27,1,45,'CL',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(120,27,1,46,'CN',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(121,27,1,47,'CX',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(122,27,1,48,'CC',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(123,27,1,49,'CO',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(124,27,1,50,'KM',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(125,27,1,51,'CD',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(126,27,1,52,'CG',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(127,27,1,53,'CK',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(128,27,1,54,'CR',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(129,27,1,55,'CI',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(130,27,1,56,'HR',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(131,27,1,57,'CU',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(132,27,1,58,'CY',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(133,27,1,59,'CZ',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(134,27,1,60,'DK',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(135,27,1,61,'DJ',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(136,27,1,62,'DM',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(137,27,1,63,'DO',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(138,27,1,64,'EC',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(139,27,1,65,'EG',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(140,27,1,66,'SV',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(141,27,1,67,'GQ',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(142,27,1,68,'ER',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(143,27,1,69,'EE',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(144,27,1,70,'ET',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(145,27,1,71,'FK',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(146,27,1,72,'FO',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(147,27,1,73,'FJ',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(148,27,1,74,'FI',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(149,27,1,75,'FR',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(150,27,1,76,'GF',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(151,27,1,77,'PF',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(152,27,1,78,'TF',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(153,27,1,79,'GA',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(154,27,1,80,'GM',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(155,27,1,81,'GE',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(156,27,1,82,'DE',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(157,27,1,83,'GH',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(158,27,1,84,'GI',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(159,27,1,85,'GR',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(160,27,1,86,'GL',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(161,27,1,87,'GD',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(162,27,1,88,'GP',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(163,27,1,89,'GU',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(164,27,1,90,'GT',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(165,27,1,91,'GG',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(166,27,1,92,'GN',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(167,27,1,93,'GW',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(168,27,1,94,'GY',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(169,27,1,95,'HT',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(170,27,1,96,'HM',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(171,27,1,97,'VA',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(172,27,1,98,'HN',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(173,27,1,99,'HK',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(174,27,1,100,'HU',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(175,27,1,101,'IS',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(176,27,1,102,'IN',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(177,27,1,103,'ID',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(178,27,1,104,'IR',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(179,27,1,105,'IQ',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(180,27,1,106,'IE',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(181,27,1,107,'IM',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(182,27,1,108,'IL',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(183,27,1,109,'IT',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(184,27,1,110,'JM',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(185,27,1,111,'JP',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(186,27,1,112,'JE',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(187,27,1,113,'JO',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(188,27,1,114,'KZ',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(189,27,1,115,'KE',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(190,27,1,116,'KI',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(191,27,1,117,'KP',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(192,27,1,118,'KR',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(193,27,1,119,'KW',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(194,27,1,120,'KG',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(195,27,1,121,'LA',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(196,27,1,122,'LV',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(197,27,1,123,'LB',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(198,27,1,124,'LS',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(199,27,1,125,'LR',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(200,27,1,126,'LY',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(201,27,1,127,'LI',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(202,27,1,128,'LT',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(203,27,1,129,'LU',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(204,27,1,130,'MO',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(205,27,1,131,'MK',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(206,27,1,132,'MG',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(207,27,1,133,'MW',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(208,27,1,134,'MY',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(209,27,1,135,'MV',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(210,27,1,136,'ML',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(211,27,1,137,'MT',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(212,27,1,138,'MH',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(213,27,1,139,'MQ',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(214,27,1,140,'MR',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(215,27,1,141,'MU',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(216,27,1,142,'YT',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(217,27,1,143,'MX',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(218,27,1,144,'FM',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(219,27,1,145,'MD',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(220,27,1,146,'MC',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(221,27,1,147,'MN',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(222,27,1,148,'ME',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(223,27,1,149,'MS',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(224,27,1,150,'MA',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(225,27,1,151,'MZ',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(226,27,1,152,'MM',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(227,27,1,153,'NA',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(228,27,1,154,'NR',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(229,27,1,155,'NP',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(230,27,1,156,'NL',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(231,27,1,157,'AN',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(232,27,1,158,'NC',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(233,27,1,159,'NZ',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(234,27,1,160,'NI',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(235,27,1,161,'NE',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(236,27,1,162,'NG',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(237,27,1,163,'NU',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(238,27,1,164,'NF',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(239,27,1,165,'MP',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(240,27,1,166,'NO',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(241,27,1,167,'OM',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(242,27,1,168,'PK',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(243,27,1,169,'PW',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(244,27,1,170,'PS',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(245,27,1,171,'PA',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(246,27,1,172,'PG',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(247,27,1,173,'PY',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(248,27,1,174,'PE',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(249,27,1,175,'PH',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(250,27,1,176,'PN',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(251,27,1,177,'PL',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(252,27,1,178,'PT',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(253,27,1,179,'PR',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(254,27,1,180,'QA',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(255,27,1,181,'RE',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(256,27,1,182,'RO',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(257,27,1,183,'RU',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(258,27,1,184,'RW',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(259,27,1,185,'BL',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(260,27,1,186,'SH',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(261,27,1,187,'KN',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(262,27,1,188,'LC',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(263,27,1,189,'MF',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(264,27,1,190,'PM',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(265,27,1,191,'VC',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(266,27,1,192,'WS',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(267,27,1,193,'SM',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(268,27,1,194,'ST',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(269,27,1,195,'SA',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(270,27,1,196,'SN',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(271,27,1,197,'RS',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(272,27,1,198,'SC',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(273,27,1,199,'SL',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(274,27,1,200,'SG',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(275,27,1,201,'SK',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(276,27,1,202,'SI',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(277,27,1,203,'SB',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(278,27,1,204,'SO',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(279,27,1,205,'ZA',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(280,27,1,206,'GS',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(281,27,1,207,'ES',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(282,27,1,208,'LK',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(283,27,1,209,'SD',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(284,27,1,210,'SR',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(285,27,1,211,'SJ',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(286,27,1,212,'SZ',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(287,27,1,213,'SE',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(288,27,1,214,'CH',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(289,27,1,215,'SY',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(290,27,1,216,'TW',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(291,27,1,217,'TJ',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(292,27,1,218,'TZ',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(293,27,1,219,'TH',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(294,27,1,220,'TL',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(295,27,1,221,'TG',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(296,27,1,222,'TK',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(297,27,1,223,'TO',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(298,27,1,224,'TT',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(299,27,1,225,'TN',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(300,27,1,226,'TR',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(301,27,1,227,'TM',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(302,27,1,228,'TC',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(303,27,1,229,'TV',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(304,27,1,230,'UG',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(305,27,1,231,'UA',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(306,27,1,232,'AE',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(307,27,1,233,'GB',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(308,27,1,234,'UM',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(309,27,1,235,'VI',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(310,27,1,236,'UY',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(311,27,1,237,'UZ',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(312,27,1,238,'VU',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(313,27,1,239,'VE',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(314,27,1,240,'VN',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(315,27,1,241,'WF',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(316,27,1,242,'EH',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(317,27,1,243,'YE',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(318,27,1,244,'ZM',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(319,27,1,245,'ZW',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(320,27,1,246,'AX',0,23157,NULL,'2011-05-02 15:36:51',NULL),
(332,28,1,1,'AL',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(333,28,1,2,'AK',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(334,28,1,58,'AB',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(335,28,1,3,'AS',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(336,28,1,4,'AZ',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(337,28,1,5,'AR',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(338,28,1,59,'BC',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(339,28,1,7,'CO',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(340,28,1,8,'CT',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(341,28,1,9,'DE',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(342,28,1,10,'DC',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(343,28,1,11,'FL',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(344,28,1,12,'GA',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(345,28,1,13,'GU',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(346,28,1,14,'HI',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(347,28,1,15,'ID',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(348,28,1,16,'IL',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(349,28,1,17,'IN',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(350,28,1,18,'IA',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(351,28,1,19,'KS',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(352,28,1,20,'KY',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(353,28,1,21,'LA',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(354,28,1,22,'ME',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(355,28,1,60,'MB',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(356,28,1,23,'MD',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(357,28,1,24,'MA',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(358,28,1,25,'MI',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(359,28,1,26,'MN',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(360,28,1,27,'MS',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(361,28,1,28,'MO',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(362,28,1,29,'MT',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(363,28,1,30,'NE',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(364,28,1,31,'NV',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(365,28,1,61,'NB',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(366,28,1,32,'NH',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(367,28,1,33,'NJ',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(368,28,1,34,'NM',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(369,28,1,35,'NY',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(370,28,1,62,'NL',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(371,28,1,36,'NC',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(372,28,1,37,'ND',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(373,28,1,38,'MP',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(374,28,1,63,'NT',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(375,28,1,64,'NS',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(376,28,1,65,'NU',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(377,28,1,39,'OH',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(378,28,1,40,'OK',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(379,28,1,66,'ON',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(380,28,1,41,'OR',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(381,28,1,42,'PA',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(382,28,1,67,'PE',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(383,28,1,43,'PR',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(384,28,1,68,'QC',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(385,28,1,44,'RI',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(386,28,1,69,'SK',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(387,28,1,45,'SC',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(388,28,1,46,'SD',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(389,28,1,47,'TN',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(390,28,1,48,'TX',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(391,28,1,49,'UM',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(392,28,1,50,'UT',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(393,28,1,51,'VT',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(394,28,1,52,'VI',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(395,28,1,53,'VA',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(396,28,1,54,'WA',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(397,28,1,55,'WV',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(398,28,1,56,'WI',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(399,28,1,57,'WY',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(400,28,1,70,'YT',0,23157,NULL,'2011-05-02 16:47:37',NULL),
(459,30,1,1,'No',0,23157,23157,'2011-05-03 14:18:35','2011-05-03 14:35:35'),
(460,30,1,2,'Yes',0,23157,23157,'2011-05-03 14:20:37','2011-05-03 14:35:41'),
(461,30,1,3,'YesWithOffice',0,23157,23157,'2011-05-03 14:20:58','2011-05-03 14:35:48'),
(462,31,1,1,'1',0,23157,23157,'2011-05-03 14:27:06','2011-05-03 14:27:48'),
(463,31,1,2,'2',0,23157,23157,'2011-05-03 14:27:22','2011-05-03 14:27:22'),
(464,31,1,3,'3',0,23157,23157,'2011-05-03 14:27:31','2011-05-03 14:27:31'),
(465,31,1,4,'4',0,23157,23157,'2011-05-03 14:27:42','2011-05-03 14:27:42'),
(466,31,1,5,'5',0,23157,23157,'2011-05-03 14:27:58','2011-05-03 14:27:58'),
(467,32,1,1,NULL,0,23157,23157,'2011-05-03 17:41:02','2011-05-03 17:41:02'),
(468,32,1,2,NULL,0,23157,23157,'2011-05-03 17:41:11','2011-05-03 17:41:11'),
(469,32,0,3,NULL,0,23157,23157,'2011-05-03 17:41:32','2011-05-03 17:41:32'),
(470,33,1,1,NULL,0,23157,23157,'2011-05-04 08:31:10','2011-05-04 08:31:10'),
(471,33,1,2,NULL,0,23157,23157,'2011-05-04 08:32:30','2011-05-04 08:32:30'),
(472,33,1,3,NULL,0,23157,23157,'2011-05-04 08:35:40','2011-05-04 08:35:40'),
(473,33,1,4,NULL,0,23157,23157,'2011-05-04 08:37:29','2011-05-04 08:37:29'),
(477,35,1,1,NULL,0,23157,23157,'2011-05-04 08:46:41','2011-05-04 08:49:09'),
(478,35,1,2,NULL,0,23157,23157,'2011-05-04 08:46:41','2011-05-04 08:49:15'),
(479,35,1,3,NULL,0,23157,23157,'2011-05-04 08:46:41','2011-05-04 08:49:23'),
(480,35,1,4,NULL,0,23157,23157,'2011-05-04 08:46:41','2011-05-04 08:49:29'),
(484,36,1,1,NULL,0,23157,NULL,'2011-05-04 08:52:44',NULL),
(485,36,1,2,NULL,0,23157,NULL,'2011-05-04 08:52:44',NULL),
(486,36,1,3,NULL,0,23157,NULL,'2011-05-04 08:52:44',NULL),
(487,36,1,4,NULL,0,23157,NULL,'2011-05-04 08:52:44',NULL),
(488,36,1,5,NULL,0,23157,NULL,'2011-05-04 08:52:44',NULL),
(489,36,1,6,NULL,0,23157,NULL,'2011-05-04 08:52:44',NULL),
(490,36,1,7,NULL,0,23157,NULL,'2011-05-04 08:52:44',NULL),
(491,36,1,8,NULL,0,23157,NULL,'2011-05-04 08:52:44',NULL),
(499,37,1,1,NULL,1,23157,NULL,'2011-05-04 08:56:14',NULL),
(500,37,1,2,NULL,1,23157,NULL,'2011-05-04 08:56:14',NULL),
(501,37,1,3,NULL,1,23157,NULL,'2011-05-04 08:56:14',NULL),
(502,37,1,4,NULL,0,23157,NULL,'2011-05-04 08:56:14',NULL),
(506,38,1,1,'Yes',4,2357,23157,'2011-02-18 14:23:13','2011-05-11 14:29:09'),
(507,38,1,2,'No',0,2357,23157,'2011-02-18 14:23:14','2011-05-11 14:29:19'),
(508,38,1,3,'NA',0,2357,23157,'2011-02-18 14:23:16','2011-05-11 14:29:25'),
(510,20,1,1,'Low',0,23157,23157,'2011-05-12 12:50:04','2011-05-12 12:50:04'),
(511,39,1,1,'Yes',1,23157,23157,'2011-05-12 16:11:13','2011-05-12 16:11:13'),
(512,39,1,2,'No',0,23157,23157,'2011-05-12 16:11:36','2011-05-12 16:11:36'),
(513,39,1,3,'NA',0,23157,23157,'2011-05-12 16:11:45','2011-05-12 16:11:45'),
(514,40,1,1,NULL,0,23157,NULL,'2011-05-31 09:27:31',NULL),
(515,40,1,2,NULL,0,23157,NULL,'2011-05-31 09:27:31',NULL),
(516,40,1,3,NULL,0,23157,NULL,'2011-05-31 09:27:31',NULL),
(517,40,1,4,NULL,0,23157,NULL,'2011-05-31 09:27:31',NULL),
(518,40,1,5,NULL,0,23157,NULL,'2011-05-31 09:27:31',NULL),
(519,40,1,6,NULL,0,23157,NULL,'2011-05-31 09:27:31',NULL),
(520,40,1,7,NULL,0,23157,NULL,'2011-05-31 09:27:31',NULL);

insert into app_translation (`msgKey`, `locale`, `msgValue`, `createdBy`, `creationDate`)
values
('AuditOptionGroup.13.1', 'en', 'Sole Owner', 23157, NOW()),
('AuditOptionGroup.13.10', 'en', 'Corporation', 23157, NOW()),
('AuditOptionGroup.13.11', 'en', 'LLP (Limited Liability Partnership)', 23157, NOW()),
('AuditOptionGroup.13.12', 'en', 'LLC (Limited Liability Corporation)', 23157, NOW()),
('AuditOptionGroup.13.2', 'en', 'Ltd', 23157, NOW()),
('AuditOptionGroup.13.3', 'en', 'Plc', 23157, NOW()),
('AuditOptionGroup.13.4', 'en', 'Ultd', 23157, NOW()),
('AuditOptionGroup.13.5', 'en', 'AG', 23157, NOW()),
('AuditOptionGroup.13.6', 'en', 'GmbH', 23157, NOW()),
('AuditOptionGroup.13.7', 'en', 'Partnership', 23157, NOW()),
('AuditOptionGroup.13.8', 'en', 'Other', 23157, NOW()),
('AuditOptionGroup.13.9', 'en', 'ULC', 23157, NOW()),
('AuditOptionGroup.14.13', 'en', 'State OSHA (Cal-Osha, WSHA, etc.)', 23157, NOW()),
('AuditOptionGroup.14.14', 'en', 'BLM (Bureau of Land Management)', 23157, NOW()),
('AuditOptionGroup.14.15', 'en', 'MMS', 23157, NOW()),
('AuditOptionGroup.14.16', 'en', 'U.S. Coast Guard', 23157, NOW()),
('AuditOptionGroup.14.17', 'en', 'DOT (Department of Transportation)', 23157, NOW()),
('AuditOptionGroup.14.18', 'en', 'MSHA (Mine Safety Health Administration)', 23157, NOW()),
('AuditOptionGroup.14.19', 'en', 'EPA (Environmental Protection Agency)', 23157, NOW()),
('AuditOptionGroup.14.20', 'en', 'OSHA', 23157, NOW()),
('AuditOptionGroup.14.21', 'en', 'MSHA', 23157, NOW()),
('AuditOptionGroup.14.22', 'en', 'State EPA', 23157, NOW()),
('AuditOptionGroup.15.23', 'en', 'Portuguese', 23157, NOW()),
('AuditOptionGroup.15.24', 'en', 'English', 23157, NOW()),
('AuditOptionGroup.15.25', 'en', 'Spanish', 23157, NOW()),
('AuditOptionGroup.15.26', 'en', 'French', 23157, NOW()),
('AuditOptionGroup.16.27', 'en', 'ASAP', 23157, NOW()),
('AuditOptionGroup.16.28', 'en', 'DISA', 23157, NOW()),
('AuditOptionGroup.16.29', 'en', 'Other', 23157, NOW()),
('AuditOptionGroup.16.30', 'en', 'PTC (Pipeline Testing Consortium)', 23157, NOW()),
('AuditOptionGroup.17.31', 'en', 'IN-HOUSE', 23157, NOW()),
('AuditOptionGroup.17.32', 'en', 'NICET', 23157, NOW()),
('AuditOptionGroup.17.33', 'en', 'NCCCO', 23157, NOW()),
('AuditOptionGroup.17.34', 'en', 'NCCER', 23157, NOW()),
('AuditOptionGroup.17.35', 'en', 'OTHER', 23157, NOW()),
('AuditOptionGroup.18.36', 'en', 'Other', 23157, NOW()),
('AuditOptionGroup.18.37', 'en', 'Welding Fumes', 23157, NOW()),
('AuditOptionGroup.18.38', 'en', 'Total Hydrocarbons', 23157, NOW()),
('AuditOptionGroup.18.39', 'en', 'Radiation', 23157, NOW()),
('AuditOptionGroup.18.40', 'en', 'Lead', 23157, NOW()),
('AuditOptionGroup.18.41', 'en', 'Hydrogen Sulfide (H2S)', 23157, NOW()),
('AuditOptionGroup.18.42', 'en', 'Benzene', 23157, NOW()),
('AuditOptionGroup.18.43', 'en', 'Asbestos', 23157, NOW()),
('AuditOptionGroup.18.44', 'en', 'Silica', 23157, NOW()),
('AuditOptionGroup.22.53', 'en', 'Accepted', 23157, NOW()),
('AuditOptionGroup.22.54', 'en', 'Conditionally Accepted', 23157, NOW()),
('AuditOptionGroup.22.55', 'en', 'Not Accepted', 23157, NOW()),
('AuditOptionGroup.23.56', 'en', '< 100,000 AED', 23157, NOW()),
('AuditOptionGroup.23.57', 'en', '100,000 AED - 1,000,000 AED', 23157, NOW()),
('AuditOptionGroup.23.58', 'en', '1,000,000 AED - 5,000,000 AED', 23157, NOW()),
('AuditOptionGroup.23.59', 'en', '> 5,000,000 AED', 23157, NOW()),
('AuditOptionGroup.24.60', 'en', 'Abu Dhabi', 23157, NOW()),
('AuditOptionGroup.24.61', 'en', 'Dubai', 23157, NOW()),
('AuditOptionGroup.24.62', 'en', 'Sharjah', 23157, NOW()),
('AuditOptionGroup.24.63', 'en', 'Ajman', 23157, NOW()),
('AuditOptionGroup.24.64', 'en', 'Umm Al-Quwain', 23157, NOW()),
('AuditOptionGroup.24.65', 'en', 'Ras Al-Khaimah', 23157, NOW()),
('AuditOptionGroup.24.66', 'en', 'Fujairah', 23157, NOW()),
('AuditOptionGroup.24.67', 'en', 'Arab Gulf Cooperation Country (AGCC)', 23157, NOW()),
('AuditOptionGroup.24.68', 'en', 'Other Country', 23157, NOW()),
('AuditOptionGroup.32.467', 'en', 'First', 23157, NOW()),
('AuditOptionGroup.32.468', 'en', 'Second', 23157, NOW()),
('AuditOptionGroup.32.469', 'en', 'Third (invisible)', 23157, NOW()),
('AuditOptionGroup.33.470', 'en', 'Interstate', 23157, NOW()),
('AuditOptionGroup.33.471', 'en', 'Intrastate', 23157, NOW()),
('AuditOptionGroup.33.472', 'en', 'Monopolistic State Rate', 23157, NOW()),
('AuditOptionGroup.33.473', 'en', 'Dual Rate', 23157, NOW()),
('AuditOptionGroup.35.477', 'en', 'ASME', 23157, NOW()),
('AuditOptionGroup.35.478', 'en', 'API', 23157, NOW()),
('AuditOptionGroup.35.479', 'en', 'API Monogram', 23157, NOW()),
('AuditOptionGroup.35.480', 'en', 'Others', 23157, NOW()),
('AuditOptionGroup.36.484', 'en', 'Heat Treatment', 23157, NOW()),
('AuditOptionGroup.36.485', 'en', 'Painting', 23157, NOW()),
('AuditOptionGroup.36.486', 'en', 'Impact tester', 23157, NOW()),
('AuditOptionGroup.36.487', 'en', 'Hardness tester', 23157, NOW()),
('AuditOptionGroup.36.488', 'en', 'Magnetic tester', 23157, NOW()),
('AuditOptionGroup.36.489', 'en', 'Liquid Penetrate', 23157, NOW()),
('AuditOptionGroup.36.490', 'en', 'Eddy Current', 23157, NOW()),
('AuditOptionGroup.36.491', 'en', 'Chemical analysis', 23157, NOW()),
('AuditOptionGroup.37.499', 'en', 'You have a Quality Plan or equivalent', 23157, NOW()),
('AuditOptionGroup.37.500', 'en', 'You have an Inspection Test Plan (ITP) or equivalent', 23157, NOW()),
('AuditOptionGroup.37.501', 'en', 'Both of the above.', 23157, NOW()),
('AuditOptionGroup.37.502', 'en', 'None of the above', 23157, NOW()),
('AuditOptionGroup.38.NA', 'en', 'N/A', 23157, NOW()),
('AuditOptionGroup.38.No', 'en', 'No', 23157, NOW()),
('AuditOptionGroup.38.Yes', 'en', 'Yes', 23157, NOW()),
('AuditOptionGroup.39.NA', 'en', 'N/A', 23157, NOW()),
('AuditOptionGroup.39.No', 'en', 'No', 23157, NOW()),
('AuditOptionGroup.39.Yes', 'en', 'Yes', 23157, NOW()),
('AuditOptionGroup.40.514', 'en', 'CanQual', 23157, NOW()),
('AuditOptionGroup.40.515', 'en', 'ComplyWorks', 23157, NOW()),
('AuditOptionGroup.40.516', 'en', 'PEC Primier', 23157, NOW()),
('AuditOptionGroup.40.517', 'en', 'Achilles', 23157, NOW()),
('AuditOptionGroup.40.518', 'en', 'ISN', 23157, NOW()),
('AuditOptionGroup.40.519', 'en', 'Browz', 23157, NOW()),
('AuditOptionGroup.40.520', 'en', 'Other', 23157, NOW()),
('AuditOptionGroup.COR.COR', 'en', 'COR', 23157, NOW()),
('AuditOptionGroup.COR.SECOR', 'en', 'SECOR', 23157, NOW()),
('AuditOptionGroup.Colors.Green', 'en', 'Green', 23157, NOW()),
('AuditOptionGroup.Colors.Red', 'en', 'Red', 23157, NOW()),
('AuditOptionGroup.Colors.Yellow', 'en', 'Yellow', 23157, NOW()),
('AuditOptionGroup.OfficeLocation.No', 'en', 'No', 23157, NOW()),
('AuditOptionGroup.OfficeLocation.Yes', 'en', 'Yes', 23157, NOW()),
('AuditOptionGroup.OfficeLocation.YesWithOffice', 'en', 'Yes with Office', 23157, NOW()),
('AuditOptionGroup.Rating.1', 'en', '1', 23157, NOW()),
('AuditOptionGroup.Rating.2', 'en', '2', 23157, NOW()),
('AuditOptionGroup.Rating.3', 'en', '3', 23157, NOW()),
('AuditOptionGroup.Rating.4', 'en', '4', 23157, NOW()),
('AuditOptionGroup.Rating.5', 'en', '5', 23157, NOW()),
('AuditOptionGroup.YesNoNA.NA', 'en', 'N/A', 23157, NOW()),
('AuditOptionGroup.YesNoNA.No', 'en', 'No', 23157, NOW()),
('AuditOptionGroup.YesNoNA.Yes', 'en', 'Yes', 23157, NOW()),
('AuditOptionGroup.name', 'en', 'Option Name', 23157, NOW()),
('AuditOptionGroup.name.fieldhelp', 'en', "A unique name.
<ul>
<li>Country</li>
<li>Yes/No</li>
</ul>", 23157, NOW()),
('AuditOptionGroup.radio', 'en', 'Radio', 23157, NOW()),
('AuditOptionGroup.radio.fieldhelp', 'en', 'Check this box if this option type is a radio field. Recommended for 3 or less values.', 23157, NOW()),
('AuditOptionGroup.uniqueCode', 'en', 'Unique Code', 23157, NOW()),
('AuditOptionGroup.uniqueCode.fieldhelp', 'en', 'A unique identifier for this option type.<br/><b>You can only define this field once!</b>', 23157, NOW()),
('AuditOptionValue.name', 'en', 'Answer', 23157, NOW()),
('AuditOptionValue.name.fieldhelp', 'en', 'This field will be used as the answer for an audit question', 23157, NOW()),
('AuditOptionValue.number', 'en', 'Number', 23157, NOW()),
('AuditOptionValue.number.fieldhelp', 'en', 'The display order of this option value', 23157, NOW()),
('AuditOptionValue.score', 'en', 'Score', 23157, NOW()),
('AuditOptionValue.score.fieldhelp', 'en', 'The score of this answer, used in scored audits', 23157, NOW()),
('AuditOptionValue.uniqueCode', 'en', 'Unique Code', 23157, NOW()),
('AuditOptionValue.uniqueCode.fieldhelp', 'en', "The unique code for this option value
<h5>Examples:</h5>
<ul>
<li>US</li>
<li>Low</li>
<li>Medium</li>
</ul>", 23157, NOW()),
('AuditOptionValue.visible', 'en', 'Visible', 23157, NOW()),
('AuditOptionValue.visible.fieldhelp', 'en', 'Whether this field is visible or not', 23157, NOW()),
('Country.AD', 'en', 'Andorra', 23157, NOW()),
('Country.AE', 'en', 'United Arab Emirates', 23157, NOW()),
('Country.AF', 'en', 'Afganistan', 23157, NOW()),
('Country.AG', 'en', 'Antigua and Barbuda', 23157, NOW()),
('Country.AI', 'en', 'Anguila', 23157, NOW()),
('Country.AL', 'en', 'Albania', 23157, NOW()),
('Country.AM', 'en', 'Armenia', 23157, NOW()),
('Country.AN', 'en', 'Netherlands Antilles', 23157, NOW()),
('Country.AO', 'en', 'Angola', 23157, NOW()),
('Country.AQ', 'en', 'Antarctica', 23157, NOW()),
('Country.AR', 'en', 'Argentina', 23157, NOW()),
('Country.AS', 'en', 'American Samoa', 23157, NOW()),
('Country.AT', 'en', 'Austria', 23157, NOW()),
('Country.AU', 'en', 'Australia', 23157, NOW()),
('Country.AW', 'en', 'Aruba', 23157, NOW()),
('Country.AX', 'en', 'Åland Islands', 23157, NOW()),
('Country.AZ', 'en', 'Azerbaijan', 23157, NOW()),
('Country.BA', 'en', 'Bosnia and Herzegovina', 23157, NOW()),
('Country.BB', 'en', 'Barbados', 23157, NOW()),
('Country.BD', 'en', 'Bangladesh', 23157, NOW()),
('Country.BE', 'en', 'Belgium', 23157, NOW()),
('Country.BF', 'en', 'Burkina Faso', 23157, NOW()),
('Country.BG', 'en', 'Bulgaria', 23157, NOW()),
('Country.BH', 'en', 'Bahrain', 23157, NOW()),
('Country.BI', 'en', 'Burundi', 23157, NOW()),
('Country.BJ', 'en', 'Benin', 23157, NOW()),
('Country.BL', 'en', 'Saint Barthélemy', 23157, NOW()),
('Country.BM', 'en', 'Bermuda', 23157, NOW()),
('Country.BN', 'en', 'Brunei Darussalam', 23157, NOW()),
('Country.BO', 'en', 'Bolivia', 23157, NOW()),
('Country.BR', 'en', 'Brazil', 23157, NOW()),
('Country.BS', 'en', 'Bahamas', 23157, NOW()),
('Country.BT', 'en', 'Bhutan', 23157, NOW()),
('Country.BV', 'en', 'Bouvet Island', 23157, NOW()),
('Country.BW', 'en', 'Botswana', 23157, NOW()),
('Country.BY', 'en', 'Belarus', 23157, NOW()),
('Country.BZ', 'en', 'Belize', 23157, NOW()),
('Country.CA', 'en', 'Canada', 23157, NOW()),
('Country.CC', 'en', 'Cocos (Keeling) Islands', 23157, NOW()),
('Country.CD', 'en', 'Congo, Democratic Republic of the', 23157, NOW()),
('Country.CF', 'en', 'Central African Republic', 23157, NOW()),
('Country.CG', 'en', 'Congo, Republic of the', 23157, NOW()),
('Country.CH', 'en', 'Switzerland', 23157, NOW()),
('Country.CI', 'en', 'Côte d''Ivoire', 23157, NOW()),
('Country.CK', 'en', 'Cook Islands', 23157, NOW()),
('Country.CL', 'en', 'Chile', 23157, NOW()),
('Country.CM', 'en', 'Cameroon', 23157, NOW()),
('Country.CN', 'en', 'China', 23157, NOW()),
('Country.CO', 'en', 'Colombia', 23157, NOW()),
('Country.CR', 'en', 'Costa Rica', 23157, NOW()),
('Country.CU', 'en', 'Cuba', 23157, NOW()),
('Country.CV', 'en', 'Cape Verde', 23157, NOW()),
('Country.CX', 'en', 'Christmas Island', 23157, NOW()),
('Country.CY', 'en', 'Cyprus', 23157, NOW()),
('Country.CZ', 'en', 'Czech Republic', 23157, NOW()),
('Country.DE', 'en', 'Germany', 23157, NOW()),
('Country.DJ', 'en', 'Djibouti', 23157, NOW()),
('Country.DK', 'en', 'Denmark', 23157, NOW()),
('Country.DM', 'en', 'Dominica', 23157, NOW()),
('Country.DO', 'en', 'Dominican Republic', 23157, NOW()),
('Country.DZ', 'en', 'Algeria', 23157, NOW()),
('Country.EC', 'en', 'Ecuador', 23157, NOW()),
('Country.EE', 'en', 'Estonia', 23157, NOW()),
('Country.EG', 'en', 'Egypt', 23157, NOW()),
('Country.EH', 'en', 'Western Sahara', 23157, NOW()),
('Country.ER', 'en', 'Eritrea', 23157, NOW()),
('Country.ES', 'en', 'Spain', 23157, NOW()),
('Country.ET', 'en', 'Ethiopia', 23157, NOW()),
('Country.FI', 'en', 'Finland', 23157, NOW()),
('Country.FJ', 'en', 'Fiji', 23157, NOW()),
('Country.FK', 'en', 'Falkland Islands', 23157, NOW()),
('Country.FM', 'en', 'Micronesia', 23157, NOW()),
('Country.FO', 'en', 'Faroe Islands', 23157, NOW()),
('Country.FR', 'en', 'France', 23157, NOW()),
('Country.GA', 'en', 'Gabon', 23157, NOW()),
('Country.GB', 'en', 'United Kingdom', 23157, NOW()),
('Country.GD', 'en', 'Grenada', 23157, NOW()),
('Country.GE', 'en', 'Georgia', 23157, NOW()),
('Country.GF', 'en', 'French Guiana', 23157, NOW()),
('Country.GG', 'en', 'Guernsey', 23157, NOW()),
('Country.GH', 'en', 'Ghana', 23157, NOW()),
('Country.GI', 'en', 'Gibraltar', 23157, NOW()),
('Country.GL', 'en', 'Greenland', 23157, NOW()),
('Country.GM', 'en', 'Gambia', 23157, NOW()),
('Country.GN', 'en', 'Guinea', 23157, NOW()),
('Country.GP', 'en', 'Guadeloupe', 23157, NOW()),
('Country.GQ', 'en', 'Equatorial Guinea', 23157, NOW()),
('Country.GR', 'en', 'Greece', 23157, NOW()),
('Country.GS', 'en', 'South Georgia & Sandwich Islands', 23157, NOW()),
('Country.GT', 'en', 'Guatemala', 23157, NOW()),
('Country.GU', 'en', 'Guam', 23157, NOW()),
('Country.GW', 'en', 'Guinea-Bissau', 23157, NOW()),
('Country.GY', 'en', 'Guyana', 23157, NOW()),
('Country.HK', 'en', 'Hong Kong', 23157, NOW()),
('Country.HM', 'en', 'Heard Island and McDonald Islands', 23157, NOW()),
('Country.HN', 'en', 'Honduras', 23157, NOW()),
('Country.HR', 'en', 'Croatia', 23157, NOW()),
('Country.HT', 'en', 'Haiti', 23157, NOW()),
('Country.HU', 'en', 'Hungary', 23157, NOW()),
('Country.ID', 'en', 'Indonesia', 23157, NOW()),
('Country.IE', 'en', 'Ireland', 23157, NOW()),
('Country.IL', 'en', 'Israel', 23157, NOW()),
('Country.IM', 'en', 'Isle of Man', 23157, NOW()),
('Country.IN', 'en', 'India', 23157, NOW()),
('Country.IO', 'en', 'British Indian Ocean Territory', 23157, NOW()),
('Country.IQ', 'en', 'Iraq', 23157, NOW()),
('Country.IR', 'en', 'Iran', 23157, NOW()),
('Country.IS', 'en', 'Iceland', 23157, NOW()),
('Country.IT', 'en', 'Italy', 23157, NOW()),
('Country.JE', 'en', 'Jersey', 23157, NOW()),
('Country.JM', 'en', 'Jamaica', 23157, NOW()),
('Country.JO', 'en', 'Jordan', 23157, NOW()),
('Country.JP', 'en', 'Japan', 23157, NOW()),
('Country.KE', 'en', 'Kenya', 23157, NOW()),
('Country.KG', 'en', 'Kyrgyzstan', 23157, NOW()),
('Country.KH', 'en', 'Cambodia', 23157, NOW()),
('Country.KI', 'en', 'Kiribati', 23157, NOW()),
('Country.KM', 'en', 'Comoros', 23157, NOW()),
('Country.KN', 'en', 'Saint Kitts and Nevis', 23157, NOW()),
('Country.KP', 'en', 'Korea (North)', 23157, NOW()),
('Country.KR', 'en', 'Korea (South)', 23157, NOW()),
('Country.KW', 'en', 'Kuwait', 23157, NOW()),
('Country.KY', 'en', 'Cayman Islands', 23157, NOW()),
('Country.KZ', 'en', 'Kazakhstan', 23157, NOW()),
('Country.LA', 'en', 'Laos', 23157, NOW()),
('Country.LB', 'en', 'Lebanon', 23157, NOW()),
('Country.LC', 'en', 'Saint Lucia', 23157, NOW()),
('Country.LI', 'en', 'Liechtenstein', 23157, NOW()),
('Country.LK', 'en', 'Sri Lanka', 23157, NOW()),
('Country.LR', 'en', 'Liberia', 23157, NOW()),
('Country.LS', 'en', 'Lesotho', 23157, NOW()),
('Country.LT', 'en', 'Lithuania', 23157, NOW()),
('Country.LU', 'en', 'Luxembourg', 23157, NOW()),
('Country.LV', 'en', 'Latvia', 23157, NOW()),
('Country.LY', 'en', 'Libya', 23157, NOW()),
('Country.MA', 'en', 'Morocco', 23157, NOW()),
('Country.MC', 'en', 'Monaco', 23157, NOW()),
('Country.MD', 'en', 'Moldova', 23157, NOW()),
('Country.ME', 'en', 'Montenegro', 23157, NOW()),
('Country.MF', 'en', 'Saint Martin', 23157, NOW()),
('Country.MG', 'en', 'Madagascar', 23157, NOW()),
('Country.MH', 'en', 'Marshall Islands', 23157, NOW()),
('Country.MK', 'en', 'Macedonia', 23157, NOW()),
('Country.ML', 'en', 'Mali', 23157, NOW()),
('Country.MM', 'en', 'Myanmar', 23157, NOW()),
('Country.MN', 'en', 'Mongolia', 23157, NOW()),
('Country.MO', 'en', 'Macao', 23157, NOW()),
('Country.MP', 'en', 'Northern Mariana Islands', 23157, NOW()),
('Country.MQ', 'en', 'Martinique', 23157, NOW()),
('Country.MR', 'en', 'Mauritania', 23157, NOW()),
('Country.MS', 'en', 'Montserrat', 23157, NOW()),
('Country.MT', 'en', 'Malta', 23157, NOW()),
('Country.MU', 'en', 'Mauritius', 23157, NOW()),
('Country.MV', 'en', 'Maldives', 23157, NOW()),
('Country.MW', 'en', 'Malawi', 23157, NOW()),
('Country.MX', 'en', 'Mexico', 23157, NOW()),
('Country.MY', 'en', 'Malaysia', 23157, NOW()),
('Country.MZ', 'en', 'Mozambique', 23157, NOW()),
('Country.NA', 'en', 'Namibia', 23157, NOW()),
('Country.NC', 'en', 'New Caledonia', 23157, NOW()),
('Country.NE', 'en', 'Niger', 23157, NOW()),
('Country.NF', 'en', 'Norfolk Island', 23157, NOW()),
('Country.NG', 'en', 'Nigeria', 23157, NOW()),
('Country.NI', 'en', 'Nicaragua', 23157, NOW()),
('Country.NL', 'en', 'Netherlands', 23157, NOW()),
('Country.NO', 'en', 'Norway', 23157, NOW()),
('Country.NP', 'en', 'Nepal', 23157, NOW()),
('Country.NR', 'en', 'Nauru', 23157, NOW()),
('Country.NU', 'en', 'Niue', 23157, NOW()),
('Country.NZ', 'en', 'New Zealand', 23157, NOW()),
('Country.OM', 'en', 'Oman', 23157, NOW()),
('Country.PA', 'en', 'Panama', 23157, NOW()),
('Country.PE', 'en', 'Peru', 23157, NOW()),
('Country.PF', 'en', 'French Polynesia', 23157, NOW()),
('Country.PG', 'en', 'Papua New Guinea', 23157, NOW()),
('Country.PH', 'en', 'Philippines', 23157, NOW()),
('Country.PK', 'en', 'Pakistan', 23157, NOW()),
('Country.PL', 'en', 'Poland', 23157, NOW()),
('Country.PM', 'en', 'Saint Pierre and Miquelon', 23157, NOW()),
('Country.PN', 'en', 'Pitcairn', 23157, NOW()),
('Country.PR', 'en', 'Puerto Rico', 23157, NOW()),
('Country.PS', 'en', 'Palestinian Territories', 23157, NOW()),
('Country.PT', 'en', 'Portugal', 23157, NOW()),
('Country.PW', 'en', 'Palau', 23157, NOW()),
('Country.PY', 'en', 'Paraguay', 23157, NOW()),
('Country.QA', 'en', 'Qatar', 23157, NOW()),
('Country.RE', 'en', 'Réunion', 23157, NOW()),
('Country.RO', 'en', 'Romania', 23157, NOW()),
('Country.RS', 'en', 'Serbia', 23157, NOW()),
('Country.RU', 'en', 'Russia', 23157, NOW()),
('Country.RW', 'en', 'Rwanda', 23157, NOW()),
('Country.SA', 'en', 'Saudi Arabia', 23157, NOW()),
('Country.SB', 'en', 'Solomon Islands', 23157, NOW()),
('Country.SC', 'en', 'Seychelles', 23157, NOW()),
('Country.SD', 'en', 'Sudan', 23157, NOW()),
('Country.SE', 'en', 'Sweden', 23157, NOW()),
('Country.SG', 'en', 'Singapore', 23157, NOW()),
('Country.SH', 'en', 'Saint Helena', 23157, NOW()),
('Country.SI', 'en', 'Slovenia', 23157, NOW()),
('Country.SJ', 'en', 'Svalbard and Jan Mayen', 23157, NOW()),
('Country.SK', 'en', 'Slovakia', 23157, NOW()),
('Country.SL', 'en', 'Sierra Leone', 23157, NOW()),
('Country.SM', 'en', 'San Marino', 23157, NOW()),
('Country.SN', 'en', 'Senegal', 23157, NOW()),
('Country.SO', 'en', 'Somalia', 23157, NOW()),
('Country.SR', 'en', 'Suriname', 23157, NOW()),
('Country.ST', 'en', 'Sao Tome and Principe', 23157, NOW()),
('Country.SV', 'en', 'El Salvador', 23157, NOW()),
('Country.SY', 'en', 'Syrian Arab Republic', 23157, NOW()),
('Country.SZ', 'en', 'Swaziland', 23157, NOW()),
('Country.TC', 'en', 'Turks and Caicos Islands', 23157, NOW()),
('Country.TD', 'en', 'Chad', 23157, NOW()),
('Country.TF', 'en', 'French Southern Territories', 23157, NOW()),
('Country.TG', 'en', 'Togo', 23157, NOW()),
('Country.TH', 'en', 'Thailand', 23157, NOW()),
('Country.TJ', 'en', 'Tajikistan', 23157, NOW()),
('Country.TK', 'en', 'Tokelau', 23157, NOW()),
('Country.TL', 'en', 'Timor-Leste', 23157, NOW()),
('Country.TM', 'en', 'Turkmenistan', 23157, NOW()),
('Country.TN', 'en', 'Tunisia', 23157, NOW()),
('Country.TO', 'en', 'Tonga', 23157, NOW()),
('Country.TR', 'en', 'Turkey', 23157, NOW()),
('Country.TT', 'en', 'Trinidad and Tobago', 23157, NOW()),
('Country.TV', 'en', 'Tuvalu', 23157, NOW()),
('Country.TW', 'en', 'Taiwan', 23157, NOW()),
('Country.TZ', 'en', 'Tanzania', 23157, NOW()),
('Country.UA', 'en', 'Ukraine', 23157, NOW()),
('Country.UG', 'en', 'Uganda', 23157, NOW()),
('Country.UM', 'en', 'United States Minor Islands', 23157, NOW()),
('Country.US', 'en', 'United States', 23157, NOW()),
('Country.UY', 'en', 'Uruguay', 23157, NOW()),
('Country.UZ', 'en', 'Uzbekistan', 23157, NOW()),
('Country.VA', 'en', 'Holy See', 23157, NOW()),
('Country.VC', 'en', 'Saint Vincent and the Grenadines', 23157, NOW()),
('Country.VE', 'en', 'Venezuela', 23157, NOW()),
('Country.VG', 'en', 'British Virgin Islands', 23157, NOW()),
('Country.VI', 'en', 'United States Virgin Islands', 23157, NOW()),
('Country.VN', 'en', 'Vietnam', 23157, NOW()),
('Country.VU', 'en', 'Vanuatu', 23157, NOW()),
('Country.WF', 'en', 'Wallis and Futuna', 23157, NOW()),
('Country.WS', 'en', 'Samoa', 23157, NOW()),
('Country.YE', 'en', 'Yemen', 23157, NOW()),
('Country.YT', 'en', 'Mayotte', 23157, NOW()),
('Country.ZA', 'en', 'South Africa', 23157, NOW()),
('Country.ZM', 'en', 'Zambia', 23157, NOW()),
('Country.ZW', 'en', 'Zimbabwe', 23157, NOW()),
('LowMedHigh.High', 'en', 'High', 23157, NOW()),
('LowMedHigh.Low', 'en', 'Low', 23157, NOW()),
('LowMedHigh.Medium', 'en', 'Medium', 23157, NOW()),
('ManageOptionType.button.SaveNew', 'en', 'Save New', 23157, NOW()),
('State.AB', 'en', 'Alberta', 23157, NOW()),
('State.AK', 'en', 'Alaska', 23157, NOW()),
('State.AL', 'en', 'Alabama', 23157, NOW()),
('State.AR', 'en', 'Arkansas', 23157, NOW()),
('State.AS', 'en', 'American Samoa', 23157, NOW()),
('State.AZ', 'en', 'Arizona', 23157, NOW()),
('State.BC', 'en', 'British Columbia', 23157, NOW()),
('State.CA', 'en', 'California', 23157, NOW()),
('State.CO', 'en', 'Colorado', 23157, NOW()),
('State.CT', 'en', 'Connecticut', 23157, NOW()),
('State.DC', 'en', 'District of Columbia', 23157, NOW()),
('State.DE', 'en', 'Delaware', 23157, NOW()),
('State.FL', 'en', 'Florida', 23157, NOW()),
('State.GA', 'en', 'Georgia', 23157, NOW()),
('State.GU', 'en', 'Guam', 23157, NOW()),
('State.HI', 'en', 'Hawaii', 23157, NOW()),
('State.IA', 'en', 'Iowa', 23157, NOW()),
('State.ID', 'en', 'Idaho', 23157, NOW()),
('State.IL', 'en', 'Illinois', 23157, NOW()),
('State.IN', 'en', 'Indiana', 23157, NOW()),
('State.KS', 'en', 'Kansas', 23157, NOW()),
('State.KY', 'en', 'Kentucky', 23157, NOW()),
('State.LA', 'en', 'Louisiana', 23157, NOW()),
('State.MA', 'en', 'Massachusetts', 23157, NOW()),
('State.MB', 'en', 'Manitoba', 23157, NOW()),
('State.MD', 'en', 'Maryland', 23157, NOW()),
('State.ME', 'en', 'Maine', 23157, NOW()),
('State.MI', 'en', 'Michigan', 23157, NOW()),
('State.MN', 'en', 'Minnesota', 23157, NOW()),
('State.MO', 'en', 'Missouri', 23157, NOW()),
('State.MP', 'en', 'Northern Mariana Islands', 23157, NOW()),
('State.MS', 'en', 'Mississippi', 23157, NOW()),
('State.MT', 'en', 'Montana', 23157, NOW()),
('State.NB', 'en', 'New Brunswick', 23157, NOW()),
('State.NC', 'en', 'North Carolina', 23157, NOW()),
('State.ND', 'en', 'North Dakota', 23157, NOW()),
('State.NE', 'en', 'Nebraska', 23157, NOW()),
('State.NH', 'en', 'New Hampshire', 23157, NOW()),
('State.NJ', 'en', 'New Jersey', 23157, NOW()),
('State.NL', 'en', 'Newfoundland and Labrador', 23157, NOW()),
('State.NM', 'en', 'New Mexico', 23157, NOW()),
('State.NS', 'en', 'Nova Scotia', 23157, NOW()),
('State.NT', 'en', 'Northwest Territories', 23157, NOW()),
('State.NU', 'en', 'Nunavut', 23157, NOW()),
('State.NV', 'en', 'Nevada', 23157, NOW()),
('State.NY', 'en', 'New York', 23157, NOW()),
('State.OH', 'en', 'Ohio', 23157, NOW()),
('State.OK', 'en', 'Oklahoma', 23157, NOW()),
('State.ON', 'en', 'Ontario', 23157, NOW()),
('State.OR', 'en', 'Oregon', 23157, NOW()),
('State.PA', 'en', 'Pennsylvania', 23157, NOW()),
('State.PE', 'en', 'Prince Edward Island', 23157, NOW()),
('State.PR', 'en', 'Puerto Rico', 23157, NOW()),
('State.QC', 'en', 'Quebec', 23157, NOW()),
('State.RI', 'en', 'Rhode Island', 23157, NOW()),
('State.SC', 'en', 'South Carolina', 23157, NOW()),
('State.SD', 'en', 'South Dakota', 23157, NOW()),
('State.SK', 'en', 'Saskatchewan', 23157, NOW()),
('State.TN', 'en', 'Tennessee', 23157, NOW()),
('State.TX', 'en', 'Texas', 23157, NOW()),
('State.UM', 'en', 'United States Minor Outlying Islands', 23157, NOW()),
('State.UT', 'en', 'Utah', 23157, NOW()),
('State.VA', 'en', 'Virginia', 23157, NOW()),
('State.VI', 'en', 'Virgin Islands, U.S.', 23157, NOW()),
('State.VT', 'en', 'Vermont', 23157, NOW()),
('State.WA', 'en', 'Washington', 23157, NOW()),
('State.WI', 'en', 'Wisconsin', 23157, NOW()),
('State.WV', 'en', 'West Virginia', 23157, NOW()),
('State.WY', 'en', 'Wyoming', 23157, NOW()),
('State.YT', 'en', 'Yukon', 23157, NOW()),
('YesNo.No', 'en', 'No', 23157, NOW()),
('YesNo.Yes', 'en', 'Yes', 23157, NOW());

-- Update audit_questions
update audit_question set optionID = 25, questionType = 'MultipleChoice' where questionType = 'Yes/No';
update audit_question set optionID = 26, questionType = 'MultipleChoice' where questionType = 'Yes/No/NA';
update audit_question set optionID = 27, questionType = 'MultipleChoice' where questionType = 'Country';
update audit_question set optionID = 28, questionType = 'MultipleChoice' where questionType = 'State';
update audit_question set optionID = 31, questionType = 'MultipleChoice' where questionType = 'Rating 1-5';
-- Individual conversions
update audit_question set optionID = 13, questionType = 'MultipleChoice' where id = 63;
update audit_question set optionID = 33, questionType = 'MultipleChoice' where id = 125;
update audit_question set optionID = 22, questionType = 'MultipleChoice' where id in (3536, 3844);
update audit_question set optionID = 20, questionType = 'MultipleChoice' where id in (2444);
update audit_question set optionID = 21, questionType = 'MultipleChoice' where id in (2949);
update audit_question set optionID = 23, questionType = 'MultipleChoice' where id in (4127);
update audit_question set optionID = 24, questionType = 'MultipleChoice' where id in (4166);
update audit_question set optionID = 19, questionType = 'MultipleChoice' where id in (2092, 2093, 2094, 2095, 2096, 2097);
update audit_question set optionID = 32, questionType = 'MultipleChoice' where id in (2244);
update audit_question set optionID = 39, questionType = 'MultipleChoice' where id in (7013);
update audit_question set optionID = 35, questionType = 'MultipleChoice' where id in (7422);
update audit_question set optionID = 37, questionType = 'MultipleChoice' where id in (7438);
update audit_question set optionID = 36, questionType = 'MultipleChoice' where id in (7448);
update audit_question set optionID = 39, questionType = 'MultipleChoice' where id in (6999,7003,7008,7011,7017,7027,7030,7035,7037,7038,7045,7046,7056,7064,7069,7070,7076,7083,7088,7091,7093,7107);
update audit_question set optionID = 40, questionType = 'MultipleChoice' where id in (7727);

-- Final DDL Changes
ALTER TABLE `audit_category`
	DROP COLUMN `name`,
	DROP KEY `auditTypeCategory`,
	ADD KEY `auditTypeCategory`(`auditTypeID`,`parentID`),
	DROP KEY `auditTypeID`;

ALTER TABLE `audit_type` 
	DROP COLUMN `auditName`, 
	DROP KEY `auditName`;

ALTER TABLE `accounts` 
	DROP COLUMN `industry`, 
	DROP COLUMN `industryID`;

ALTER TABLE `audit_question` 
	DROP COLUMN `name`;

DROP TABLE `audit_question_option`;
-- PICS-2336
insert into widget (caption, widgetType, synchronous, url)
values ('Submitted Import PQF Audits', 'Html', 0, 'SubmittedImportPQFAuditsAjax.action');
insert into widget_user (widgetID, userID, expanded, `column`, sortOrder)
values (35, 959, 1, 1, 40);

-- audit_question -> app_translation
INSERT INTO pics_alpha1.app_translation(msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate)
  SELECT CONCAT('AuditQuestion.', aq.id, '.name') AS msgKey, 'en' AS locale, aq.name AS msgValue, 33884 AS createdBy,
         33884 AS updatedBy, now() AS creationDate, now() AS updateDate
  FROM   pics_alpha1.audit_question aq
  WHERE  aq.expirationDate > now();
  
-- PICS-2211
insert into flag_criteria (category, displayOrder, label, description, dataType, comparison, defaultValue, allowCustomValue, oshaType, oshaRateType, multiYearScope, insurance, flaggableWhenMissing, createdBy, creationDate, updatedBy, updateDate)
values ('Statistics', 130, 'Severity Rate Average', 'Severity Rate three year average must be less than or equal to {HURDLE}', 'number', '>', 1.0, 1, 'OSHA', 'SeverityRate', 'ThreeYearAverage', 0, 0, 23157, NOW(), 23157, NOW());

-- DROP Services Performed
DELETE from pqfdata
where questionID IN (
	select q.id from audit_question q where q.questionType IN ('Service','Main Work','Industry') OR q.categoryID IN (423,1197)
);

DELETE from pqfdata where questionID = 870;
