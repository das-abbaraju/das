ALTER TABLE `contractor_info`
	ADD COLUMN `safetyRiskVerified` DATE NULL AFTER `safetyRisk`,
	ADD COLUMN `productRiskVerified` DATE NULL AFTER `productRisk`;
