
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

--NAICS codes
update accounts a,
  (select
     ca.conID,
     answer
   from contractor_audit ca
     join pqfdata pd
       on pd.auditID = ca.id
     join naics n
       on n.code = pd.answer
   where ca.auditTypeID = 1
       and pd.questionid = 57) t
set a.naics = t.answer,    
naicsValid = 1
where a.id = t.conID and a.naics = 0;

update accounts a,
  (select
     ca.conID,
     answer
   from contractor_audit ca
     join pqfdata pd
       on pd.auditID = ca.id
     join naics n
       on n.code = LEFT(pd.answer, 10)
   where ca.auditTypeID = 1
       and pd.questionid = 57) t
set a.naics = LEFT(t.answer,10) ,    
naicsValid = 0
where a.id = t.conID and a.naics = 0;
