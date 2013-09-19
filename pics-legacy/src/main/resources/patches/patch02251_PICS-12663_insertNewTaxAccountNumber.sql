INSERT INTO ref_sap_currency_general_ledger_account (isoCurrencyCode,accountType,accountNumber) VALUES ('USD','TaxAP',25001001000)
ON DUPLICATE KEY UPDATE
accountNumber = 25001001000;
