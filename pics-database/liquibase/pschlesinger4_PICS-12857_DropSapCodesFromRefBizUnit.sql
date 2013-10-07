--liquibase formatted sql

--changeset pschlesinger:4
SELECT IFNULL(column_name, null) INTO @colName
FROM information_schema.columns
WHERE table_name = 'ref_sap_business_unit'
      AND column_name = 'sapCode'
      and table_schema = database();

select if(@colName is null, 'select 1', 'ALTER TABLE `ref_sap_business_unit` DROP COLUMN `sapCode`') into @dropStatement;

prepare dropColumn from @dropStatement;

execute dropColumn;

deallocate prepare dropColumn;

set @colName = null;

COLLATE=utf8_general_ci CHECKSUM=0 DELAY_KEY_WRITE=0
;

