--liquibase formatted sql

--changeset mdo:25
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
UPDATE IGNORE email_subscription
SET subscription = 'DynamicReports', updatedBy = 37951, updateDate = NOW(), reportID = 1542
WHERE subscription = 'FlagChanges';

UPDATE IGNORE email_subscription
SET timePeriod = 'None', updatedBy = 37951, updateDate = NOW()
WHERE subscription = 'FlagChanges';
