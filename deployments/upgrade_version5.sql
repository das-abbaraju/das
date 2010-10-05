-- This is a list of changes we're doing ONCE as part of the 4.5 to 5 upgrade on 10/4
-- This data will eventually be copied back to live on 10/28

-- Audit Changes
insert into audit_category
select id, auditTypeID, null parent, 
category name, number, numRequired, numQuestions, null helpText, 
createdBy, updatedBy, creationDate, updateDate, false pageBreak, 
id legacyID
from pqfcategories;
-- fix name of id = 103

insert into audit_category 
select null,pc.auditTypeID,ps.categoryID, ps.subCategory,
ps.number,-1,-1,ps.helpText,ps.createdBy,ps.updatedBy,ps.creationDate, 
ps.updateDate, false, ps.id 
from pqfsubcategories ps
join pqfcategories pc on ps.categoryID = pc.id;

insert into audit_question
select q.id, c.id categoryID, q.number, t.question name, q.createdBy, q.updatedBy, q.creationDate, q.updateDate, q.effectiveDate, q.expirationDate, q.questionType, (q.hasRequirement = 'Yes') hasRequirement, q.okAnswer, (q.isRequired = 'Yes') required, q.dependsOnQID, q.dependsOnAnswer, null visibleQuestion, null visibleAnswer, q.columnHeader, q.uniqueCode, q.title, (q.isGroupedWithPrevious = 'Yes') groupedWithPrevious, (q.isRedFlagQuestion = 'Yes') flaggable, q.showComment, q.riskLevel, q.helpPage, t.requirement
from pqfquestions q
  join pqfquestion_text t
    on q.id = t.questionID
      and t.locale = 'en'
  join audit_category c
    on c.legacyID = q.subCategoryID
and c.parentID is not null;

update pqfquestions set linkURL3 = replace(linkURL3, 'http://', '') where linkURL3 like '%http://%';

update audit_question q2, pqfquestions q1
set name = concat(name,' <a href="http://', linkURL6, '" target="_BLANK">', linkText6,'</a>')
where q2.id = q1.id and linkURL6 > '';

create table temp_single_subcats as
select
          p.id    pid,
p.name pname, 
          c.id    cid, 
c.name cname
from audit_category p
join audit_category c on p.id = c.parentID
group by p.id
having count(c.id) = 1;

-- Move questions up a level where there is only a single subcategory
update audit_question q, temp_single_subcats t
set q.categoryID = t.pid
where q.categoryID = t.cid;

create table temp_cao_conversion as
select ao.id, ao.opID, ao.auditTypeID, ao.help
from audit_operator ao
join audit_type aType on aType.id = ao.auditTypeID and aType.classType = 'Policy'
where ao.canSee = 1 and ao.opID IN (SELECT o.inheritInsurance FROM accounts a
join operators o on a.id = o.id
WHERE a.status in ('Active','Pending'));

update temp_cao_conversion set help = '' where help is null;

insert into audit_category (auditTypeID, name, parentID, number, numRequired, numQuestions, createdBy, updatedBy, creationDate, updateDate, legacyID)
select t.auditTypeID, a.name, acp.id parentID, 3, 2, 2, 941, 941, now(), now(), t.id
from temp_cao_conversion t
join audit_category acp on acp.auditTypeID = t.auditTypeID and acp.parentID is null
join accounts a on a.id = t.opID
order by t.auditTypeID, a.name;

insert into audit_question 
(categoryID, number, name, createdBy, updatedBy, creationDate, updateDate, effectiveDate, expirationDate, questionType, hasRequirement, required, columnHeader, groupedWithPrevious, flaggable, showComment)
select ac.id, 1, 'Upload a Certificate of Insurance or other supporting documentation for this policy.', 941, 941, now(), now(), '2001-01-01', '4000-01-01', 'FileCertificate', 0, 1, 'Certificate', 0, 0, 0
from temp_cao_conversion t
join audit_category ac on ac.legacyID = t.id
join audit_type aType on aType.id = ac.auditTypeID and aType.classType = 'Policy';


insert into audit_question 
(categoryID, number, name, createdBy, updatedBy, creationDate, updateDate, effectiveDate, expirationDate, questionType, hasRequirement, required, columnHeader, groupedWithPrevious, flaggable, showComment)
select ac.id, 2, concat('This insurance policy complies with all additional ', trim(a.name), ' requirements. ', t.help), 941, 941, now(), now(), '2001-01-01', '4000-01-01', 'Yes/No', 0, 1, 'Certificate', 0, 0, 0
from temp_cao_conversion t
join audit_category ac on ac.legacyID = t.id
join accounts a on t.opID = a.id
join audit_type aType on aType.id = ac.auditTypeID and aType.classType = 'Policy';

delete from audit_category
where id in (select cid from temp_single_subcats);

drop table temp_single_subcats;
