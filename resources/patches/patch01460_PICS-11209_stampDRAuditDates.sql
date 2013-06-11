UPDATE report r
JOIN report_user ru ON r.id = ru.`reportID` AND ru.creationDate = (SELECT MIN(cr.`creationDate`) FROM report_user cr WHERE cr.`reportID` = ru.reportID)
SET r.createdBy = ru.`userID`, r.`creationDate` = ru.`creationDate`;