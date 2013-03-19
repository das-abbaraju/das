CREATE TABLE tmp_deactivated_accounts
select
	accounts.id
,	accounts.type
,	accounts.name
,	accounts.status
,	accounts.deactivationDate
,	contractor_info.lastRecalculation
,	accounts.updateDate
,	accounts.updatedBy
from accounts
join contractor_info
on contractor_info.id = accounts.id
and accounts.status = 'Deactivated'
AND accounts.deactivationDate is null
and contractor_info.lastRecalculation is not null
order by accounts.id
;
UPDATE accounts
join contractor_info
on contractor_info.id = accounts.id
and accounts.status = 'Deactivated'
and contractor_info.lastRecalculation is not null
AND accounts.deactivationDate is null
SET
	accounts.deactivationDate = contractor_info.lastRecalculation
,	accounts.deactivatedBy = 941
,	accounts.updateDate = now()
,	accounts.updatedBy = 941
;
