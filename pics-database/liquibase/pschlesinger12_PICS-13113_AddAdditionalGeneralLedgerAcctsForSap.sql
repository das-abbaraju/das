--liquibase formatted sql

--changeset pschlesinger:12
insert into ref_sap_currency_general_ledger_account
(isoCurrencyCode,accountType,accountNumber)
values
('CAD','UndepositedFunds',14000102000),
('CAD','BadDebt',12000502000),
('CAD','TaxAP',25001002000),
('GBP','UndepositedFunds',14000103000),
('GBP','BadDebt',12000503000),
('GBP','TaxAP',25001103000),
('EUR','UndepositedFunds',14000203000),
('EUR','BadDebt',12000503000),
('EUR','TaxAP',25001103000),
('NOK','UndepositedFunds',14000203000),
('NOK','BadDebt',12000503000),
('NOK','TaxAP',25001103000),
('SEK','UndepositedFunds',14000203000),
('SEK','BadDebt',12000503000),
('SEK','TaxAP',25001103000),
('DKK','UndepositedFunds',14000203000),
('DKK','BadDebt',12000503000),
('DKK','TaxAP',25001103000);
