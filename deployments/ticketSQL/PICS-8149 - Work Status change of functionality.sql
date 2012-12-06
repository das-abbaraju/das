ALTER TABLE `generalcontractors` CHANGE `workStatus` `workStatus` varchar(2)  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'P' after `updateDate`, COMMENT=''; 

insert into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, lastUsed, qualityRating, applicable, sourceLanguage, contentDriven)
select 	concat(msgKey,'F'), locale, msgValue, createdBy, updatedBy, creationDate, updateDate, lastUsed, qualityRating, applicable, sourceLanguage, contentDriven
from app_translation 
where locale = 'en'
and (msgKey = 'ApprovalStatus.N' or msgKey = 'ApprovalStatus.Y');

update accounts a
join generalcontractors gc on a.id = gc.genID
set workStatus = concat(workStatus,'F')
where a.type = 'Corporate'
and a.id not in (4,5,6,7,8)
and gc.workStatus in ('Y','N');

update accounts a
join generalcontractors gc on a.id = gc.genID
join facilities f on a.id = f.corporateID
join accounts op on f.opID = op.id
join operators o on op.id = o.id and o.approvesRelationships = 'Yes'
join generalcontractors opgc on op.id = opgc.genID and gc.subID = opgc.subID
set gc.workStatus = 'Y'
where a.type = 'Corporate'
and a.id not in (4,5,6,7,8)
and gc.workStatus not in ('YF','NF')
and opgc.workStatus = 'Y'
and gc.workStatus != 'Y';

update accounts a
join generalcontractors gc on a.id = gc.genID
join facilities f on a.id = f.corporateID
join accounts op on f.opID = op.id
join operators o on op.id = o.id and o.approvesRelationships = 'Yes'
join generalcontractors opgc on op.id = opgc.genID and gc.subID = opgc.subID
set gc.workStatus = 'N'
where a.type = 'Corporate'
and a.id not in (4,5,6,7,8)
and gc.workStatus not in ('YF','NF','Y')
and opgc.workStatus = 'N'
and gc.workStatus != 'N';
