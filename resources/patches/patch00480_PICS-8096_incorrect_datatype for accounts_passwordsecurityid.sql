-- Fix incorrect column type for passwordSecurityLevelId
ALTER TABLE `accounts`
DROP COLUMN `passwordSecurityLevelId`;

ALTER TABLE `accounts`
ADD COLUMN `passwordSecurityLevelId` TINYINT(4) NULL DEFAULT 0;

-- Reapply setting originally in patch00450: Set PasswordSecurityLevel = High for all Nassco acccounts
UPDATE accounts SET passwordSecurityLevelId = 2 WHERE id IN (33250, 33251, 33252, 33253, 34502);
