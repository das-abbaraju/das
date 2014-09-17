--liquibase formatted sql

--changeset cfranks:1
UPDATE operators o
JOIN accounts a on o.id = a.id 
SET o.reportingID = o.parentID
WHERE a.status = 'Active' 
AND o.reportingID IS NULL
AND o.parentID IS NOT NULL;