--liquibase formatted SQL

--changeset cfranks:6
UPDATE operators o
JOIN accounts a ON o.id = a.id
SET o.reportingID = get_parent_company(o.parentID)
WHERE a.status = 'Active'
AND o.reportingID IS NULL
AND o.parentID IS NOT NULL;