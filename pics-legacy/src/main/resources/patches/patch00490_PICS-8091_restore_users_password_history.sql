-- Restore the users.passwordHistory column so we don't break Stable.
ALTER TABLE `users`
ADD COLUMN `passwordHistory` VARCHAR(1000) NULL after `accountID`;
