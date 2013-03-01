select ci.id as 'Contractor ID', a.name as 'Contractor Name', a.status as 'Contractor Status', 
if (a2.id is null, 'No Client Site Selected Yet', a2.id) as 'Client Site ID', 
if (a2.name is null, 'No Client Site Selected Yet', a2.name) as 'First Client Site' 
from contractor_info ci 
join accounts a on a.id = ci.id 
left join (select gc.id, gc.genID, gc.subID from generalcontractors gc 
join (select gc2.subID, min(gc2.creationDate) as cDate from generalcontractors gc2 group by gc2.subID) as gcMin 
on gc.subID = gcMin.subID and gc.creationDate = gcMin.cDate) as firstSite on firstSite.subID = ci.id 
left join accounts a2 on a2.id = firstSite.genID and a2.type = 'Operator'
-- left join operators o on o.id = a2.id 
where ci.requestedByID is null 
and a.status in ('Active', 'Pending') 
and a.creationDate >= '2012-11-01';