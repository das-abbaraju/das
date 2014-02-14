--liquibase formatted sql

--changeset mdo:47
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
DROP TABLE IF EXISTS temp_contractor_trade_ss;
CREATE TABLE temp_contractor_trade_ss AS
SELECT  contractor_trade.conID AS accountID, (CASE WHEN ISNULL(child.safetySensitive) THEN MAX(parent.safetySensitive) ELSE MAX(child.safetySensitive) END) AS tradeSafetySensitive, (CASE WHEN ISNULL(child.safetyRisk) THEN MAX(parent.safetyRisk) ELSE MAX(child.safetyRisk) END) AS tradeSafetyRisk
FROM contractor_trade
JOIN contractor_info ON contractor_info.id = contractor_trade.conID
JOIN ref_trade child ON contractor_trade.tradeID = child.id
JOIN ref_trade parent ON child.indexStart > parent.indexStart AND child.indexEnd < parent.indexEnd AND child.indexLevel > parent.indexLevel AND parent.safetySensitive IS NOT NULL
GROUP BY contractor_trade.conID
ORDER BY parent.indexLevel DESC;

UPDATE contractor_info c
JOIN temp_contractor_trade_ss s ON c.id = s.accountID
SET c.tradeSafetyRisk = s.tradeSafetyRisk, c.tradeSafetySensitive = s.tradeSafetySensitive;

DROP TABLE IF EXISTS temp_contractor_trade_ss;

