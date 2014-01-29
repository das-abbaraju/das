--liquibase formatted sql

--changeset mdo:25
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
UPDATE IGNORE email_subscription
SET subscription = 'DynamicReports', updatedBy = 37951, updateDate = NOW(), reportID = 1542
WHERE subscription = 'FlagChanges';

UPDATE email_subscription es
JOIN email_subscription e ON es.userID = e.userID AND es.subscription = 'FlagChanges' AND e.subscription = 'DynamicReports' AND e.reportID = 1542
AND es.timePeriod != 'None'
SET e.timePeriod = es.timePeriod, e.updatedBy = 37951, e.updateDate = NOW(), es.timePeriod = 'None', es.updatedBy = 37951, es.updateDate = NOW();
