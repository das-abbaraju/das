--liquibase formatted sql

--changeset gmeurer:2
SELECT IFNULL(column_name, null) INTO @colName
FROM information_schema.columns
WHERE table_name = 'translation_usage'
      AND column_name = 'ipAddress'
      and table_schema = database();

select if(@colName is not null, 'select 1', 'ALTER TABLE translation_usage ADD ipAddress varchar(20)') into @addStatement;

prepare addColumn from @addStatement;

execute addColumn;

deallocate prepare addColumn;

set @colName = null;
