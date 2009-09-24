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

/** Added the Email, Fax and Phone tokens for the Email **/
insert into token 
values
(null, "MyPhone", "ALL", "Tel: $!{permissions.phone}");

insert into token 
values
(null, "MyEmail", "ALL", "Email: $!{permissions.email}");

insert into token 
values
(null, "MyFax", "ALL", "Fax: $!{permissions.fax}");

/** update the signature on the verification email to 
<MyName>,
PICS 
P.O. Box 51387
Irvine CA 92619-1387
<MyPhone>
<MyFax>
<MyEmail>
**/