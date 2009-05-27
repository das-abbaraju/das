
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

/**
 * update the visible field to 0 on the CAO if not required
 */
update contractor_audit_operator cao set visible = 0 
where status = 'NotApplicable' 
and recommendedStatus = 'NotApplicable'
and creationDate = updateDate;

update contractor_audit_operator cao set flag = 'Green'
where recommendedStatus = 'Approved';

update contractor_audit_operator cao set flag = 'Red'
where recommendedStatus = 'Rejected';

/**
 * update the column header for these questions on live 
**/
select pq.* from pqfquestions pq 
where subcategoryId in (select ps.id from pqfsubcategories ps where ps.subcategory = 'Policy Limits')
and columnHeader = '';

/**
 * set the helpText to NULL for all operator requirements
 **/ 
update pqfsubcategories set helpText = NULL
where subcategory = 'Operator Requirements';