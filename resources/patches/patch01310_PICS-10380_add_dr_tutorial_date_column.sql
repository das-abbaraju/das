-- Add the reportsManagerTutorialDate column to track whether to show the tutorial.
ALTER TABLE `users`
ADD COLUMN `reportsManagerTutorialDate` DATETIME NULL COMMENT 'Indicates the date that the user was redirected to the tutorial. NULL indicates never.' AFTER `usingVersion7MenusDate`;