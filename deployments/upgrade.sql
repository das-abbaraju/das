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

