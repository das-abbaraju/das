--liquibase formatted sql

--changeset pschlesinger:15
CREATE TABLE temp_credit_memo SELECT * FROM invoice WHERE 1=0;
CREATE TABLE temp_credit_memo_items SELECT * FROM invoice_item WHERE 1=0;
