--liquibase formatted sql

--changeset dalvarado:6

-- Add an include rule and an exclude rule for Safety Sensitive.

-- include
INSERT IGNORE INTO audit_type_rule
(include, level, levelAdjustment, priority, auditTypeID, contractorType, safetySensitive, createdBy, updatedBy, creationDate, updateDate, effectiveDate, expirationDate)
VALUES
(1, 2, 0, 207, 2, 'Onsite', 1, 37745, 37745, NOW(), NOW(), '2013-11-21 00:00:00', '4000-01-01 00:00:00');

-- ignore rule
INSERT IGNORE INTO audit_type_rule
(include, level, levelAdjustment, priority, auditTypeID, contractorType, safetySensitive, tagID, createdBy, updatedBy, creationDate, updateDate, effectiveDate, expirationDate)
  VALUES
  (0, 2, 0, 210, 2, 'Onsite', 1, 1647, 37745, 37745, NOW(), NOW(), '2013-11-21 00:00:00', '4000-01-01 00:00:00');
