delete from useraccess
where accessType IN ('ManageCategoryRules','ManageAuditTypeRules','AuditRuleAdmin');

insert into useraccess VALUES (null, 10801, 'ManageCategoryRules', 1, 1, 1, NULL, now(), 941);
insert into useraccess VALUES (null, 10801, 'ManageAuditTypeRules', 1, 1, 1, NULL, now(), 941);
insert into useraccess VALUES (null, 981, 'ManageCategoryRules', 1, 1, 1, 1, now(), 941);
insert into useraccess VALUES (null, 981, 'ManageAuditTypeRules', 1, 1, 1, 1, now(), 941);
insert into useraccess VALUES (null, 981, 'AuditRuleAdmin', 1, null, null, 1, now(), 941);
