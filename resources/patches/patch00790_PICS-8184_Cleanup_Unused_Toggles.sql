DELETE FROM app_properties
	WHERE property IN ('Toggle.DynamicReports_v2', 'Toggle.DynamicReports');
	
ALTER TABLE `users` 
    ADD COLUMN `usingVersion7Menus` tinyint(4) NOT NULL AFTER `usingDynamicReportsDate`,
    ADD COLUMN `usingVersion7MenusDate` datetime NULL AFTER `usingVersion7Menus`;
    
    
UPDATE users
    SET users.usingVersion7Menus = users.usingDynamicReports, 
        users.usingVersion7MenusDate = users.usingDynamicReportsDate
    WHERE users.usingDynamicReports = 1 or users.usingDynamicReportsDate IS NOT NULL;