-- FOR STORING EFT GENERAL LEDGER ACCOUNT NUMBER
create table if not exists ref_sap_business_unit_account (
  ref_sap_business_unit_account_id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  ref_sap_business_unit_id int(11) NOT NULL REFERENCES ref_sap_business_unit(id),
  account_type ENUM('eftDepositGLAccount'),
  account_number varchar(12)
);

INSERT INTO
  ref_sap_business_unit_account
  (ref_sap_business_unit_id,account_type,account_number)
  select * from (select 2,'eftDepositGLAccount','14000101000') as tmp
  WHERE NOT EXISTS (
      SELECT
        *
      FROM
        ref_sap_business_unit_account
      WHERE
        ref_sap_business_unit_id = 2
        AND
        account_type = 'eftDepositGLAccount'
        and
        account_number ='14000101000'
  ) LIMIT 1;

-- TO REMOVE SYNCING OF ANY INVOICES AND PAYMENTS BEFORE NVP MINORITY OWNERSHIP
UPDATE
  invoice
SET
  sapSync = 0
WHERE
  updateDate < "2012-12-27";
