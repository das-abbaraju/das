drop table flags_july18;
alter table audit_type drop column legacyCode;
alter table certificates drop column operator;
alter table contractor_audit drop column canDelete;

alter table contractor_info drop column certs;
alter table contractor_info drop column welcomeEmailDate;
alter table contractor_info drop column welcomeCallDate;
alter table pqfdata drop column conID;

insert into users (id, username, isGroup, email, name, isActive, dateCreated, accountID)
select id, CONCAT(id, name), 'No', email, name, 'No', dateCreated, accountID from deletedusers;

drop table deletedusers;

drop table form_categories;
drop table forms;

drop table opaccess;
drop table requirements;
drop table rules_row;
drop table statelicenses;

alter table loginlog drop column company;
alter table loginlog drop column type;

//alter table contractor_info.isOnlyCerts
//alter table contractor_info.isExempt

