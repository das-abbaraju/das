--liquibase formatted sql

--changeset pschlesinger:1
SELECT IFNULL(column_name, null) INTO @colName
FROM information_schema.columns
WHERE table_name = 'ref_sap_business_unit'
      AND column_name = 'testDatabase'
      and table_schema = database();

select if(@colName is null, 'select 1', 'ALTER TABLE `ref_sap_business_unit` DROP COLUMN `testDatabase`') into @dropStatement;

prepare dropColumn from @dropStatement;

execute dropColumn;

deallocate prepare dropColumn;

set @colName = null;

SELECT IFNULL(column_name, null) INTO @colName
FROM information_schema.columns
WHERE table_name = 'ref_sap_business_unit'
      AND column_name = 'liveDatabase'
      and table_schema = database();

select if(@colName is null, 'select 1', 'ALTER TABLE `ref_sap_business_unit` DROP COLUMN `liveDatabase`') into @dropStatement;

prepare dropColumn from @dropStatement;

execute dropColumn;

deallocate prepare dropColumn;

set @colName = null;
COLLATE=utf8_general_ci CHECKSUM=0 DELAY_KEY_WRITE=0
;

