--liquibase formatted sql

--changeset lkam:2
INSERT IGNORE INTO contractor_tag(conID,tagID,createdBy,updatedBy,creationDate,updateDate)
SELECT a.id,1647,37951,37951,NOW(),NOW()
FROM accounts a
JOIN contractor_info c ON a.id = c.id
JOIN contractor_fee cf ON a.id = cf.conID AND cf.feeClass = 'AuditGUARD' AND cf.currentAmount = 0
WHERE a.onsiteServices = 1 AND c.safetySensitive = 1
AND a.status = 'Active' AND c.accountLevel = 'Full'
AND c.paymentExpires < '2014-01-01';
