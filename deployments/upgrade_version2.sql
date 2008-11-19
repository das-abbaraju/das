-- ON LIVE BEFORE
delete from contractor_audit
where auditTypeID = 11;

insert into contractor_audit (createdDate, auditFor, auditTypeID, conID, auditStatus, auditorID, completedDate, closedDate, manuallyAdded)
select '2007-12-31', '2007', 11, conID, auditStatus, auditorID, completedDate, closedDate, 0
from contractor_audit ca
where auditTypeID = 1;

insert into contractor_audit (createdDate, auditFor, auditTypeID, conID, auditStatus, auditorID, completedDate, closedDate, manuallyAdded)
select '2006-12-31', '2006', 11, conID, auditStatus, auditorID, completedDate, closedDate, 0
from contractor_audit ca
where auditTypeID = 1;

insert into contractor_audit (createdDate, auditFor, auditTypeID, conID, auditStatus, auditorID, completedDate, closedDate, manuallyAdded)
select '2005-12-31', '2005', 11, conID, auditStatus, auditorID, completedDate, closedDate, 0
from contractor_audit ca
where auditTypeID = 1;



insert into contractor_audit (createdDate, auditFor, auditTypeID, conID, auditStatus)
select distinct '2004-12-31', '2004', 11, conID, 'Expired'
from osha where manHours4 > 0;

insert into contractor_audit (createdDate, auditFor, auditTypeID, conID, auditStatus)
select distinct '2003-12-31', '2003', 11, conID, 'Expired'
from osha where manHours5 > 0;

insert into contractor_audit (createdDate, auditFor, auditTypeID, conID, auditStatus)
select distinct '2002-12-31', '2002', 11, conID, 'Expired'
from osha where manHours6 > 0;

insert into contractor_audit (createdDate, auditFor, auditTypeID, conID, auditStatus)
select distinct '2001-12-31', '2001', 11, conID, 'Expired'
from osha where manHours7 > 0;


update contractor_audit
set expiresDate = ADDDATE(ADDDATE(createdDate, INTERVAL 1 DAY), INTERVAL 38 MONTH)
where auditTypeID = 11;


insert into osha_audit (auditID, SHAType, location, description, auditorID, 
verifiedDate, applicable, fileUploaded, manHours, fatalities, lostWorkCases, 
lostWorkDays, injuryIllnessCases, restrictedWorkCases, recordableTotal, comment)
select ca.auditID, SHAType, location, description, osha.auditorID, 
verifiedDate1, case NA1 when 'Yes' then 0 else 1 end, case file1YearAgo when 'Yes' then 1 else 0 end, manHours1, fatalities1, lostWorkCases1, 
lostWorkDays1, injuryIllnessCases1, restrictedWorkCases1, recordableTotal1, comment1
from osha
join contractor_audit ca on ca.conID = osha.conID and auditTypeID = 11
where auditFor = '2007' and manHours1 > 0;

insert into osha_audit (auditID, SHAType, location, description, auditorID, 
verifiedDate, applicable, fileUploaded, manHours, fatalities, lostWorkCases, 
lostWorkDays, injuryIllnessCases, restrictedWorkCases, recordableTotal, comment)
select ca.auditID, SHAType, location, description, osha.auditorID, 
verifiedDate2, case NA2 when 'Yes' then 0 else 1 end, case file2YearAgo when 'Yes' then 1 else 0 end, manHours2, fatalities2, lostWorkCases2, 
lostWorkDays2, injuryIllnessCases2, restrictedWorkCases2, recordableTotal2, comment2
from osha
join contractor_audit ca on ca.conID = osha.conID and auditTypeID = 11
where auditFor = '2006' and manHours2 > 0;

insert into osha_audit (auditID, SHAType, location, description, auditorID, 
verifiedDate, applicable, fileUploaded, manHours, fatalities, lostWorkCases, 
lostWorkDays, injuryIllnessCases, restrictedWorkCases, recordableTotal, comment)
select ca.auditID, SHAType, location, description, osha.auditorID, 
verifiedDate3, case NA3 when 'Yes' then 0 else 1 end, case file3YearAgo when 'Yes' then 1 else 0 end, manHours3, fatalities3, lostWorkCases3, 
lostWorkDays3, injuryIllnessCases3, restrictedWorkCases3, recordableTotal3, comment3
from osha
join contractor_audit ca on ca.conID = osha.conID and auditTypeID = 11
where auditFor = '2005' and manHours3 > 0;


insert into osha_audit (auditID, SHAType, location, description,
applicable, fileUploaded, manHours, fatalities, lostWorkCases, 
lostWorkDays, injuryIllnessCases, restrictedWorkCases, recordableTotal)
select ca.auditID, SHAType, location, description, 
1, case file4YearAgo when 'Yes' then 1 else 0 end, manHours4, fatalities4, lostWorkCases4,
lostWorkDays4, injuryIllnessCases4, restrictedWorkCases4, recordableTotal4
from osha
join contractor_audit ca on ca.conID = osha.conID and auditTypeID = 11
where auditFor = '2004' and manHours4 > 0;


insert into osha_audit (auditID, SHAType, location, description,
applicable, fileUploaded, manHours, fatalities, lostWorkCases, 
lostWorkDays, injuryIllnessCases, restrictedWorkCases, recordableTotal)
select ca.auditID, SHAType, location, description, 
1, case file5YearAgo when 'Yes' then 1 else 0 end, manHours5, fatalities5, lostWorkCases5,
lostWorkDays5, injuryIllnessCases5, restrictedWorkCases5, recordableTotal5
from osha
join contractor_audit ca on ca.conID = osha.conID and auditTypeID = 11
where auditFor = '2003' and manHours5 > 0;


insert into osha_audit (auditID, SHAType, location, description,
applicable, fileUploaded, manHours, fatalities, lostWorkCases, 
lostWorkDays, injuryIllnessCases, restrictedWorkCases, recordableTotal)
select ca.auditID, SHAType, location, description, 
1, case file6YearAgo when 'Yes' then 1 else 0 end, manHours6, fatalities6, lostWorkCases6,
lostWorkDays6, injuryIllnessCases6, restrictedWorkCases6, recordableTotal6
from osha
join contractor_audit ca on ca.conID = osha.conID and auditTypeID = 11
where auditFor = '2002' and manHours6 > 0;


insert into osha_audit (auditID, SHAType, location, description,
applicable, fileUploaded, manHours, fatalities, lostWorkCases, 
lostWorkDays, injuryIllnessCases, restrictedWorkCases, recordableTotal)
select ca.auditID, SHAType, location, description, 
1, case file7YearAgo when 'Yes' then 1 else 0 end, manHours7, fatalities7, lostWorkCases7,
lostWorkDays7, injuryIllnessCases7, restrictedWorkCases7, recordableTotal7
from osha
join contractor_audit ca on ca.conID = osha.conID and auditTypeID = 11
where auditFor = '2001' and manHours7 > 0;


update osha_audit 
set auditorID = null
where auditorID = 0;





update notes set userID from whois and opID
update notes set deletedUserID from whoDeleted and opID


insert into note (accountID, creationDate, createdBy, summary, noteCategory, priority, viewableBy, body)
select id, accountDate, 959, 'Contractor Notes Pre-Oct08', 'General', 3, 1, notes
from contractor_info
where notes > '';

insert into note (accountID, creationDate, createdBy, summary, noteCategory, priority, viewableBy, body)
select id, accountDate, 959, 'PICS-only Notes Pre-Oct08', 'General', 3, 1100, adminNotes
from contractor_info
where adminNotes > '';

insert into note (accountID, creationDate, createdBy, updatedBy, updateDate, summary, noteCategory, status, priority, viewableBy, body)
select conID, timeStamp, case ISNULL(userID) when 1 then 959 else userID end, deletedDate, deletedUserID, note, 'General', case isDeleted when 1 then 0 else 2 end, 3, opID, null
from notes
where length(note) <= 250;

insert into note (accountID, creationDate, createdBy, updatedBy, updateDate, summary, noteCategory, status, priority, viewableBy, body)
select conID, timeStamp, case ISNULL(userID) when 1 then 959 else userID end, deletedDate, deletedUserID, substring(note, 1, 255), 'General', case isDeleted when 1 then 0 else 2 end, 3, opID, substring(note, 255)
from notes
where length(note) > 250;
