-- update applies for expired audit categroy 472
UPDATE audit_cat_data acd
join contractor_audit ca on acd.auditID=ca.id
join contractor_audit_operator cao on cao.auditID=ca.id
Set acd.applies=0
where acd.categoryID=472
AND acd.applies = 1
AND (ca.expiresDate is null or ca.expiresDate > NOW())
AND cao.visible=1;