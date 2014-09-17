--liquibase formatted sql

--changeset kchase:1
-- update Johnson & Johnson contractors work status
-- original script
-- update generalcontractors gc
-- set gc.workStatus='Y'
-- where gc.workStatus='P'
-- and gc.genID=41063;

update
generalcontractors gc
join accounts a on gc.genID = a.id
join accounts con on gc.subID = con.id
join operators o on a.id = o.id
set gc.workStatus = 'Y'
where gc.workStatus='P'
and a.autoApproveRelationships = 1
and a.status = 'Active'
and con.status = 'Active'
and o.inPicsConsortium = 0
;