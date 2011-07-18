-- PICS-2787
update app_translation 
set msgValue = replace(msgValue,'(US)','(US/Canada)')
where msgKey like '%TimeZone%'
and (msgValue like '%Pacific%(US)%'
or msgValue like '%Mountain%(US)%'
or msgValue like '%Central%(US)%'
or msgValue like '%Eastern%(US)%');

update users
set timezone = replace(timezone, 'Canada', 'US')
where timezone like 'Canada/Central%'
or timezone like 'Canada/Eastern%'
or timezone like 'Canada/Mountain%'
or timezone like 'Canada/Pacific%';

update users
set timezone = 'America/Port-au-Prince'
where timezone = 'US/East-Indiana'
or timezone = 'US/Indiana-Starke';

update users
set timezone = 'US/Pacific'
where timezone = 'US/Pacific-NEW'
or timezone = 'Canada/Yukon';

update users
set timezone = 'US/Eastern'
where timezone = 'US/Michigan';

update users
set timezone = 'Atlantic/Bermuda'
where timezone = 'Canada/Atlantic';

update users
set timezone = 'Pacific/Honolulu'
where timezone = 'US/Hawaii';

update users
set timezone = 'Canada/Saskatchewan'
where timezone = 'Canada/East-Saskatchewan';


update accounts
set timezone = replace(timezone, 'Canada', 'US')
where timezone like 'Canada/Central%'
or timezone like 'Canada/Eastern%'
or timezone like 'Canada/Mountain%'
or timezone like 'Canada/Pacific%';

update accounts
set timezone = 'America/Port-au-Prince'
where timezone = 'US/East-Indiana'
or timezone = 'US/Indiana-Starke';

update accounts
set timezone = 'US/Pacific'
where timezone = 'US/Pacific-NEW'
or timezone = 'Canada/Yukon';

update accounts
set timezone = 'US/Eastern'
where timezone = 'US/Michigan';

update accounts
set timezone = 'Atlantic/Bermuda'
where timezone = 'Canada/Atlantic';

update accounts
set timezone = 'Pacific/Honolulu'
where timezone = 'US/Hawaii';

-- PICS-2848
update invoice_fee fee set fee.minFacilities = 1 where fee.id = 340;
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
	values
	(null, 
	'Data Import Fee for 0 Operators', 
	0.00, 
	0, 
	'ImportFee', 
	0, 
	0, 
	'FVEN017', 
	20952, 
	20952, 
	now(), 
	now(), 
	41
	);

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
