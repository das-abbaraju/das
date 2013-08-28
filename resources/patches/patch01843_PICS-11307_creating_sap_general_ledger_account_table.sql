CREATE TABLE IF NOT EXISTS ref_sap_currency_general_ledger_account (
  id              INT(11)    NOT NULL AUTO_INCREMENT PRIMARY KEY,
  isoCurrencyCode VARCHAR(3) NOT NULL REFERENCES ref_country(isoCode),
  accountType ENUM ('UndepositedFunds','BadDebt'),
  accountNumber   VARCHAR(12),
  UNIQUE KEY (isoCurrencyCode,accountType)
);

INSERT INTO
  ref_sap_currency_general_ledger_account
  (isoCurrencyCode, accountType, accountNumber)
  SELECT
    *
  FROM (SELECT
          'USD',
          'UndepositedFunds',
          '14000101000') AS tmp
  WHERE NOT EXISTS(
      SELECT
        isoCurrencyCode,
        accountType,
        accountNumber
      FROM
        ref_sap_currency_general_ledger_account
      WHERE
        isoCurrencyCode = 'USD'
        AND
        accountType = 'UndepositedFunds'
        AND
        accountNumber = '14000101000'
  )
  LIMIT 1;

INSERT INTO
  ref_sap_currency_general_ledger_account
  (isoCurrencyCode, accountType, accountNumber)
  SELECT
    *
  FROM (SELECT
          'USD',
          'BadDebt',
          '12000501000') AS tmp
  WHERE NOT EXISTS(
      SELECT
        isoCurrencyCode,
        accountType,
        accountNumber
      FROM
        ref_sap_currency_general_ledger_account
      WHERE
        isoCurrencyCode = 'USD'
        AND
        accountType = 'BadDebt'
        AND
        accountNumber = '12000501000'
  )
  LIMIT 1;

