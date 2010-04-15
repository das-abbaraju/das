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

-- ADD TO HOURLY SCRIPT
truncate table job_competency_stats;

insert into job_competency_stats (jobRole, totalCount, competencyID)
select jr.name, count(DISTINCT jr.accountID), oc.id from job_role jr, operator_competency oc
where jr.id in (SELECT jobRoleID FROM job_competency)
group by jr.name, oc.id;

UPDATE job_competency_stats s, (
select jr.name, jc.competencyID, count(*) usedCount from job_role jr
join job_competency jc on jr.id = jc.jobRoleID
group by jr.name, jc.competencyID) t
set s.usedCount = t.usedCount
where t.name = s.jobRole and s.competencyID = t.competencyID;

select jobRole, oc.label, round(100*usedCount/totalCount) percentUsed from job_competency_stats s
join operator_competency oc on s.competencyID = oc.id
order by usedCount/totalCount desc, totalCount desc, jobRole;

/** Update the requiresOQ for all contractors **/
update accounts set requiresOQ = 1
where id in (select distinct conid from contractor_audit ca
join pqfdata pd on ca.id = pd.auditid
where pd.questionid = 894
and pd.answer = 'Yes');

/** Added new Operator Basic User permission to view the Operator Flag Matrix report **/
insert into `useraccess`(`accessID`,`userID`,`accessType`,`viewFlag`,`editFlag`,`deleteFlag`,`grantFlag`,`lastUpdate`,`grantedByID`)
values ( NULL,'1554','OperatorFlagMatrix','1',NULL,NULL,'0',CURRENT_TIMESTAMP,NULL);

alter table `contractor_info` drop column `oqEmployees`;