-- PICS-2636
update accounts a
join contractor_info c on a.id = c.id
set c.renew = 0
where a.status = 'Deactivated' and c.renew = 1 and a.updateDate > '2011-06-01';
--