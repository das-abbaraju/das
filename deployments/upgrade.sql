-- PICS-1038/PICS-1055
-- insert into invoice_fee (id, fee, defaultAmount, visible, feeClass, qbFullName, createdBy, updatedBy, creationDate, updateDate)
--	values (12, 'PICS UAE Membership for PQF-Only', 500.00, 1, 'Membership', 'PQF-Only', 20952, 20952, now(), NOW()),
--	(13, 'PICS UAE Membership for 1 Operator', 2000.00, 1, 'Membership', '1 Operator', 20952, 20952, now(), NOW()),
--	(14, 'PICS UAE Membership for 2-4 Operators', 3000.00, 1, 'Membership', '2-4 Operators', 20952, 20952, now(), NOW()),
--	(15, 'PICS UAE Membership for 5-8 Operators', 4500.00, 1, 'Membership', '5-8 Operators', 20952, 20952, now(), NOW()),
--	(16, 'PICS UAE Membership for 9-12 Operators', 6000.00, 1, 'Membership', '9-12 Operators', 20952, 20952, now(), NOW()),
--	(17, 'PICS UAE Membership for 13-19 Operators', 8000.00, 1, 'Membership', '13-19 Operators', 20952, 20952, now(), NOW()),
--	(18, 'PICS UAE Membership for 20-49 Operators', 11000.00, 1, 'Membership', '20-49 Operators', 20952, 20952, now(), NOW()),
--	(19, 'PICS UAE Membership for 50+ Operators', 15000.00, 1, 'Membership', '50 Operators', 20952, 20952, now(), NOW());
-- END

--update flag_criteria_operator set criteriaID = 634 
--where criteriaID = 469 and opid not in (13656);
---- PICS-1575, PICS-1461

-- PICS-1694 - Moving manual audit rules back to a matrix table
insert into audit_cat_matrix  (categoryID, tableType, foreignKeyID, createdBy, creationDate) 
select distinct catID, 'D', questionID, 23157, NOW()
from audit_category_rule 
where auditTypeID = 2  and catID is not null  and questionID is not null;

update widget set chartType = 'Bar2D' where widgetID = 5;

-- PICS-1797 - Use a better label for annual stats
update flag_criteria set label = REPLACE(label, right(label, 3), '') where label like '% \'%';
