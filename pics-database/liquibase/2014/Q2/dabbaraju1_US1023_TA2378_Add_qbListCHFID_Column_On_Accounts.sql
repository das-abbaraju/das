-- Add new nullable column qbListCHFID on Accounts table for new CHF currency.

--liquibase formatted sql

--changeset dabbaraju:1

-- --preConditions onFail MARK_RAN

alter table
accounts
add
qbListCHFID  VARCHAR(25);
