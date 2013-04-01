update users u
set u.firstName = left(trim(left(u.name, locate(substring_index(trim(replace(left(u.name, locate(' jr', u.name)), ',', '')), ' ', -1), u.name))), 50),
u.lastName = left(substring(u.name, locate(substring_index(trim(replace(left(u.name, locate(' jr', u.name)), ',', '')), ' ', -1), u.name)), 50),
u.updateDate = now(),
u.updatedBy = 23157
where u.name like '% jr%'
and u.firstName is null
and u.lastName is null
and u.isActive = 'Yes'
and u.isGroup = 'No';

update users u
set u.firstName = left(trim(left(u.name, locate(substring_index(trim(replace(left(u.name, locate(' sr', u.name)), ',', '')), ' ', -1), u.name))), 50),
u.lastName = left(substring(u.name, locate(substring_index(trim(replace(left(u.name, locate(' sr', u.name)), ',', '')), ' ', -1), u.name)), 50),
u.updateDate = now(),
u.updatedBy = 23157
where u.name like '% sr%'
and u.firstName is null
and u.lastName is null
and u.isActive = 'Yes'
and u.isGroup = 'No';

update users u
set u.firstName = left(trim(substring_index(u.name, ',', 1)), 50),
u.lastName = left(trim(substring_index(u.name, ',', -1)), 50),
u.updateDate = now(),
u.updatedBy = 23157
where u.name like '%,%'
and u.name not like '%(%)%'
and u.firstName is null
and u.lastName is null
and u.isActive = 'Yes'
and u.isGroup = 'No';

update users u
set u.firstName = left(trim(left(u.name, locate(substring_index(trim(u.name), ' ', -1), u.name) - 1)), 50),
u.lastName = left(substring_index(trim(u.name), ' ', -1), 50),
u.updateDate = now(),
u.updatedBy = 23157
where u.name not like 'Account%'
and u.name not like '%Admin%'
and u.name not like 'test%'
and u.name not like 'Customer Service%'
and u.isActive = 'Yes'
and u.isGroup = 'No';

update users u
set u.firstName = u.lastName,
u.lastName = null,
u.updateDate = now(),
u.updatedBy = 23157
where u.lastName is not null
and length(u.firstName) = 0;

update users u
set u.firstName = left(u.name, 50),
u.updateDate = now(),
u.updatedBy = 23157
where u.firstName is null
and u.lastName is null
and u.isActive = 'Yes'
and u.isGroup = 'No';