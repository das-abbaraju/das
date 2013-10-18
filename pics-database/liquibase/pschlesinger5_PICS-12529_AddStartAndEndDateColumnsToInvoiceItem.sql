--liquibase formatted sql

--changeset pschlesinger:5
SELECT IFNULL(column_name, null) INTO @colName
FROM information_schema.columns
WHERE table_name = 'invoice_item'
      AND column_name = 'startDate'
      and table_schema = database();

select if(@colName is not null, 'select 1', 'ALTER TABLE `invoice_item` ADD COLUMN `startDate` DATE') into @addStatement;

prepare addColumn from @addStatement;

execute addColumn;

deallocate prepare addColumn;

set @colName = null;

SELECT IFNULL(column_name, null) INTO @colName
FROM information_schema.columns
WHERE table_name = 'invoice_item'
      AND column_name = 'endDate'
      and table_schema = database();

select if(@colName is not null, 'select 1', 'ALTER TABLE `invoice_item` ADD COLUMN `endDate` DATE') into @addStatement;

prepare addColumn from @addStatement;

execute addColumn;

deallocate prepare addColumn;

set @colName = null;

