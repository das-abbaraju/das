ALTER TABLE `email_subscription`
  ADD COLUMN `reportID` INT(11) NULL AFTER `queueBatchDate`;
