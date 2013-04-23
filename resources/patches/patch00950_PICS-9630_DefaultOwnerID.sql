UPDATE report
SET ownerID = createdBy
WHERE createdBy IS NOT NULL AND ownerID = 0;

UPDATE report
SET ownerID = 82286
WHERE createdBy IS NULL AND ownerID = 0;