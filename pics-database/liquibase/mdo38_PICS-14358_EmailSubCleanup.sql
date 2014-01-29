--liquibase formatted sql

--changeset mdo:38
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
DELETE es.* FROM email_subscription es
JOIN email_subscription e ON es.userID = e.userID AND es.subscription = e.subscription AND es.id < e.id
WHERE es.reportID IS NULL;

ALTER TABLE `email_subscription`
  CHANGE `reportID` `reportID` INT(11) DEFAULT 0  NOT NULL;
