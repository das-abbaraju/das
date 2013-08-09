-- set Suncor Corporate
UPDATE accounts
SET autoApproveRelationships=1
WHERE id=10566;

-- update work status
UPDATE generalcontractors gc
SET gc.workStatus = 'Y'
WHERE gc.workStatus = 'P'
      AND (gc.genID=10566 OR gc.genID IN (SELECT f.opID FROM facilities f
WHERE f.corporateID=10566
      AND f.opID != 3716 AND f.opID != 20186));