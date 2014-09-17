--liquibase formatted sql

--changeset cfranks:1
UPDATE users u
INNER JOIN accounts a ON u.accountID = a.ID
SET u.UsingDynamicReports = 1,
    u.UsingDynamicReportsDate = NOW(),
    u.UsingVersion7Menus = 1,
    u.UsingVersion7MenusDate = NOW()
WHERE a.type in ('Operator','Corporate')
AND u.UsingDynamicReports = 0
AND u.UsingVersion7Menus = 0;