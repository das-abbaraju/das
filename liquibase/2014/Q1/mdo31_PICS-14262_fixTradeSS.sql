--liquibase formatted sql

--changeset mdo:31
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
UPDATE
ref_trade rt
SET safetySensitive = CASE WHEN safetyRisk > 1 THEN 1 WHEN safetyRisk <= 1 THEN 0 ELSE NULL END
