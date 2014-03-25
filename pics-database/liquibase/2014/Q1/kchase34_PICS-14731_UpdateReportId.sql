--liquibase formatted sql

--changeset kchase:34

-- Update email subscription report id to dummy report
update 
email_subscription
set reportID=2209
where subscription<>'DynamicReports';