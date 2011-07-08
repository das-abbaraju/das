-- PICS-2787
update app_translation 
set msgValue = replace(msgValue,'(US)','(US/Canada)')
where msgKey like '%TimeZone%'
and (msgValue like '%Pacific%(US)%'
or msgValue like '%Mountain%(US)%'
or msgValue like '%Central%(US)%'
or msgValue like '%Eastern%(US)%');

-- PICS-2287
insert into app_translation 
	(msgKey, 
	locale, 
	msgValue, 
	createdBy, 
	updatedBy, 
	creationDate, 
	updateDate
	)
	values
	('operatorFlagMatrix.messageText', 
	'en', 
	'This matrix displays all contractors that have been flagged for a given criteria. </br> If a criteria is not shown, that is because there are no contractors for the given criteria who have been flagged</br>', 
	1, 
	1, 
	now(), 
	now()
	);
