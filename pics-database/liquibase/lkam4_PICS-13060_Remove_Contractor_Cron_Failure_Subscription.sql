--liquibase formatted sql

--changeset lkam:4
--preConditions onFail MARK_RAN
DELETE FROM email_subscription WHERE subscription = 'ContractorCronFailure';