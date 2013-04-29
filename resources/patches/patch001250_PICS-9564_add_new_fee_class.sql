-- Adding SSIP fee

INSERT INTO invoice_fee (fee, defaultamount, visible, feeClass, minFacilities, maxFacilities,
                         qbFullName, createdBy, updatedBy, creationDate, updateDate,
                         effectiveDate, displayOrder, commissionEligible)
  VALUES ("SSIP Activation Discount", -199, 1, "SSIPDiscountFee", 1, 10000, "SSIP Discount", 53137, 53137, NOW(), NOW(), NOW(), 255, 1);

-- Add translation item
INSERT INTO app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, lastUsed,
                             qualityRating, applicable, sourceLanguage, contentDriven)
  VALUES ((SELECT
             CONCAT("InvoiceFee.", id, ".fee")
           FROM invoice_fee
           WHERE fee = "SSIP Activation Discount"), 'en',
          'SSIP Discount', 53137, 53137, NOW(), NOW(), null, 1, 1, 'en', 0);
