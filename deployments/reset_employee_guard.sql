start transaction;

-- FILL pics_temp
USE pics_temp;
insert into contractor_audit
select * from pics_config.contractor_audit where auditTypeID IN (17,29);

insert into pqfdata
select * from pics_config.pqfdata where auditID IN (select id from pics_temp.contractor_audit);

insert into audit_cat_data
select * from pics_config.audit_cat_data where auditID IN (select id from pics_temp.contractor_audit);

insert into contractor_audit_operator
select * from pics_config.contractor_audit_operator where auditID IN (select id from pics_temp.contractor_audit);

insert into contractor_audit_operator_permission
select * from pics_config.contractor_audit_operator_permission where caoID IN (select id from pics_temp.contractor_audit_operator);

insert into contractor_audit_operator_workflow
select * from pics_config.contractor_audit_operator_workflow where caoID IN (select id from pics_temp.contractor_audit_operator);


-- DROP FROM Alpha2
USE pics_alpha2;
delete from pqfdata where auditID IN (select id from contractor_audit where auditTypeID IN (17,29));
delete from contractor_audit where auditTypeID IN (17,29);

-- REMOVE PKEY conflicts
delete t from pics_alpha2.contractor_audit t JOIN pics_temp.contractor_audit USING (id);
delete t from pics_alpha2.pqfdata t JOIN pics_temp.pqfdata USING (id);
delete t from pics_alpha2.audit_cat_data t JOIN pics_temp.audit_cat_data USING (id);
delete t from pics_alpha2.contractor_audit_operator t JOIN pics_temp.contractor_audit_operator USING (id);
delete t from pics_alpha2.contractor_audit_operator_permission t JOIN pics_temp.contractor_audit_operator_permission USING (id);
delete t from pics_alpha2.contractor_audit_operator_workflow t JOIN pics_temp.contractor_audit_operator_workflow USING (id);

-- INSERT INTO ALPHA2
insert into contractor_audit
select * from pics_temp.contractor_audit;

insert into pqfdata
select * from pics_temp.pqfdata;

insert into audit_cat_data
select * from pics_temp.audit_cat_data;

insert into contractor_audit_operator
select * from pics_temp.contractor_audit_operator;

insert into contractor_audit_operator_permission
select * from pics_temp.contractor_audit_operator_permission;

insert into contractor_audit_operator_workflow
select * from pics_temp.contractor_audit_operator_workflow;

-- commit;
