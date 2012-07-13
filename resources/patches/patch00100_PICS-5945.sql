/* Foreign Keys must be dropped in the target to ensure that requires changes can be done*/

ALTER TABLE `contractor_info` DROP FOREIGN KEY `FK_contractor_info` ;

/* Alter table in target */
ALTER TABLE `accounts` 
	ADD COLUMN `countrySubdivision` varchar(10)  COLLATE latin1_swedish_ci NULL after `state`, 
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
	CHANGE `timezone` `timezone` varchar(50)  COLLATE latin1_swedish_ci NULL after `locale`, COMMENT='';

/* Alter table in target */
ALTER TABLE `contractor_audit` 
	CHANGE `state` `state` varchar(10)  COLLATE latin1_swedish_ci NULL after `city`, 
	ADD COLUMN `countrySubdivision` varchar(10)  COLLATE latin1_swedish_ci NULL after `state`, 
	CHANGE `zip` `zip` varchar(10)  COLLATE latin1_swedish_ci NULL after `countrySubdivision`, 
	CHANGE `country` `country` varchar(50)  COLLATE latin1_swedish_ci NULL after `zip`, 
	CHANGE `latitude` `latitude` float   NOT NULL DEFAULT '0' after `country`, 
	CHANGE `longitude` `longitude` float   NOT NULL DEFAULT '0' after `latitude`, 
	CHANGE `paidDate` `paidDate` date   NULL after `longitude`, 
	CHANGE `ruleID` `ruleID` int(11)   NULL after `paidDate`, COMMENT='';

/* Alter table in target */
ALTER TABLE `contractor_info` 
	ADD COLUMN `billingCountrySubdivision` varchar(10)  COLLATE latin1_swedish_ci NULL after `billingState`, 
	CHANGE `billingZip` `billingZip` varchar(10)  COLLATE latin1_swedish_ci NULL after `billingCountrySubdivision`, 
	CHANGE `billingCountry` `billingCountry` varchar(25)  COLLATE latin1_swedish_ci NULL after `billingZip`, 
	CHANGE `membershipDate` `membershipDate` date   NULL after `billingCountry`, 
	CHANGE `payingFacilities` `payingFacilities` smallint(5) unsigned   NOT NULL DEFAULT '0' after `membershipDate`, 
	CHANGE `welcomeAuditor_id` `welcomeAuditor_id` mediumint(8) unsigned   NULL after `payingFacilities`, 
	CHANGE `safetyRisk` `safetyRisk` tinyint(3)   NULL DEFAULT '2' after `welcomeAuditor_id`, 
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
	CHANGE `balance` `balance` decimal(9,2)   NULL DEFAULT '0.00' after `lastUpgradeDate`, 
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
	CHANGE `reviewedContractorBadge` `reviewedContractorBadge` tinyint(4)   NOT NULL DEFAULT '0' after `showInDirectory`, COMMENT='';

/* Alter table in target */
ALTER TABLE `contractor_registration_request` 
	CHANGE `state` `state` varchar(10)  COLLATE latin1_swedish_ci NULL after `city`, 
	ADD COLUMN `countrySubdivision` varchar(10)  COLLATE latin1_swedish_ci NULL after `state`, 
	CHANGE `zip` `zip` varchar(10)  COLLATE latin1_swedish_ci NULL after `countrySubdivision`, 
	CHANGE `country` `country` char(2)  COLLATE latin1_swedish_ci NULL after `zip`, 
	CHANGE `deadline` `deadline` date   NULL after `country`, 
	CHANGE `lastContactedBy` `lastContactedBy` mediumint(9)   NULL after `deadline`, 
	CHANGE `lastContactDate` `lastContactDate` datetime   NULL after `lastContactedBy`, 
	CHANGE `contactCountByEmail` `contactCountByEmail` tinyint(4) unsigned   NOT NULL DEFAULT '0' after `lastContactDate`, 
	CHANGE `contactCountByPhone` `contactCountByPhone` tinyint(4) unsigned   NOT NULL DEFAULT '0' after `contactCountByEmail`, 
	CHANGE `matchCount` `matchCount` tinyint(4) unsigned   NOT NULL DEFAULT '0' after `contactCountByPhone`, 
	CHANGE `notes` `notes` text  COLLATE latin1_swedish_ci NULL after `matchCount`, 
	CHANGE `conID` `conID` mediumint(9)   NULL after `notes`, 
	CHANGE `operatorTags` `operatorTags` varchar(100)  COLLATE latin1_swedish_ci NULL after `conID`, 
	CHANGE `holdDate` `holdDate` date   NULL after `operatorTags`, 
	CHANGE `reasonForRegistration` `reasonForRegistration` varchar(500)  COLLATE latin1_swedish_ci NULL after `holdDate`, 
	CHANGE `reasonForDecline` `reasonForDecline` varchar(500)  COLLATE latin1_swedish_ci NULL after `reasonForRegistration`, 
	CHANGE `closedOnDate` `closedOnDate` date   NULL after `reasonForDecline`, COMMENT='';

/* Alter table in target */
ALTER TABLE `job_site` 
	CHANGE `state` `state` varchar(10)  COLLATE latin1_swedish_ci NULL after `city`, 
	ADD COLUMN `countrySubdivision` varchar(10)  COLLATE latin1_swedish_ci NULL after `state`, 
	CHANGE `country` `country` varchar(10)  COLLATE latin1_swedish_ci NULL after `countrySubdivision`, 
	CHANGE `projectStart` `projectStart` date   NULL after `country`, 
	CHANGE `projectStop` `projectStop` date   NULL after `projectStart`, COMMENT='';

/* Alter table in target */
ALTER TABLE `ref_country` 
	CHANGE `isoCode` `isoCode` varchar(10)  COLLATE latin1_swedish_ci NOT NULL DEFAULT '' first, COMMENT='';

/* Alter table in target */
ALTER TABLE `ref_country_subdivision` 
	CHANGE `isoCode` `isoCode` varchar(10)  COLLATE utf8_general_ci NOT NULL first, 
	CHANGE `countryCode` `countryCode` varchar(10)  COLLATE utf8_general_ci NOT NULL after `isoCode`, 
	ADD COLUMN `english` varchar(50)  COLLATE utf8_general_ci NULL after `countryCode`, COMMENT='';

/* Alter table in target */
ALTER TABLE `user_assignment` 
	CHANGE `state` `state` varchar(10)  COLLATE latin1_swedish_ci NULL after `assignmentType`, 
	ADD COLUMN `countrySubdivision` varchar(10)  COLLATE latin1_swedish_ci NULL after `state`, 
	CHANGE `country` `country` varchar(10)  COLLATE latin1_swedish_ci NULL after `countrySubdivision`, 
	CHANGE `postal_start` `postal_start` varchar(10)  COLLATE latin1_swedish_ci NULL after `country`, 
	CHANGE `postal_end` `postal_end` varchar(10)  COLLATE latin1_swedish_ci NULL after `postal_start`, 
	CHANGE `conID` `conID` int(11)   NULL after `postal_end`, 
	CHANGE `auditTypeID` `auditTypeID` int(11)   NULL after `conID`, COMMENT=''; 

/* The foreign keys that were dropped are now re-created*/

ALTER TABLE `contractor_info`
ADD CONSTRAINT `FK_contractor_info` 
FOREIGN KEY (`id`) REFERENCES `accounts` (`id`) ON DELETE CASCADE;

-- data conversion for pre-existing values
update accounts a
join ref_state rs on a.state = rs.isoCode and a.country = rs.countryCode
set countrySubdivision = concat(country,'-',state)
where state is not null 
and countrySubdivision is null;

update 
contractor_audit ca
join ref_state rs on a.state = rs.isoCode and a.country = rs.countryCode
set countrySubdivision = concat(country,'-',state)
where ca.state is not null 
and ca.countrySubdivision is null;

update 
contractor_info ci
join ref_state rs on a.billingState = rs.isoCode and a.billingCountry = rs.countryCode
set billingCountrySubdivision = concat(billingCountry,'-',billingState)
where ci.billingState is not null 
and ci.billingCountrySubdivision is null;

update 
contractor_registration_request a
join ref_state rs on a.state = rs.isoCode and a.country = rs.countryCode
set countrySubdivision = concat(country,'-',state)
where state is not null 
and countrySubdivision is null;

update 
job_site a
join ref_state rs on a.state = rs.isoCode and a.country = rs.countryCode
set countrySubdivision = concat(country,'-',state)
where state is not null 
and countrySubdivision is null;

update 
user_assignment a
join ref_state rs on a.state = rs.isoCode and a.country = rs.countryCode
set countrySubdivision = concat(country,'-',state)
where state is not null 
and countrySubdivision is null;
