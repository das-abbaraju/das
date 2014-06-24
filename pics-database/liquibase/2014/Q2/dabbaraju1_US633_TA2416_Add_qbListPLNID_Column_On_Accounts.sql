--liquibase formatted sql

--changeset dabbaraju:1
-- --preConditions onFail MARK_RAN

-- First let's correct the name of the qbListCHFID column to use the two-letter country code (not the three-letter code)
-- and then move the column to be immediately after the other qbList___ columns
ALTER TABLE accounts
CHANGE COLUMN qbListCHFID qbListCHID VARCHAR(25) NULL AFTER qbListEUID;


--  Now, add new column qbListPLID on Accounts table for new PLN currency, after qbListCHID
alter table accounts
add column qbListPLID  VARCHAR(25) NULL AFTER qbListCHID;

