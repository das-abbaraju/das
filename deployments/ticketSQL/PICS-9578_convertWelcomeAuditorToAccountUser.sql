insert into account_user
(accountID, userID, role, startDate, endDate, ownerPercent, createdBy, creationDate)
select ci.id, ci.welcomeAuditor_id, 'PICSCustomerServiceRep', now(), '4000-01-01', 100, 1, now()
from contractor_info ci join accounts a on a.id = ci.id
where a.status = 'Active' and ci.welcomeAuditor_id is not null
and not exists (select id from account_user where role = 'PICSCustomerServiceRep');