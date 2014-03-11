--liquibase formatted sql

--changeset kchase:32

-- Update affected flag data overrides
CREATE TEMPORARY TABLE noChanges
AS (select conID FROM flag_data_override where criteriaID=784);

update flag_data_override
set criteriaID=784
where criteriaID=785
and year is not null
and conid not in (select conid FROM noChanges)
;
