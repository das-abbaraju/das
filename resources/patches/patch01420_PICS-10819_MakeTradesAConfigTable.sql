-- split table
CREATE TABLE IF NOT EXISTS	`ref_trade_calc`
(
	`tradeID`	INT(10) UNSIGNED NOT NULL
,	`childCount` 	INT(10) UNSIGNED NOT NULL DEFAULT '0'
,	`childCountTotal`	 INT(10) UNSIGNED NOT NULL DEFAULT '0'
,	`contractorCount`	 INT(10) UNSIGNED NOT NULL DEFAULT '0'
)
	ENGINE=INNODB DEFAULT CHARSET=utf8
;

-- populate table
INSERT INTO	ref_trade_calc
(
	tradeID
,	childCount
,	childCountTotal
,	contractorCount
)
SELECT
	ref_trade.`id`
,	ref_trade.`childCount`
,	ref_trade.`childCountTotal`
,	ref_trade.`contractorCount`
FROM
	ref_trade
;

ALTER TABLE ref_trade_calc
ADD PRIMARY KEY (`tradeID`)
;
