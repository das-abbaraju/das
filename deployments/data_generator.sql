truncate table employee;

insert into employee (firstName, lastName, accountID)
select * from 
(select * from test.random_names 
order by rand() limit 1000) n, 
(select distinct subID from generalcontractors
where genID IN (select opID from facilities where corporateID = 1336)
) t
Order by rand() limit 20000

insert into employee (firstName, lastName, accountID)
select firstName, lastName, 1450 from test.random_names 
order by rand() limit 50;

update employee set createdBy = 1, updatedBy = 1, creationDate = now(), updateDate = now();
update employee set classification = 'FullTime';
update employee set classification = 'PartTime' where mod(id,3) = 0;
update employee set classification = 'Contract' where mod(id,4) = 0;

update employee set active = 1;
update employee set active = 0 where mod(id,7) = 0;

truncate table employee_site;
insert into employee_site
select null, e.id, f.opID, null, 941, 941, now(), now() from employee e, facilities f
where f.corporateID = 1336
order by rand()
limit 20000;

truncate table employee_role;
insert into employee_role
select null, e.id, t.id, 1, 1, now(), now()
from employee e, job_role t
where t.accountID = e.accountID
order by rand()
limit 100;

