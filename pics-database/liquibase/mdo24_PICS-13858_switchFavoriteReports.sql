--liquibase formatted sql

--changeset mdo:24
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
UPDATE IGNORE report_user
SET reportID = 490
WHERE reportID = 108;

UPDATE report_user ru
JOIN report_user ru2 ON ru.userID = ru2.userID
SET ru2.favorite = ru.favorite, ru2.sortOrder = ru.sortOrder, ru2.hidden = ru.hidden, ru2.pinnedIndex = ru.pinnedIndex,
ru.favorite = ru2.favorite, ru.sortOrder = ru2.sortOrder, ru.hidden = ru2.hidden, ru.pinnedIndex = ru2.pinnedIndex
WHERE ru.reportID = 108
AND ru2.reportID = 490;

UPDATE report
SET deleted = 1
WHERE id = 108;
