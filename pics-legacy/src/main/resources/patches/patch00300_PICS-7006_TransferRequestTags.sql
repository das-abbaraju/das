insert IGNORE into contractor_tag (conID, tagID, createdBy, creationDate)
select distinct cr.conID, ot.id, 23157, now()
from contractor_registration_request cr
join operator_tag ot on FIND_IN_SET(ot.id, cr.operatorTags)
join generalcontractors gc on gc.subID = cr.conID and gc.genID = ot.opID
where cr.operatorTags > ''
and cr.conID is not null
and not exists (select * from contractor_tag ct where ct.tagID = ot.id and ct.conID = cr.conID)
order by cr.name;