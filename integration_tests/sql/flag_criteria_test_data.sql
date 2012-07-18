INSERT INTO `flag_criteria` 
(`category`,`questionID`,`auditTypeID`,`oshaType`,`oshaRateType`,`createdBy`,`updatedBy`,`creationDate`,`updateDate`,`label`,
  `description`,`comparison`,`dataType`,`defaultValue`,`multiYearScope`,`allowCustomValue`,`flaggableWhenMissing`,`insurance`,
  `displayOrder`,`requiredStatus`,`optionCode`,`requiredStatusComparison`,`requiredLanguages`)
VALUES 
('Statistics', NULL, NULL, 'COHS', 'LwcrAbsolute', '1', '1', '2010-02-11 12:16:23', '2010-02-11 12:16:23', 'LTIF Avg', 
 'Lost Time Injury Frequency (LTIF) average (3 year) must be less than or equal to {HURDLE}', '>', 'number', '1', 'ThreeYearAverage', '1', '0', '0', 
 '130', NULL, NULL, NULL, '["en"]');

 INSERT INTO `flag_criteria` 
(`category`,`questionID`,`auditTypeID`,`oshaType`,`oshaRateType`,`createdBy`,`updatedBy`,`creationDate`,`updateDate`,`label`,
  `description`,`comparison`,`dataType`,`defaultValue`,`multiYearScope`,`allowCustomValue`,`flaggableWhenMissing`,`insurance`,
  `displayOrder`,`requiredStatus`,`optionCode`,`requiredStatusComparison`,`requiredLanguages`)
VALUES 
('Audits', NULL, NULL, NULL, NULL, '1', '941', '2010-02-11 12:16:23', '2011-05-30 15:13:40', 'Manual Audit', 
'PICS Manual Audit is missing or has open requirements', '', 'boolean', 'false', NULL, '0', '0', '0', '210', 'Complete', NULL, NULL, '["en"]');

 INSERT INTO `flag_criteria` 
(`category`,`questionID`,`auditTypeID`,`oshaType`,`oshaRateType`,`createdBy`,`updatedBy`,`creationDate`,`updateDate`,`label`,
  `description`,`comparison`,`dataType`,`defaultValue`,`multiYearScope`,`allowCustomValue`,`flaggableWhenMissing`,`insurance`,
  `displayOrder`,`requiredStatus`,`optionCode`,`requiredStatusComparison`,`requiredLanguages`)
VALUES 
('Audits', NULL, NULL, '', NULL, '1', '941', '2010-02-11 12:16:23', '2011-05-30 15:13:40', 'Manual Audit', 
'PICS Manual Audit is missing or has open requirements', '', 'boolean', 'false', NULL, '0', '0', '0', '210', 'Complete', NULL, NULL, '["en"]');
