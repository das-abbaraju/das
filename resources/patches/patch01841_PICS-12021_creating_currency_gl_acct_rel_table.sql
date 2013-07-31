-- FOR STORING UNDEPOSITED FUNDS GENERAL LEDGER ACCOUNT NUMBERS
CREATE TABLE IF NOT EXISTS ref_sap_currency_undeposited_funds_account (
  id              INT(11)    NOT NULL AUTO_INCREMENT PRIMARY KEY,
  isoCurrencyCode VARCHAR(3) NOT NULL,
  accountNumber   VARCHAR(12),
  UNIQUE KEY (isoCurrencyCode)
);

INSERT INTO
  ref_sap_currency_undeposited_funds_account
  (isoCurrencyCode, accountNumber)
  SELECT
    *
  FROM (SELECT
          'USD',
          '14000101000') AS tmp
  WHERE NOT EXISTS(
      SELECT
        isoCurrencyCode,
        accountNumber
      FROM
        ref_sap_currency_undeposited_funds_account
      WHERE
        isoCurrencyCode = 'USD'
        AND
        accountNumber = '14000101000'
  )
  LIMIT 1;

