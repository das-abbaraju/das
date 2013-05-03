INSERT INTO app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, lastUsed,
                             qualityRating, applicable, sourceLanguage, contentDriven)
  VALUES ((SELECT
             CONCAT("InvoiceFee.", id, ".fee")
           FROM invoice_fee
           WHERE fee = "SSIP Activation Fee Discount"), 'en',
          'SSIP Discount', 53137, 53137, NOW(), NOW(), null, 1, 1, 'en', 0)
  ON DUPLICATE KEY UPDATE msgValue = 'SSIP Activation Fee Discount' ;
