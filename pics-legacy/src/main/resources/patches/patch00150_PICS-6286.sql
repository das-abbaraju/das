/* Foreign Keys must be dropped in the target to ensure that requires changes can be done*/

ALTER TABLE `contractor_info` DROP FOREIGN KEY `FK_contractor_info` ;


/* Alter table in target */
ALTER TABLE `accounts` 
	CHANGE `countrySubdivision` `countrySubdivision` varchar(10)  COLLATE latin1_swedish_ci NULL after `city`, 
	CHANGE `zip` `zip` varchar(15)  COLLATE latin1_swedish_ci NULL after `countrySubdivision`, 
	CHANGE `country` `country` varchar(25)  COLLATE latin1_swedish_ci NULL after `zip`, 
	CHANGE `phone` `phone` varchar(30)  COLLATE latin1_swedish_ci NULL after `country`, 
	CHANGE `phone2` `phone2` varchar(35)  COLLATE latin1_swedish_ci NULL after `phone`, 
	CHANGE `fax` `fax` varchar(30)  COLLATE latin1_swedish_ci NULL after `phone2`, 
	CHANGE `contactID` `contactID` mediumint(9)   NULL after `fax`, 
	CHANGE `email` `email` varchar(50)  COLLATE latin1_swedish_ci NULL after `contactID`, 
	CHANGE `web_URL` `web_URL` varchar(50)  COLLATE latin1_swedish_ci NULL after `email`, 
	CHANGE `mainTradeID` `mainTradeID` int(11)   NULL after `web_URL`, 
	CHANGE `industryID` `industryID` int(11)   NULL after `mainTradeID`, 
	CHANGE `industry` `industry` varchar(50)  COLLATE latin1_swedish_ci NULL after `industryID`, 
	CHANGE `naics` `naics` varchar(10)  COLLATE latin1_swedish_ci NOT NULL DEFAULT '0' after `industry`, 
	CHANGE `naicsValid` `naicsValid` tinyint(4)   NOT NULL DEFAULT '0' after `naics`, 
	CHANGE `dbaName` `dbaName` varchar(400)  COLLATE latin1_swedish_ci NULL after `naicsValid`, 
	CHANGE `nameIndex` `nameIndex` varchar(50)  COLLATE latin1_swedish_ci NULL after `dbaName`, 
	CHANGE `reason` `reason` varchar(100)  COLLATE latin1_swedish_ci NULL after `nameIndex`, 
	CHANGE `acceptsBids` `acceptsBids` tinyint(4)   NOT NULL DEFAULT '0' after `reason`, 
	CHANGE `description` `description` text  COLLATE latin1_swedish_ci NULL after `acceptsBids`, 
	CHANGE `requiresOQ` `requiresOQ` tinyint(4) unsigned   NOT NULL DEFAULT '0' after `description`, 
	CHANGE `requiresCompetencyReview` `requiresCompetencyReview` tinyint(4) unsigned   NOT NULL DEFAULT '0' after `requiresOQ`, 
	CHANGE `needsIndexing` `needsIndexing` tinyint(4) unsigned   NOT NULL DEFAULT '1' after `requiresCompetencyReview`, 
	CHANGE `onsiteServices` `onsiteServices` tinyint(4) unsigned   NOT NULL DEFAULT '0' after `needsIndexing`, 
	CHANGE `transportationServices` `transportationServices` tinyint(4) unsigned   NOT NULL DEFAULT '0' after `onsiteServices`, 
	CHANGE `offsiteServices` `offsiteServices` tinyint(4) unsigned   NOT NULL DEFAULT '0' after `transportationServices`, 
	CHANGE `materialSupplier` `materialSupplier` tinyint(4) unsigned   NOT NULL DEFAULT '0' after `offsiteServices`, 
	CHANGE `generalContractor` `generalContractor` tinyint(4)   NOT NULL DEFAULT '0' after `materialSupplier`, 
	CHANGE `autoApproveRelationships` `autoApproveRelationships` tinyint(4)   NOT NULL DEFAULT '1' after `generalContractor`, 
	CHANGE `accreditation` `accreditation` date   NULL after `autoApproveRelationships`, 
	CHANGE `parentID` `parentID` int(11)   NULL after `accreditation`, 
	CHANGE `currencyCode` `currencyCode` char(3)  COLLATE latin1_swedish_ci NULL DEFAULT 'USD' after `parentID`, 
	CHANGE `qbListID` `qbListID` varchar(25)  COLLATE latin1_swedish_ci NULL after `currencyCode`, 
	CHANGE `qbListCAID` `qbListCAID` varchar(25)  COLLATE latin1_swedish_ci NULL after `qbListID`, 
	CHANGE `qbListUKID` `qbListUKID` varchar(25)  COLLATE latin1_swedish_ci NULL after `qbListCAID`, 
	CHANGE `qbListEUID` `qbListEUID` varchar(25)  COLLATE latin1_swedish_ci NULL after `qbListUKID`, 
	CHANGE `qbSync` `qbSync` tinyint(4)   NOT NULL DEFAULT '1' after `qbListEUID`, 
	CHANGE `locale` `locale` varchar(5)  COLLATE latin1_swedish_ci NULL DEFAULT 'en' after `qbSync`, 
	CHANGE `timezone` `timezone` varchar(50)  COLLATE latin1_swedish_ci NULL after `locale`, 
	DROP COLUMN `state`, COMMENT='';

/* Alter table in target */
ALTER TABLE `auditor_availability` 
	ADD COLUMN `onlyInCountrySubdivisions` varchar(100)  COLLATE latin1_swedish_ci NULL after `webOnly`, 
	DROP COLUMN `onlyInStates`, COMMENT='';

/* Alter table in target */
ALTER TABLE `contractor_audit` 
	CHANGE `countrySubdivision` `countrySubdivision` varchar(10)  COLLATE latin1_swedish_ci NULL after `city`, 
	CHANGE `zip` `zip` varchar(10)  COLLATE latin1_swedish_ci NULL after `countrySubdivision`, 
	CHANGE `country` `country` varchar(50)  COLLATE latin1_swedish_ci NULL after `zip`, 
	CHANGE `latitude` `latitude` float   NOT NULL DEFAULT '0' after `country`, 
	CHANGE `longitude` `longitude` float   NOT NULL DEFAULT '0' after `latitude`, 
	CHANGE `paidDate` `paidDate` date   NULL after `longitude`, 
	CHANGE `ruleID` `ruleID` int(11)   NULL after `paidDate`, 
	DROP COLUMN `state`, COMMENT='';

/* Alter table in target */
ALTER TABLE `contractor_info` 
	CHANGE `billingCountrySubdivision` `billingCountrySubdivision` varchar(10)  COLLATE latin1_swedish_ci NULL after `billingCity`, 
	CHANGE `billingZip` `billingZip` varchar(10)  COLLATE latin1_swedish_ci NULL after `billingCountrySubdivision`, 
	CHANGE `billingCountry` `billingCountry` varchar(25)  COLLATE latin1_swedish_ci NULL after `billingZip`, 
	CHANGE `membershipDate` `membershipDate` date   NULL after `billingCountry`, 
	CHANGE `payingFacilities` `payingFacilities` smallint(5) unsigned   NOT NULL DEFAULT '0' after `membershipDate`, 
	CHANGE `welcomeAuditor_id` `welcomeAuditor_id` mediumint(8) unsigned   NULL after `payingFacilities`, 
	ADD COLUMN `insideSalesPriority` tinyint(3)   NULL after `welcomeAuditor_id`, 
	CHANGE `safetyRisk` `safetyRisk` tinyint(3)   NULL DEFAULT '2' after `insideSalesPriority`, 
	CHANGE `safetyRiskVerified` `safetyRiskVerified` date   NULL after `safetyRisk`, 
	CHANGE `productRisk` `productRisk` tinyint(3)   NULL DEFAULT '2' after `safetyRiskVerified`, 
	CHANGE `productRiskVerified` `productRiskVerified` date   NULL after `productRisk`, 
	CHANGE `transportationRisk` `transportationRisk` tinyint(3)   NULL DEFAULT '2' after `productRiskVerified`, 
	CHANGE `transportationRiskVerified` `transportationRiskVerified` date   NULL after `transportationRisk`, 
	CHANGE `riskLevel` `riskLevel` tinyint(3)   NULL DEFAULT '2' after `transportationRiskVerified`, 
	CHANGE `viewedFacilities` `viewedFacilities` datetime   NULL after `riskLevel`, 
	CHANGE `paymentMethod` `paymentMethod` varchar(20)  COLLATE latin1_swedish_ci NULL DEFAULT 'CreditCard' after `viewedFacilities`, 
	CHANGE `paymentMethodStatus` `paymentMethodStatus` varchar(20)  COLLATE latin1_swedish_ci NULL after `paymentMethod`, 
	CHANGE `membershipLevelID` `membershipLevelID` smallint(6)   NULL after `paymentMethodStatus`, 
	CHANGE `newMembershipLevelID` `newMembershipLevelID` smallint(6)   NULL after `membershipLevelID`, 
	CHANGE `renew` `renew` tinyint(4)   NULL DEFAULT '1' after `newMembershipLevelID`, 
	CHANGE `lastUpgradeDate` `lastUpgradeDate` date   NULL after `renew`, 
	CHANGE `lastContactedByAutomatedEmailDate` `lastContactedByAutomatedEmailDate` datetime   NULL after `lastUpgradeDate`, 
	CHANGE `lastContactedByInsideSales` `lastContactedByInsideSales` int(11)   NULL after `lastContactedByAutomatedEmailDate`, 
	CHANGE `lastContactedByInsideSalesDate` `lastContactedByInsideSalesDate` datetime   NULL after `lastContactedByInsideSales`, 
	CHANGE `followupDate` `followupDate` datetime   NULL after `lastContactedByInsideSalesDate`, 
	CHANGE `contactCountByEmail` `contactCountByEmail` tinyint(4) unsigned   NOT NULL DEFAULT '0' after `followupDate`, 
	CHANGE `contactCountByPhone` `contactCountByPhone` tinyint(4) unsigned   NOT NULL DEFAULT '0' after `contactCountByEmail`, 
	ADD COLUMN `expiresOnDate` date   NULL after `contactCountByPhone`, 
	CHANGE `balance` `balance` decimal(9,2)   NULL DEFAULT '0.00' after `expiresOnDate`, 
	CHANGE `needsRecalculation` `needsRecalculation` tinyint(4)   NOT NULL DEFAULT '1' after `balance`, 
	CHANGE `lastRecalculation` `lastRecalculation` datetime   NULL after `needsRecalculation`, 
	CHANGE `ccOnFile` `ccOnFile` tinyint(4)   NOT NULL DEFAULT '0' after `lastRecalculation`, 
	CHANGE `ccExpiration` `ccExpiration` date   NULL after `ccOnFile`, 
	CHANGE `ccEmail` `ccEmail` varchar(50)  COLLATE latin1_swedish_ci NULL after `ccExpiration`, 
	CHANGE `emrAverage` `emrAverage` decimal(8,3)   NULL after `ccEmail`, 
	CHANGE `trirAverage` `trirAverage` decimal(8,3)   NULL after `emrAverage`, 
	CHANGE `lwcrAverage` `lwcrAverage` decimal(8,3)   NULL after `trirAverage`, 
	CHANGE `tradesSelf` `tradesSelf` varchar(4000)  COLLATE latin1_swedish_ci NULL after `lwcrAverage`, 
	CHANGE `tradesSub` `tradesSub` varchar(4000)  COLLATE latin1_swedish_ci NULL after `tradesSelf`, 
	CHANGE `agreementDate` `agreementDate` datetime   NULL after `tradesSub`, 
	CHANGE `agreedBy` `agreedBy` int(11)   NULL after `agreementDate`, 
	CHANGE `score` `score` int(10) unsigned   NOT NULL DEFAULT '0' after `agreedBy`, 
	CHANGE `tradesUpdated` `tradesUpdated` datetime   NULL after `score`, 
	CHANGE `soleProprietor` `soleProprietor` tinyint(3)   NULL after `tradesUpdated`, 
	CHANGE `competitorMembership` `competitorMembership` tinyint(4)   NULL after `soleProprietor`, 
	CHANGE `accountLevel` `accountLevel` varchar(20)  COLLATE latin1_swedish_ci NULL after `competitorMembership`, 
	CHANGE `hasCanadianCompetitor` `hasCanadianCompetitor` tinyint(4)   NULL after `accountLevel`, 
	CHANGE `showInDirectory` `showInDirectory` tinyint(4)   NULL DEFAULT '1' after `hasCanadianCompetitor`, 
	CHANGE `reviewedContractorBadge` `reviewedContractorBadge` tinyint(4)   NOT NULL DEFAULT '0' after `showInDirectory`, 
	CHANGE `lcCorPhase` `lcCorPhase` varchar(20)  COLLATE latin1_swedish_ci NULL after `reviewedContractorBadge`, 
	CHANGE `lcCorNotification` `lcCorNotification` date   NULL after `lcCorPhase`, 
	CHANGE `shiftStart` `shiftStart` tinyint(4)   NULL after `lcCorNotification`, 
	CHANGE `shiftEnd` `shiftEnd` tinyint(4)   NULL after `shiftStart`, 
	CHANGE `languageID` `languageID` tinyint(4)   NOT NULL DEFAULT '1' after `shiftEnd`, 
	ADD COLUMN `europeanUnionVATnumber` varchar(15)  COLLATE latin1_swedish_ci NULL after `languageID`, 
	ADD COLUMN `registrationHash` varchar(100)  COLLATE latin1_swedish_ci NULL after `europeanUnionVATnumber`, 
	DROP COLUMN `billingState`, 
	DROP COLUMN `priority`, 
	DROP COLUMN `reasonForDecline`, 
	DROP COLUMN `closedOnDate`, COMMENT='';

/* Alter table in target */
ALTER TABLE `contractor_registration_request` 
	CHANGE `countrySubdivision` `countrySubdivision` varchar(10)  COLLATE latin1_swedish_ci NULL after `city`, 
	CHANGE `zip` `zip` varchar(10)  COLLATE latin1_swedish_ci NULL after `countrySubdivision`, 
	CHANGE `country` `country` char(2)  COLLATE latin1_swedish_ci NULL after `zip`, 
	CHANGE `deadline` `deadline` date   NULL after `country`, 
	CHANGE `lastContactedBy` `lastContactedBy` mediumint(9)   NULL after `deadline`, 
	CHANGE `lastContactDate` `lastContactDate` datetime   NULL after `lastContactedBy`, 
	CHANGE `lastContactedByAutomatedEmailDate` `lastContactedByAutomatedEmailDate` datetime   NULL after `lastContactDate`, 
	CHANGE `contactCountByEmail` `contactCountByEmail` tinyint(4) unsigned   NOT NULL DEFAULT '0' after `lastContactedByAutomatedEmailDate`, 
	CHANGE `contactCountByPhone` `contactCountByPhone` tinyint(4) unsigned   NOT NULL DEFAULT '0' after `contactCountByEmail`, 
	CHANGE `matchCount` `matchCount` tinyint(4) unsigned   NOT NULL DEFAULT '0' after `contactCountByPhone`, 
	CHANGE `notes` `notes` text  COLLATE latin1_swedish_ci NULL after `matchCount`, 
	CHANGE `conID` `conID` mediumint(9)   NULL after `notes`, 
	CHANGE `operatorTags` `operatorTags` varchar(100)  COLLATE latin1_swedish_ci NULL after `conID`, 
	CHANGE `holdDate` `holdDate` date   NULL after `operatorTags`, 
	CHANGE `reasonForRegistration` `reasonForRegistration` varchar(500)  COLLATE latin1_swedish_ci NULL after `holdDate`, 
	CHANGE `reasonForDecline` `reasonForDecline` varchar(500)  COLLATE latin1_swedish_ci NULL after `reasonForRegistration`, 
	CHANGE `closedOnDate` `closedOnDate` date   NULL after `reasonForDecline`, 
	ADD COLUMN `registrationHash` varchar(100)  COLLATE latin1_swedish_ci NULL after `closedOnDate`, 
	DROP COLUMN `state`, 
	DROP KEY `status`, add KEY `status`(`status`,`country`), COMMENT='';

/* Alter table in target */
ALTER TABLE `job_site` 
	CHANGE `countrySubdivision` `countrySubdivision` varchar(10)  COLLATE latin1_swedish_ci NULL after `city`, 
	CHANGE `country` `country` varchar(10)  COLLATE latin1_swedish_ci NULL after `countrySubdivision`, 
	CHANGE `projectStart` `projectStart` date   NULL after `country`, 
	CHANGE `projectStop` `projectStop` date   NULL after `projectStart`, 
	DROP COLUMN `state`, COMMENT='';

/* Alter table in target */
ALTER TABLE `user_assignment` 
	CHANGE `countrySubdivision` `countrySubdivision` varchar(10)  COLLATE latin1_swedish_ci NULL after `assignmentType`, 
	CHANGE `country` `country` varchar(10)  COLLATE latin1_swedish_ci NULL after `countrySubdivision`, 
	CHANGE `postal_start` `postal_start` varchar(10)  COLLATE latin1_swedish_ci NULL after `country`, 
	CHANGE `postal_end` `postal_end` varchar(10)  COLLATE latin1_swedish_ci NULL after `postal_start`, 
	CHANGE `conID` `conID` int(11)   NULL after `postal_end`, 
	CHANGE `auditTypeID` `auditTypeID` int(11)   NULL after `conID`, 
	DROP COLUMN `state`, COMMENT=''; 

/* The foreign keys that were dropped are now re-created*/

ALTER TABLE `contractor_info`
ADD CONSTRAINT `FK_contractor_info` 
FOREIGN KEY (`id`) REFERENCES `accounts` (`id`) ON DELETE CASCADE;

delete from app_translation where msgKey like 'State%';
delete from app_translation where msgKey like 'Report.%State%';