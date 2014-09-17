--liquibase formatted sql

--changeset kchase:9
-- make desired cats invisible
update  audit_cat_data
set applies=0
where auditId=1066927 and categoryID in (4605 , 4617, 4618, 4606, 4598, 4604, 4603, 4602, 4601, 4600, 4599);

-- update audity to recalc
update contractor_audit
set lastRecalculation=null
where id=1066927;

-- update contractor to run through cron
update contractor_info
set lastRecalculation=null
where id=50415;