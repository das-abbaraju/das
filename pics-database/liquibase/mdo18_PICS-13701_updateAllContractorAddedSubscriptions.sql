--liquibase formatted sql

--changeset mdo:18
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
UPDATE email_subscription SET subscription = 'DynamicReports', reportID = 1416, lastSent = NULL
WHERE 1
AND subscription = 'ContractorAdded'
AND timePeriod = 'Daily';

UPDATE email_subscription SET subscription = 'DynamicReports', reportID = 1586, lastSent = NULL
WHERE 1
AND subscription = 'ContractorAdded'
AND timePeriod = 'Weekly';

UPDATE email_subscription SET subscription = 'DynamicReports', reportID = 1587, lastSent = NULL
WHERE 1
AND subscription = 'ContractorAdded'
AND timePeriod = 'Monthly';
