ALTER TABLE `invoice`
  ADD COLUMN `invoiceType` VARCHAR(20) NULL AFTER `tableType`,
  ADD COLUMN `commissionableAmount` DECIMAL(9,2) DEFAULT 0.00 NOT NULL AFTER `amountApplied`;
