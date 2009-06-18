/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;
*/

select os.applicable, os.auditid, pcd.applies from osha_audit os 
join pqfcatdata pcd on pcd.auditid = os.auditid
where os.applicable = 1
and pcd.applies = 'No'
and pcd.catID = 151; 

select os.applicable, os.auditid, pcd.applies from osha_audit os 
join pqfcatdata pcd on pcd.auditid = os.auditid
join contractor_audit ca on ca.id = os.auditid 
where os.applicable = 0
and pcd.applies = 'Yes'
and pcd.catID = 151;


-- DROP table pqfdata_duplicates;

create table pqfdata_duplicates as
select auditID, questionID, max(updateDate) updateDate, MAX(id) id
from pqfdata
group by auditID, questionID
having count(*) > 1;

CREATE UNIQUE INDEX questionaudit ON pqfdata_duplicates (questionID, auditID);

delete from pqfdata
where exists (
	select * from pqfdata_duplicates d2
	where pqfdata.auditID = d2.auditID and pqfdata.questionID = d2.questionID and pqfdata.updateDate < d2.updateDate
)

DROP table pqfdata_duplicates;

create table pqfdata_duplicates as
select auditID, questionID, max(updateDate) updateDate, MAX(id) id
from pqfdata
group by auditID, questionID
having count(*) > 1;

CREATE UNIQUE INDEX questionaudit ON pqfdata_duplicates (questionID, auditID);

delete from pqfdata
where exists (
	select * from pqfdata_duplicates d2
	where pqfdata.auditID = d2.auditID and pqfdata.questionID = d2.questionID and pqfdata.id < d2.id
)

DROP INDEX questionContractor ON pqfdata;
CREATE UNIQUE INDEX questionaudit ON pqfdata (questionID, auditID);

DROP table pqfdata_duplicates;

/*
Added a new widget for operators to show contractors with Pending Approval
*/
insert into widget 
values (null,"Contractors Pending Approvals", "Html", 0, 
	"ContractorPendingApprovalAjax.action", 
	"ContractorApproval",null);

insert into widget_user 
values (null, newWidgetID, 616, 1, 2,10, null);>>>>>>> .r5827

/*
 * Added a system message to the app_properties.
 */
 insert into app_properties values ('SYSTEM.MESSAGE', null);
