--liquibase formatted sql

--sql:
--changeset sshacter:41CREATE splitStatements:true endDelimiter:|
CREATE OR REPLACE DEFINER =`pics_admin`@`%` VIEW	vwAccountTradeSafetySensitivity
(
	accountID
,	accountTradID
,	tradeSafetySensitive
)
AS
(
	SELECT
		contractor_trade.conID	accountID
	,	child.id	accountTradID
	,	CASE
		WHEN
			child.safetySensitive IS NULL
		THEN
			MAX(parent.safetySensitive)
		ELSE
			MAX(child.safetySensitive)
		END 	tradeSafetySensitive
	FROM
		contractor_trade
	JOIN
		contractor_info
	ON	contractor_info.id	= contractor_trade.conID
	JOIN
		ref_trade	child
	ON	contractor_trade.tradeID	= child.id
	JOIN
		ref_trade 	parent
	ON	child.indexStart	> parent.indexStart
	AND	child.indexEnd		< parent.indexEnd
	AND	child.indexLevel	> parent.indexLevel
	AND	parent.safetySensitive IS NOT NULL
	WHERE	1=1
	GROUP BY
		contractor_trade.conID
	ORDER BY
		parent.indexLevel DESC
)
;
