--liquibase formatted sql

--changeset pschlesinger:7
SELECT IFNULL(column_name, null) INTO @colName
FROM information_schema.columns
WHERE table_name = 'invoice_item'
      AND column_name = 'startDate'
      and table_schema = database();

select if(@colName is null, 'select 1', 'ALTER TABLE `invoice_item` change COLUMN `startDate` `revenueStartDate` date') into @renameStatement;

prepare renameColumn from @renameStatement;

execute renameColumn;

deallocate prepare renameColumn;

set @colName = null;

SELECT IFNULL(column_name, null) INTO @colName
FROM information_schema.columns
WHERE table_name = 'invoice_item'
      AND column_name = 'endDate'
      and table_schema = database();

select if(@colName is null, 'select 1', 'ALTER TABLE `invoice_item` change COLUMN `endDate` `revenueFinishDate` date') into @renameStatement;

prepare renameColumn from @renameStatement;

execute renameColumn;

deallocate prepare renameColumn;

set @colName = null;
