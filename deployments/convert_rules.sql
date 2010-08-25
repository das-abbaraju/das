drop table temp_pqfopmatrix_full;

create table temp_pqfopmatrix_full as
select c.catID, o.opID, r.riskLevel, case when t.id is null then 0 else 1 end onFlag, 0 include
from (select distinct catID FROM pics_yesterday.pqfopmatrix m join pics_yesterday.pqfcategories t on m.catID = t.id) c
join (select distinct inheritAuditCategories opID FROM pics_yesterday.operators) o
join (select distinct riskLevel FROM pqfopmatrix) r
LEFT join pqfopmatrix t on t.catID = c.catID and t.opID = o.opID and t.riskLevel = r.riskLevel;


update temp_pqfopmatrix_full set include = 0; -- 75%
update temp_pqfopmatrix_full set include = 1 where catID IN (2,28,8,6,29,10,33,7,16,15,25,12); -- 85%
update temp_pqfopmatrix_full set include = 0 where catID = 25 and riskLevel = 1;
update temp_pqfopmatrix_full set include = 0 where catID = 7 and riskLevel = 1;
update temp_pqfopmatrix_full set include = 0 where catID = 15 and riskLevel = 1;
update temp_pqfopmatrix_full set include = 0 where catID = 16 and riskLevel = 1;
update temp_pqfopmatrix_full set include = 0 where catID = 12 and riskLevel = 1;
update temp_pqfopmatrix_full set include = 0 where catID = 33 and riskLevel = 1;
update temp_pqfopmatrix_full set include = 1 where catID = 11 and riskLevel = 3;
update temp_pqfopmatrix_full set include = 1 where catID = 18 and riskLevel = 3;
update temp_pqfopmatrix_full set include = 1 where catID = 18 and riskLevel = 2;
update temp_pqfopmatrix_full set include = 1 where catID = 32 and riskLevel = 3;
update temp_pqfopmatrix_full set include = 1 where catID = 32 and riskLevel = 2;
update temp_pqfopmatrix_full set include = 1 where catID = 17 and riskLevel = 3;
update temp_pqfopmatrix_full set include = 1 where catID = 17 and riskLevel = 2; -- 91%

select valid/total from (select count(*) valid from temp_pqfopmatrix_full where onFlag = include) v
join (select count(*) total from temp_pqfopmatrix_full) t;

select concat('update temp_pqfopmatrix_full set include = ',include,' where catID = ',catID,' and riskLevel = ',risk,';') from audit_cat_dt where catID > 0 and risk > 0 and opID is null;


insert into audit_category_rule (priority, include, catID)
select 20, 1, catID from (
select catID, count(*)/906 total from temp_pqfopmatrix_full
where include != onFlag
group by catID
having total > .5) t
order by total desc;

select onFlag, opID, catID, count(*)/3 total from temp_pqfopmatrix_full
where include != onFlag
group by opID, catID
having total > .5
order by total desc;

insert into audit_cat_dt (priority, include, risk, catID, tempTotal)
select 30, onFlag, riskLevel, catID, count(*)/302 total from temp_pqfopmatrix_full
where include != onFlag
group by riskLevel, catID
having total > .5
order by total desc;

select count(distinct opID) from temp_pqfopmatrix_full

select riskLevel, count(*)/13288 total from temp_pqfopmatrix_full
where include != onFlag
group by riskLevel
-- having total > .5
order by total desc;



insert into audit_category_rule (auditTypeID, catID, opID, risk, include)
select 1, catID, opID, riskLevel, onFlag from temp_pqfopmatrix_full;

insert into audit_category_rule (auditTypeID, catID, questionID, include)
select 2, categoryID, questionID, 1 from desktopmatrix;

insert into audit_type_rule (auditTypeID, opID, risk, tagID, include)
select auditTypeID, opID, null, tagID, canSee from audit_operator
where minRiskLevel = 1;

insert into audit_type_rule (auditTypeID, opID, risk, tagID, include)
select auditTypeID, opID, 2, tagID, canSee from audit_operator
where minRiskLevel = 2;

insert into audit_type_rule (auditTypeID, opID, risk, tagID, include)
select auditTypeID, opID, 2, tagID, canSee from audit_operator
where minRiskLevel = 3;

insert into audit_type_rule (auditTypeID, opID, risk, tagID, include)
select auditTypeID, opID, 3, tagID, canSee from audit_operator
where minRiskLevel = 3;

update audit_category_rule set priority = 0, effectiveDate = '2000-01-01', expirationDate = '4000-01-01', createdBy = 1, creationDate = now(), updatedBy = 1, updateDate = now();
update audit_category_rule set priority = priority + 105 where auditTypeID > 0 OR catID > 0;
update audit_category_rule set priority = priority + 102 where risk > 0;
update audit_category_rule set priority = priority + 104 where opID > 0;
update audit_category_rule set priority = priority + 130 where tagID > 0;

update audit_type_rule set priority = 0, effectiveDate = '2000-01-01', expirationDate = '4000-01-01', createdBy = 1, creationDate = now(), updatedBy = 1, updateDate = now();

update audit_type_rule set priority = priority + 105 where auditTypeID > 0;
update audit_type_rule set priority = priority + 102 where risk > 0;
update audit_type_rule set priority = priority + 104 where opID > 0;
update audit_type_rule set priority = priority + 130 where tagID > 0;

delete from audit_category_rule where opID > 0 and opID not in (select id from accounts);

-- levels --
update audit_category_rule a1 set level = (if(catID is null, 0, 1) + if(auditTypeID is null, 0, 1) +
if(risk is null, 0, 1) + if(opID is null, 0, 1) +
if(tagID is null, 0, 1) + if(questionID is null, 0, 1) +
if(contractorType is null, 0, 1) + if(acceptsBids is null, 0, 1));
