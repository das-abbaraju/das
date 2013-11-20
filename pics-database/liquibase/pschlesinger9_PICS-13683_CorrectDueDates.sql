--liquibase formatted sql

--changeset pschlesinger:9
update
  invoice
set
  dueDate = date(creationDate)
where
  dueDate < date(creationDate)
and
  tableType = 'I';
