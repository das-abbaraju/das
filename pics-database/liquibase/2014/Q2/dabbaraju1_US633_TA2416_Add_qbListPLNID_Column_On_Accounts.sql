--liquibase formatted sql

--changeset dabbaraju:1
-- --preConditions onFail MARK_RAN

alter table accounts
add column qbListPLNID  VARCHAR(25) NULL;

-- Add new nullable column qbListCHFID on Accounts table for new PLN currency.
