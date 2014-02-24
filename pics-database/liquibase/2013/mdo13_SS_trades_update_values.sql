--liquibase formatted sql

--changeset mdo:13
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
UPDATE ref_trade
SET safetySensitive = CASE WHEN safetyRisk IS NULL THEN NULL WHEN safetyRisk > 1 THEN 1 ELSE 0 END;
