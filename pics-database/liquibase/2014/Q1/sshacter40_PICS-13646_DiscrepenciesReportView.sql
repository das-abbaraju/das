--liquibase formatted sql

--sql:
--changeset sshacter:40DROP splitStatements:true endDelimiter:|
DROP VIEW IF EXISTS vwAccountTradeSafetySensitivity;
--sql:
--changeset sshacter:40CREATE splitStatements:true endDelimiter:|
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
			parent.safetySensitive
		ELSE
			child.safetySensitive
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
	,	contractor_info.safetySensitive
	HAVING
		MAX(tradeSafetySensitive)	= FALSE
	ORDER BY
		parent.indexLevel DESC
)
;
