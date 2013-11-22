--liquibase formatted sql

--changeset mdo:10
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
UPDATE contractor_info
SET safetySensitive = CASE WHEN safetyRisk > 1 THEN 1 ELSE 0 END;
