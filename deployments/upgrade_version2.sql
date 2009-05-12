/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;
*/
UPDATE audit_type set classType = 'PQF'
where auditName like 'PQF%';

update flagoshacriteria set lwcrHurdleType = 'None', trirHurdleType = 'None', fatalitiesHurdleType = 'None';

update flagoshacriteria set lwcrHurdleType = 'Absolute' where flagLwcr = 'Yes';
update flagoshacriteria set trirHurdleType = 'Absolute' where flagTrir = 'Yes';
update flagoshacriteria set fatalitiesHurdleType = 'Absolute' where flagFatalities = 'Yes';

/**
 * update the NAICS from the pqfdata for the existing contractors
 * 
 */
create TEMPORARY table temp_naics
(id Mediumint(8), naics varchar(10));

insert into temp_naics 
select a.id, SUBSTRING_INDEX(pd.answer,',',1) from accounts a
join contractor_audit ca on ca.conid = a.id
Left join pqfdata pd on pd.id = ca.id
where a.type = 'Contractor'
and ca.audittypeId = 1
and pd.answer != null or pd.answer != ''
and pd.questionid = 57;
 
select * from temp_naics;

update accounts a, temp_naics t set a.naics = t.naics
where a.id = t.id;

update accounts a set naics = NULL
where naics = 'NA' or naics = 'N/A';

update accounts a set naics = 562910
where id = 7060;

/**
 * Added open Notes widget to all users
 */
insert into widget values
('Open Notes', 'Html',0,'UserOpenNotesAjax.action',null, null);

insert into widget_user values
(newwidgetid, 941, 1,1,5, null);
insert into widget_user values
(newwidgetid, 910, 1,1,5, null);
insert into widget_user values
(newwidgetid, 616, 1,1,5, null);
insert into widget_user values
(newwidgetid, 646, 1,1,5, null);

/**
 * Splitting up the pqf operator specific categories into their audits 
 * 
 */
select * from temp_category_audittype;

-- update the categories to the new auditTypeID 
update pqfcategories pc, temp_category_audittype tca set pc.auditTypeID = tca.auditTypeID
where pc.id = tca.categoryID
and pc.auditTypeID = 1;  

-- delete from contractor_audit any audits with the new auditTypeID
delete from contractor_audit  
where auditTypeID IN (select tca.auditTypeID from temp_category_audittype tca);

-- Create audits for these new Categories
insert into contractor_audit
select null, tca.auditTypeID, canew.conID, canew.creationDate,canew.auditStatus,
  canew.expiresDate,canew.auditorID,canew.assignedDate,canew.scheduledDate,
  canew.completedDate,canew.closedDate,canew.requestedByOpID,
  canew.auditLocation,canew.percentComplete,0,
  canew.contractorConfirm,canew.auditorConfirm,canew.manuallyAdded,
  canew.auditFor,canew.createdBy,canew.updatedBy,NOW(),canew.score,
  null from contractor_audit canew
join pqfcatdata pcd on pcd.auditid = canew.id
join temp_category_audittype tca on tca.categoryID = pcd.catID
where canew.auditTypeID = 1
AND pcd.applies = 'Yes';

-- insert into temp_auditconverison the old auditid, new auditid and catid
insert into temp_auditconversion
select null, caold.id,canew.id, pcd.catid from contractor_audit canew
join contractor_audit caold on canew.conid = caold.conid
join audit_type at on at.id = canew.auditTypeID
join pqfcatdata pcd on pcd.auditid = caold.id
join temp_category_audittype tca on tca.categoryID = pcd.catID
where caold.auditTypeID = 1
and at.id = tca.auditTypeID
and pcd.applies = 'Yes';

-- Also update the new auditid on the pqfcatData
update pqfcatdata pcd, temp_auditconversion tca set pcd.auditID = tca.customID
where pcd.auditID = tca.pqfID
and pcd.applies = 'Yes'
and pcd.catId = tca.catID;

-- update the pqfdata with the new audits
update pqfdata pd, temp_auditconversion tca, pqfquestions pqf, pqfsubcategories ps, 
pqfcategories pc, pqfcatdata pcd  
set pd.auditid = tca.customID
where pd.auditid = tca.pqfID
and pd.questionid = pqf.id
and ps.id = pqf.subcategoryID
and pc.id = ps.categoryID
and pc.id = tca.catID
and pcd.auditid = tca.customid
and pcd.applies = 'Yes'
and pcd.catid = pc.id;


-- Remove all the other categories on the pqfcatData where applies = 'No'
select count(*) from contractor_audit ca
join pqfcatdata pcd on ca.id = pcd.auditid
join temp_category_audittype tca on pcd.catid = tca.categoryID
where ca.audittypeId = 1;
 
delete from pqfcatdata 
where auditid in (select ca.id from contractor_audit ca where ca.audittypeId = 1)
and catid in (select tca.categoryID from temp_category_audittype tca);

-- update the new audits with percent Complete and auditStatus
update contractor_audit ca,temp_category_audittype tca, pqfcatdata pcd 
set ca.auditstatus = 'Pending', ca.percentComplete = pcd.percentCompleted
where tca.audittypeID = ca.auditTypeid
and ca.id = pcd.auditid
and pcd.percentCompleted < 100;

update contractor_audit ca,temp_category_audittype tca, pqfcatdata pcd 
set ca.auditstatus = 'Active', ca.percentComplete = pcd.percentCompleted
where tca.audittypeID = ca.auditTypeid
and ca.id = pcd.auditid
and pcd.percentCompleted = 100;
 
-- update it to audit_operator matrix for these operators looking at the pqf of this operator.
insert into audit_operator 
select null, tca.auditTypeid, ao.opID, ao.canSee, ao.canEdit, 
	min(pm.risklevel), ao.orderedCount,ao.orderDate, 
	ao.requiredForFlag, 
	ao.requiredAuditStatus, 
	ao.additionalInsuredFlag, 
	ao.waiverSubFlag, 
	ao.createdBy, 
	ao.updatedBy, 
	ao.creationDate, 
	Now()
from audit_operator ao 
join pqfopmatrix pm on ao.opID = pm.opID
join temp_category_audittype tca on tca.categoryid = pm.catid
where ao.auditTypeid = 1
group by pm.catid, pm.opid; 

-- Remove these categories from pqfopmatrix
delete from pqfopmatrix where catid in (select tca.categoryID from temp_category_audittype tca);
