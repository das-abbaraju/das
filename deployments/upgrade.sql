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
	
--PICS-2177 Pulled out email template
insert into `email_template` (`id`, `accountID`, `templateName`, `subject`, `body`, `createdBy`, `creationDate`, `updatedBy`, `updateDate`, `listType`, `allowsVelocity`, `html`, `recipient`) values('165','1100','Unsuccessful in having request register','Unsuccessful in having ${newContractor.name} register at PICS','Hi ${con.contact},\n\nIt was great to talk with you today. As we discussed, ${op_contact.name} asked me to\nplease have you register in ${op.name}’s contractor database for upcoming work.\n\nPlease see ${op_contact.name}’s request below:\n\n${con.reasonForRegistration}\n\nAll ${op.name} contractors are required to prequalify before doing work for ${op.name}.\nYour firm has been requested to be placed in the system before ${deadline}.\n\nI have cc’d ${op_contact.name} on this email, to notify ${op.name} that you have declined\nto be a part of ${op.name}’s contractor database at this time, and will not be qualified for\nupcoming work.\n\nIf you would still like to be considered for upcoming work, I have listed the registration\nlink below, and can help walk you through the process:\n\n${link}\n\nThe site you should select when you are at the phase of selecting a facility is the following:\n\n${op.name}\n\nPlease feel free to contact me directly should you have any questions.\n\nThank you,\n\nCatherine Gutierrez\nPICS, Inc.\nMarketing Manager \nTel: (949) 936-4555 \nEmail: CGutierrez@PICSAuditing.com','23157','2011-07-05 15:38:03','38492','2011-07-19 14:12:37','Audit','1','1','undefined');
--PICS-2758 Email templated was created on live, just need to update allowsVelocity
UPDATE email_template SET allowsVelocity = 1 where id = 163;
	
