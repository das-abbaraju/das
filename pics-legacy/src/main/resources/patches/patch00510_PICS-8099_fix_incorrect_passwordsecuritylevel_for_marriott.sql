-- Fix incorrect passwordSecurityLevelId for Marriott introduced in patch 500
UPDATE accounts SET passwordSecurityLevelId = 1 WHERE id IN (11732, 14960);

