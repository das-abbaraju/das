SELECT IFNULL(column_name, null) INTO @colName
FROM information_schema.columns
WHERE table_name = 'flag_criteria'
AND column_name = 'new_category'
and table_schema = database();

select if(@colName is null, 'select 1', 'alter table flag_criteria drop column new_category') into @dropStatement;

prepare dropColumn from @dropStatement;

execute dropColumn;

deallocate prepare dropColumn;

set @colName = null;
