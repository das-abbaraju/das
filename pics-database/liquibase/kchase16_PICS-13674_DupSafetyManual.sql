--liquibase formatted sql

--changeset kchase:16

CREATE TEMPORARY TABLE myDeletes
AS
SELECT d.id FROM pqfdata d
JOIN contractor_audit ca ON ca.id=d.auditID
JOIN accounts a ON a.id=ca.conID
WHERE ca.auditTypeID=1
AND ca.expiresDate < NOW()
AND d.questionID=1331
and d.answer is not null
AND a.status='Active'
;

DELETE pqfdata
FROM
	pqfdata
JOIN
	myDeletes
ON	myDeletes.id	= pqfdata.id
;