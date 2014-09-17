--liquibase formatted sql

--changeset mdo:45
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
UPDATE report_user ru
JOIN report r ON ru.reportID = r.id
JOIN users u ON ru.userID = u.id
JOIN accounts a ON u.accountID = a.id
JOIN facilities f ON ((a.id = f.opID AND f.corporateID = 6115) OR a.id = 6115) -- works at BASF
SET ru.favorite = 0
WHERE ru.favorite = 1 -- favorite
AND (ru.viewCount <= 5 OR lastViewedDate < DATE_SUB(NOW(), INTERVAL 1 MONTH)) -- not viewed enough
AND reportID IN (108,490,904,979,980,1233,1279,1299,1300,1414, -- forced flags
7,494,862,1659,1693, -- unresponsive companies
104,164, -- expiring manual audits
122 -- complete contractors
)
