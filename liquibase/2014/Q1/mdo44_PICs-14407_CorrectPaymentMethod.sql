--liquibase formatted sql

--changeset mdo:44
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
UPDATE invoice i
SET paymentMethod = 'BadDebtCreditMemo'
WHERE paymentMethod = 'BadDebt'