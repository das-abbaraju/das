-- Set PasswordSecurityLevel = 1(High) for Marriott International and Marriott Hotels and Resorts
UPDATE accounts SET passwordSecurityLevelId = 2 WHERE id IN (11732, 14960);

