SELECT IFNULL(column_name, null) INTO @colName
FROM information_schema.columns
WHERE table_name = 'invoice'
      AND column_name = 'lateFeeInvoice'
      and table_schema = database();

select if(@colName is null, 'select 1', 'ALTER TABLE `invoice` CHANGE COLUMN `lateFeeInvoice` `lateFeeInvoiceID` INT(11) NULL') into @addStatement;

prepare addColumn from @addStatement;

execute addColumn;

deallocate prepare addColumn;

set @colName = null;
