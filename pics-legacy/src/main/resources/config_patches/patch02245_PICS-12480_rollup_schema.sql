-- add parent audit type
SELECT IFNULL(column_name, null) INTO @colName
FROM information_schema.columns
WHERE table_name = 'invoice'
      AND column_name = 'lateFeeInvoice'
      and table_schema = database();

select if(@colName is not null, 'select 1', 'Alter table audit_type add column parentID int(11) default NULL') into @addStatement;

prepare addColumn from @addStatement;

execute addColumn;

deallocate prepare addColumn;

set @colName = null;

-- add rollback status
SELECT IFNULL(column_name, null) INTO @colName
FROM information_schema.columns
WHERE table_name = 'invoice'
      AND column_name = 'lateFeeInvoice'
      and table_schema = database();

select if(@colName is not null, 'select 1', 'Alter table audit_type add column rollbackStatus varchar(30) default NULL') into @addStatement;

prepare addColumn from @addStatement;

execute addColumn;

deallocate prepare addColumn;

set @colName = null;

-- increase column size
Alter table audit_question_function modify column expression varchar(255);