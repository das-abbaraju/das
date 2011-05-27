-- Copied all these tables from LIVE to Alpha2 (5/27)
app_translation audit_category audit_category_rule audit_question audit_type audit_type_rule pqfoptions workflow workflow_step
-- Need to copy this all back to Live when we release
ref_trade ref_trade_alt

-- Already ran this on Alpha2, don't run again
insert into pics_alpha2.app_translation
select null, 
	t1.msgKey, 
	t1.locale, 
	t1.msgValue, 
	t1.createdBy, 
	t1.updatedBy, 
	t1.creationDate, 
	t1.updateDate, 
	t1.lastUsed
from pics_alpha1.app_translation t1
LEFT JOIN pics_alpha2.app_translation t2 ON t1.msgKey = t2.msgKey AND t1.locale = t2.locale
where t2.id IS NULL
-- AND t1.msgKey NOT LIKE 'Trade.%'
AND t1.msgKey NOT LIKE 'AuditCategory.%'
AND t1.msgKey NOT LIKE 'AuditQuestion.%'
AND t1.msgKey NOT LIKE 'AuditQuestionOption.%'
AND t1.msgKey NOT LIKE 'AuditType.%'
AND t1.locale = 'en'
AND t1.msgValue != 'Translation missing'
AND t1.msgValue = t2.msgValue
;

-- Move over the ref trades somehow
SET foreign_key_checks = 0;
INSERT INTO pics_alpha2.ref_trade
SELECT * FROM pics_alpha1.ref_trade;

INSERT INTO pics_alpha2.ref_trade_alt
SELECT * FROM pics_alpha1.ref_trade_alt;
SET foreign_key_checks = 1;

-- PICS-2254
DELETE FROM app_translation WHERE msgKey LIKE 'AuditCategory.%';

insert into app_translation 
	(id, 
	msgKey, 
	locale, 
	msgValue, 
	createdBy, 
	updatedBy, 
	creationDate, 
	updateDate, 
	lastUsed
	)
select null, concat('AuditCategory.',ac.id,'.name'), 'en', ac.name, 20952, 20952, now(), now(), null from audit_category ac
left join app_translation t on concat('AuditCategory.',ac.id,'.name') = t.msgKey
where t.id is NULL and ac.name not in ('Policy Limits','Policy Information');

update audit_category ac set ac.uniqueCode = 'limits' where ac.name = 'Policy Limits';
update audit_category ac set ac.uniqueCode = 'policyInformation' where ac.name = 'Policy Information';

insert into app_translation 
	(msgKey, 
	locale, 
	msgValue, 
	createdBy, 
	updatedBy, 
	creationDate, 
	updateDate, 
	lastUsed
	)
values
('AuditCategory.limits.name', 'en', 'Policy Limits', 20952, 20952, NOW(), NOW(), NULL),
('AuditCategory.policyInformation.name', 'en', 'Policy Information', 20952, 20952, NOW(), NOW(), NULL);
--

-- PICS-2332
update invoice_fee set fee = 'List Only Account Fee' where id = 100;
update invoice_item ii set ii.paymentExpires = date_add(ii.paymentExpires, interval 9 month) where ii.feeID = 100;
--

-- PICS-1639
update invoice_fee invf set invf.minFacilities = -1, invf.maxFacilities = -1, invf.feeClass = 'Deprecated', invf.visible = 0, invf.fee = concat('Old ',invf.fee) where invf.id in (1,2,4,5,6,7,8,9,10,11,50,51,52,54,55,100,101,104,105);
update invoice_fee invf set invf.feeClass = 'Misc' where invf.feeClass = 'Other';
update invoice_fee invf set invf.feeClass = 'Activation' where invf.id in (1,104);
update invoice_fee invf set invf.feeClass = 'DocuGUARD' where invf.id = 4;
update invoice_fee invf set invf.feeClass = 'AuditGUARD' where invf.id in (5,6,7,8,9,10,11,105);
update invoice_fee invf set invf.feeClass = 'ListOnly' where invf.id = 100;
update invoice_fee invf set invf.feeClass = 'GST' where invf.id = 200;

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
values 	(299, 'List Only 0 Operators', 0.00, 1, 'ListOnly', 0, 0, 'LTVEN0', 20952, 20952, now(), now(), 1),
	(300, 'List Only', 25.00, 1, 'ListOnly', 1, 10000, 'LTVEN25', 20952, 20952, now(), now(), 1),
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
select null, c.id, 'ListOnly', 
  case when c.newMembershipLevelID = 100 then 300 else 299 end,
  case when c.membershipLevelID = 100 then 300 else 299 end,
  20952, 20952, now(), now() from contractor_info c;
--

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
--

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
