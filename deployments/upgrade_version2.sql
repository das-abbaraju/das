/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;

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
**/

update pqfquestions set dependsOnqID = null where dependsOnqID = 0;

update accounts set status = 'Active';
update accounts set status = 'Pending' WHERE active = 'N';
update accounts set status = 'Demo' where name like '%^^^%' or name like 'PICS%demo%';
update accounts set status = 'Deleted' where status != 'Active' and name like '%duplicat%';
update accounts set status = 'Deactivated' where status = 'Pending' and type = 'Contractor' and id in (select id from invoice where tableType = 'I' and status = 'Paid');

-- Changing Cron Statistics widget title to System Status
update widget set caption = 'System Status' where widgetID = 16;