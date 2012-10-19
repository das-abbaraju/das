-- tag all Canadian contractors for open task
update
contractor_info ci
join accounts a on a.id=ci.id
set lcCorPhase='RemindMeLater', lcCorNotification=Now()
where a.country='CA'
and a.status='Active';
