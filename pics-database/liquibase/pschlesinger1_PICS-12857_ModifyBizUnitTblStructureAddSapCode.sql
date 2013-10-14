--liquibase formatted sql

--changeset pschlesinger:1
SELECT IFNULL(column_name, null) INTO @colName
FROM information_schema.columns
WHERE table_name = 'ref_sap_business_unit'
      AND column_name = 'sapCode'
      and table_schema = database();

select if(@colName is not null, 'select 1', 'ALTER TABLE `ref_sap_business_unit` ADD COLUMN `sapCode` varchar(20) NOT NULL DEFAULT \'Undefined\' ') into @addStatement;

prepare addColumn from @addStatement;

execute addColumn;

deallocate prepare addColumn;

set @colName = null;

