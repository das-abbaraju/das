-- PICS-2254
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
values
(NULL, 'AuditCategory.limits.name', 'en', 'Policy Limits', 20952, 20952, NOW(), NOW(), NULL),
(NULL, 'AuditCategory.policyInformation.name', 'en', 'Policy Information', 20952, 20952, NOW(), NOW(), NULL);
--

-- PICS-2332
update invoice_fee set fee = 'Listed Account Fee' where id = 100;
update invoice_item ii set ii.paymentExpires = date_add(ii.paymentExpires, interval 9 month) where ii.feeID = 100;
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