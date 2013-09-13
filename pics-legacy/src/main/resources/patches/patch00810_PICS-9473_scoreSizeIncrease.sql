ALTER TABLE `contractor_audit` 
	CHANGE `score` `score` mediumint(7)   NULL DEFAULT '0' after `lastRecalculation`, COMMENT='';