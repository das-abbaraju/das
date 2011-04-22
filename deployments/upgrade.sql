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
where t.id is NULL;
--