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


/**  insert data into the flag_criteria_operator **/
-- insert data for PQF and Annual Updates
insert into flag_criteria_operator
select null,ao.createdBy, ao.updatedBy, ao.creationDate, ao.updateDate, ao.opid, f.id, 
ao.requiredForFlag,null as hurdle, null as percentAffected, null as lastcalculated,ao.minRiskLevel from audit_operator ao
join flag_criteria f on ao.auditTypeid = f.audittypeid
join audit_type at on at.id = ao.audittypeid
where
(ao.auditTypeid in (1,11) AND 
	(
		(ao.requiredAuditStatus = 'Active' and f.validationRequired = 1) 
		or
		(ao.requiredAuditStatus = 'Submitted' and f.validationRequired = 0)
	)
)
and ao.canSee = 1 
and ao.minriskLevel > 0;

-- insert data for non PQF and Annual Updates
insert into flag_criteria_operator
select null,ao.createdBy, ao.updatedBy, ao.creationDate, ao.updateDate, ao.opid, f.id, 
ao.requiredForFlag,null as hurdle, null as percentAffected, null as lastcalculated,ao.minRiskLevel from audit_operator ao
join flag_criteria f on ao.auditTypeid = f.audittypeid
join audit_type at on at.id = ao.audittypeid
where ao.auditTypeid not in (1,11)
and ao.canSee = 1 
and ao.minriskLevel > 0;


-- updating the flag_criteria_opertor for Non-EMR questions 

insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,f.ID, 
fo.flagStatus,fo.value as hurdle, null as percentAffected, null as lastcalculated,null as minRiskLevel
from flagcriteria fo
join flag_criteria f on fo.questionid = f.questionid
where fo.questionid not in (2034);

-- updating the flag_criteria_opertor for EMR questions 
insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,f.ID, 
fo.flagStatus,fo.value as hurdle, null as percentAffected, null as lastcalculated,null as minRiskLevel
from flagcriteria fo
join flag_criteria f on fo.questionid = f.questionid
and fo.multiyearscope = f.multiyearscope
where fo.questionid in (2034);



-- updating the multiyearscope for AllThreeYears
insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,f.ID, 
fo.flagStatus,fo.value as hurdle, null as percentAffected, null as lastcalculated,null as minRiskLevel
from flagcriteria fo
join flag_criteria f on fo.questionid = f.questionid
and fo.multiyearscope = 'AllThreeYears'
and f.multiyearscope = 'LastYearOnly'
where fo.questionid in (2034);

insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,f.ID, 
fo.flagStatus,fo.value as hurdle, null as percentAffected, null as lastcalculated,null as minRiskLevel
from flagcriteria fo
join flag_criteria f on fo.questionid = f.questionid
and fo.multiyearscope = 'AllThreeYears'
and f.multiyearscope = 'TwoYearsAgo'
where fo.questionid in (2034);

insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,f.ID, 
fo.flagStatus,fo.value as hurdle, null as percentAffected, null as lastcalculated,null as minRiskLevel
from flagcriteria fo
join flag_criteria f on fo.questionid = f.questionid
and fo.multiyearscope = 'AllThreeYears'
and f.multiyearscope = 'ThreeYearsAgo'
where fo.questionid in (2034);


-- not sure if we want to run these
-----------------------------------------------
select * from flag_criteria_operator fo
join flag_criteria k on fo.criteriaid = k.id
where k.allowCustomValue = 0
and k.questionid is not null
and fo.hurdle is not null;

update flag_criteria_operator fo,flag_criteria k set fo.hurdle = NULL
where fo.criteriaid = k.id
and k.allowCustomValue = 0
and k.questionid is not null
and fo.hurdle is not null;
------------------------------------------------

-- inserting on flag_criteria_operator for osha LWCR
insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,k.id,fo.flagStatus, 
fo.lwcrhurdle as hurdle, null as percentAffected, null as lastcalculated,null as minRiskLevel
from flagoshacriteria fo
join operators o on fo.opid = o.id
join flag_criteria k on k.oshaType = o.oshaType
where  
(
	(k.multiyearScope = 'ThreeYearAverage' and fo.lwcrTime = 3)
	or
	(k.multiyearScope = 'LastYearOnly' and fo.lwcrTime = 2)
)
and 
(	(k.oshaRateType = 'LwcrAbsolute' and fo.lwcrhurdleType = 'Absolute')
	or
	(k.oshaRateType = 'LwcrNaics' and fo.lwcrhurdleType = 'NAICS')
);

insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,k.id,fo.flagStatus, 
fo.lwcrhurdle as hurdle, null as percentAffected, null as lastcalculated,null as minRiskLevel
from flagoshacriteria fo
join operators o on fo.opid = o.id
join flag_criteria k on k.oshaType = o.oshaType
where  
(
	(k.multiyearScope = 'LastYearOnly' and fo.lwcrTime = 1)
	or
	(k.multiyearScope = 'TwoYearsAgo' and fo.lwcrTime = 1)
	or
	(k.multiyearScope = 'ThreeYearsAgo' and fo.lwcrTime = 1)
)
and 
(	(k.oshaRateType = 'LwcrAbsolute' and fo.lwcrhurdleType = 'Absolute')
	or
	(k.oshaRateType = 'LwcrNaics' and fo.lwcrhurdleType = 'NAICS')
);

-- inserting on flag_criteria_operator for osha TRIR
insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,k.id,fo.flagStatus, 
fo.trirhurdle as hurdle, null as percentAffected, null as lastcalculated,null as minRiskLevel
from flagoshacriteria fo
join operators o on fo.opid = o.id
join flag_criteria k on k.oshaType = o.oshaType
where  
(
	(k.multiyearScope = 'ThreeYearAverage' and fo.trirTime = 3)
	or
	(k.multiyearScope = 'LastYearOnly' and fo.trirTime = 2)
)
and 
(	(k.oshaRateType = 'TrirAbsolute' and fo.trirhurdleType = 'Absolute')
	or
	(k.oshaRateType = 'TrirNaics' and fo.trirhurdleType = 'NAICS')
);

insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,k.id,fo.flagStatus, 
fo.trirhurdle as hurdle, null as percentAffected, null as lastcalculated,null as minRiskLevel
from flagoshacriteria fo
join operators o on fo.opid = o.id
join flag_criteria k on k.oshaType = o.oshaType
where  
(
	(k.multiyearScope = 'LastYearOnly' and fo.trirTime = 1)
	or
	(k.multiyearScope = 'TwoYearsAgo' and fo.trirTime = 1)
	or
	(k.multiyearScope = 'ThreeYearsAgo' and fo.trirTime = 1)
)
and 
(	(k.oshaRateType = 'TrirAbsolute' and fo.trirhurdleType = 'Absolute')
	or
	(k.oshaRateType = 'TrirNaics' and fo.trirhurdleType = 'NAICS')
);

-- inserting on flag_criteria_operator for osha Fatalities
insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,k.id,fo.flagStatus, 
fo.fatalitieshurdle as hurdle, null as percentAffected, null as lastcalculated,null as minRiskLevel
from flagoshacriteria fo
join operators o on fo.opid = o.id
join flag_criteria k on k.oshaType = o.oshaType
where  
(
	(k.multiyearScope = 'ThreeYearAverage' and fo.fatalitiesTime = 3)
	or
	(k.multiyearScope = 'LastYearOnly' and fo.fatalitiesTime = 2)
)
and 
(	(k.oshaRateType = 'Fatalities' and fo.fatalitieshurdleType = 'Absolute')
);

insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,k.id,fo.flagStatus, 
fo.fatalitieshurdle as hurdle, null as percentAffected, null as lastcalculated,null as minRiskLevel
from flagoshacriteria fo
join operators o on fo.opid = o.id
join flag_criteria k on k.oshaType = o.oshaType
where  
(
	(k.multiyearScope = 'LastYearOnly' and fo.fatalitiesTime = 1)
	or
	(k.multiyearScope = 'TwoYearsAgo' and fo.fatalitiesTime = 1)
	or
	(k.multiyearScope = 'ThreeYearsAgo' and fo.fatalitiesTime = 1)
)
and 
(	(k.oshaRateType = 'Fatalities' and fo.fatalitieshurdleType = 'Absolute')
);


-- inserting on flag_criteria_operator for osha SeverityRate
insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,k.id,fo.flagStatus, 
fo.severityhurdle as hurdle, null as percentAffected, null as lastcalculated,null as minRiskLevel
from flagoshacriteria fo
join operators o on fo.opid = o.id
join flag_criteria k on k.oshaType = o.oshaType
where  
(
	(k.multiyearScope = 'ThreeYearAverage' and fo.severityTime = 3)
	or
	(k.multiyearScope = 'LastYearOnly' and fo.severityTime = 2)
)
and 
(	(k.oshaRateType = 'SeverityRate' and fo.severityhurdleType = 'Absolute')
);

insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,k.id,fo.flagStatus, 
fo.severityhurdle as hurdle, null as percentAffected, null as lastcalculated,null as minRiskLevel
from flagoshacriteria fo
join operators o on fo.opid = o.id
join flag_criteria k on k.oshaType = o.oshaType
where  
(
	(k.multiyearScope = 'LastYearOnly' and fo.severityTime = 1)
	or
	(k.multiyearScope = 'TwoYearsAgo' and fo.severityTime = 1)
	or
	(k.multiyearScope = 'ThreeYearsAgo' and fo.severityTime = 1)
)
and 
(	(k.oshaRateType = 'SeverityRate' and fo.severityhurdleType = 'Absolute')
);


-- inserting on flag_criteria_operator for osha CAd7
insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,k.id,fo.flagStatus, 
fo.cad7hurdle as hurdle, null as percentAffected, null as lastcalculated,null as minRiskLevel
from flagoshacriteria fo
join operators o on fo.opid = o.id
join flag_criteria k on k.oshaType = o.oshaType
where  
(
	(k.multiyearScope = 'ThreeYearAverage' and fo.cad7Time = 3)
	or
	(k.multiyearScope = 'LastYearOnly' and fo.cad7Time = 2)
)
and 
(	(k.oshaRateType = 'Cad7' and fo.cad7hurdleType = 'Absolute')
);

insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,k.id,fo.flagStatus, 
fo.cad7hurdle as hurdle, null as percentAffected, null as lastcalculated,null as minRiskLevel
from flagoshacriteria fo
join operators o on fo.opid = o.id
join flag_criteria k on k.oshaType = o.oshaType
where  
(
	(k.multiyearScope = 'LastYearOnly' and fo.cad7Time = 1)
	or
	(k.multiyearScope = 'TwoYearsAgo' and fo.cad7Time = 1)
	or
	(k.multiyearScope = 'ThreeYearsAgo' and fo.cad7Time = 1)
)
and 
(	(k.oshaRateType = 'Cad7' and fo.cad7hurdleType = 'Absolute')
);


-- inserting on flag_criteria_operator for osha Neer
insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,k.id,fo.flagStatus, 
fo.neerhurdle as hurdle, null as percentAffected, null as lastcalculated,null as minRiskLevel
from flagoshacriteria fo
join operators o on fo.opid = o.id
join flag_criteria k on k.oshaType = o.oshaType
where  
(
	(k.multiyearScope = 'ThreeYearAverage' and fo.neerTime = 3)
	or
	(k.multiyearScope = 'LastYearOnly' and fo.neerTime = 2)
)
and 
(	(k.oshaRateType = 'Neer' and fo.neerhurdleType = 'Absolute')
);

insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,k.id,fo.flagStatus, 
fo.neerhurdle as hurdle, null as percentAffected, null as lastcalculated,null as minRiskLevel
from flagoshacriteria fo
join operators o on fo.opid = o.id
join flag_criteria k on k.oshaType = o.oshaType
where  
(
	(k.multiyearScope = 'LastYearOnly' and fo.neerTime = 1)
	or
	(k.multiyearScope = 'TwoYearsAgo' and fo.neerTime = 1)
	or
	(k.multiyearScope = 'ThreeYearsAgo' and fo.neerTime = 1)
)
and 
(	(k.oshaRateType = 'Neer' and fo.neerhurdleType = 'Absolute')
);


update flag_criteria_operator set createdBy = 1
where createdBy is null;

update flag_criteria_operator set updatedBy = 1
where updatedBy is null;

update flag_criteria_operator set creationDate = Now()
where creationDate is null;

update flag_criteria_operator set updateDate = Now()
where updateDate is null;

select * from operators where inheritaudits is null;
select * from operators where inheritInsurance is null;
select * from operators where inheritFlagCriteria is null;
select * from operators where inheritInsuranceCriteria is null;

-- clean the flag_criteria_operator data to remove the unused criteria data for audits
delete from flag_criteria_operator
where opid not in (select distinct inheritaudits from operators)
and criteriaid in (select f.id from flag_criteria f
join audit_type at on f.audittypeid = at.id
where at.classType != 'Policy');

delete from flag_criteria_operator
where opid not in (select distinct inheritInsurance from operators)
and criteriaid in (select f.id from flag_criteria f
join audit_type at on f.audittypeid = at.id
where at.classType = 'Policy');

-- clean the flag_criteria_operator data to remove the unused criteria data for questions
delete from flag_criteria_operator
where opid not in (select distinct inheritFlagCriteria from operators)
and criteriaid in (select f.id from flag_criteria f
join pqfquestions p on f.questionID = p.id 
join pqfsubcategories ps on ps.id = p.subcategoryid
join pqfcategories pc on pc.id = ps.categoryid
join audit_type at on at.id = pc.audittypeid
where at.classType != 'Policy');

delete from flag_criteria_operator
where opid not in (select distinct inheritInsuranceCriteria from operators)
and criteriaid in (select f.id from flag_criteria f
join pqfquestions p on f.questionID = p.id 
join pqfsubcategories ps on ps.id = p.subcategoryid
join pqfcategories pc on pc.id = ps.categoryid
join audit_type at on at.id = pc.audittypeid
where at.classType = 'Policy');

-- clean the flag_criteria_operator data to remove the unused criteria data for osha
delete from flag_criteria_operator
where opid not in (select distinct inheritFlagCriteria from operators)
and criteriaid in (select f.id from flag_criteria f
where f.oshatype is not null
and f.questionid is null 
and f.audittypeID is null);

-- clean up the generalcontractors table dates
update generalcontractors set creationDate = null where creationDate = '0000-00-00 00:00:00'