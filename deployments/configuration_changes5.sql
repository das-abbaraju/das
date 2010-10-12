/*
 * UPDATING THE QUESTIONCOMPARATOR AND QUESTIONANSWER
 */
-- deleting the desktop rules for categories for question 1417
delete from audit_category_rule where auditTypeID = 2 and questionid = 1417;

update audit_category_rule acr
join audit_category pc on acr.catID = pc.id
join audit_question pq on pq.id = acr.questionID 
set acr.questionComparator = 'Equals',acr.questionAnswer = 'X', acr.updatedBy= 1098, acr.updateDate = Now()
where acr.audittypeid = 2 and include = 1
and pq.questionType IN ('Industry','Main Work');

update audit_category_rule acr
join audit_category pc on acr.catID = pc.id
join audit_question pq on pq.id = acr.questionID 
set acr.questionComparator = 'StartsWith',acr.questionAnswer = 'C', acr.updatedBy= 1098, acr.updateDate = Now()
where acr.audittypeid = 2 and include = 1
and pq.questionType = 'Service';

/*
 * expiring the not visible questions on the audit question
 */
update audit_question aq
join pics_yesterday.pqfquestions oldq on oldq.id = aq.id
set aq.expirationDate = oldq.updateDate
where oldq.isvisible = 'No'
and aq.expirationDate > Now(); 

/*
 * inserting the category rules for Annual Update
 */
insert into audit_category_rule 
values
(null,458,1,151,11,NULL,5,NULL,1098,1098,now(),Now(),now(),'4000-01-01',NULL,2064,'Equals','Yes',NULL,NULL,4),-- osha
(null,454,1,158,11,NULL,6,NULL,1098,1098,now(),Now(),now(),'4000-01-01',NULL,2066,'Equals','Yes',NULL,NULL,4),-- canadian
(null,458,1,157,11,NULL,5,NULL,1098,1098,now(),Now(),now(),'4000-01-01',NULL,2065,'Equals','Yes',NULL,NULL,4),-- msha
(null,458,1,152,11,NULL,5,NULL,1098,1098,now(),Now(),now(),'4000-01-01',NULL,2033,'Equals','Yes',NULL,NULL,4),-- emr
(null,454,1,159,11,NULL,5,NULL,1098,1098,now(),Now(),now(),'4000-01-01',NULL,2033,'Equals','No',NULL,NULL,4),-- loss run
(null,333,1,278,11,NULL,5,NULL,1098,1098,now(),Now(),now(),'4000-01-01',NULL,3546,'Equals','Yes',NULL,NULL,3); -- citations

/*
* Customizing the Rules for Audit Types
*/
--  Create Manual Audit if PQF is Active
update audit_type_rule set dependentAuditTypeID = 1, dependentAuditStatus = 'Complete',
updatedBy = 1098, updateDate = Now()
where auditTypeID = 2 and include = 1;
-- Create Implementation Audit if PQF is >= Submitted
update audit_type_rule set dependentAuditTypeID = 1, dependentAuditStatus = 'Submitted',
updatedBy = 1098, updateDate = Now()
where auditTypeID = 3 and include = 1;
-- Create DA if PQF is >= submitted and OqEmployees answer is Yes
update audit_type_rule set dependentAuditTypeID = 1, dependentAuditStatus = 'Submitted',
questionid = 894, questionComparator = 'Equals', questionAnswer = 'Yes',
updatedBy = 1098, updateDate = Now()
where auditTypeID = 6 and include = 1;
-- Create COR if hasCor answer is Yes
update audit_type_rule set questionid = 2954, questionComparator = 'Equals', questionAnswer = 'Yes',
updatedBy = 1098, updateDate = Now()
where auditTypeID = 72 and include = 1;
-- Create BPIISNCASEMGMT if BP IISN Specific is >= Resubmitted
update audit_type_rule set dependentAuditTypeID = 87, dependentAuditStatus = 'Resubmitted',
updatedBy = 1098, updateDate = Now()
where auditTypeID = 96 and include = 1;
-- Create HSE Competency Submittal if Shell Competency Review >= Resubmitted
update audit_type_rule set dependentAuditTypeID = 99, dependentAuditStatus = 'Resubmitted',
updatedBy = 1098, updateDate = Now()
where auditTypeID = 100 and include = 1;


/**
 * Creating rules for bid only contractors 
 **/
insert into audit_type_rule 
(id,priority,include,createdBy,updatedBy,creationDate,updateDate,
effectiveDate,expirationDate,acceptsBids,level,levelAdjustment)
values 
(null,1,0,1098,1098,Now(),Now(),'2001-01-01', '4000-01-01',1,0,0);

-- for pqf
insert into audit_type_rule 
(id,priority,include,auditTypeID,createdBy,updatedBy,creationDate,updateDate,
effectiveDate,expirationDate,acceptsBids,level,levelAdjustment)
values 
(null,104,1,1,1098,1098,Now(),Now(),'2001-01-01', '4000-01-01',1,1,0);

-- for annual addendum
insert into audit_type_rule 
(id,priority,include,auditTypeID,createdBy,updatedBy,creationDate,updateDate,
effectiveDate,expirationDate,acceptsBids,level,levelAdjustment)
values 
(null,104,1,11,1098,1098,Now(),Now(),'2001-01-01', '4000-01-01',1,1,0);

-- category rules
insert into audit_category_rule 
(id,priority,include,auditTypeID,createdBy,updatedBy,creationDate,updateDate,
effectiveDate,expirationDate,acceptsBids,level,levelAdjustment)
values 
(null,110,0,11,1098,1098,Now(),Now(),'2001-01-01', '4000-01-01',1,1,0);

insert into audit_category_rule 
select 	null,priority,include,catID,auditTypeID, 
risk,opID,tagID,1098,1098,now(),Now(),effectiveDate, 
expirationDate,tempTotal,questionID,questionComparator,questionAnswer, 
contractorType,1,level,operatorCountry,levelAdjustment
from audit_category_rule
where include = 1 AND AUDITtypeid = 11;

insert into audit_category_rule 
(id,priority,include,auditTypeID,createdBy,updatedBy,creationDate,updateDate,
effectiveDate,expirationDate,acceptsBids,level,levelAdjustment)
values 
(null,110,0,1,1098,1098,Now(),Now(),'2001-01-01', '4000-01-01',1,1,0);

insert into audit_category_rule 
select 	null,priority,include,catID,auditTypeID, 
risk,opID,tagID,1098,1098,now(),Now(),effectiveDate, 
expirationDate,tempTotal,questionID,questionComparator,questionAnswer, 
contractorType,1,level,operatorCountry,levelAdjustment
from audit_category_rule
where include = 1 AND AUDITtypeid = 1 and opid is null and risk is null
and catid in (2,8,28,184);
