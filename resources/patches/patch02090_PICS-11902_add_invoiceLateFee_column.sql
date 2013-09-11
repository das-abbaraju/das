SELECT IFNULL(column_name, null) INTO @colName
FROM information_schema.columns
WHERE table_name = 'invoice'
      AND column_name = 'lateFeeInvoice'
      and table_schema = database();

select if(@colName is not null, 'select 1', 'ALTER TABLE invoice ADD lateFeeInvoice INT(11)') into @addStatement;

prepare addColumn from @addStatement;

execute addColumn;

deallocate prepare addColumn;

set @colName = null;
