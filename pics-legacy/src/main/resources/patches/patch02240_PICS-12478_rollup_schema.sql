Alter table audit_type add column parentID int(11) default NULL;
Alter table audit_type add column rollbackStatus varchar(30) default NULL;
Alter table audit_question_function modify column expression varchar(255);
