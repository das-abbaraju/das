--liquibase formatted sql

--changeset kchase:45
SELECT IFNULL(column_name, null) INTO @colName
FROM information_schema.columns
WHERE table_name = 'flag_data'
      AND column_name = 'criteriaOpID'
      and table_schema = database();

select if(@colName is not null, 'select 1', 'ALTER TABLE flag_data ADD COLUMN criteriaOpID INT(11) UNSIGNED NULL ') into @addStatement;

prepare addColumn from @addStatement;

execute addColumn;

deallocate prepare addColumn;

set @colName = null;
