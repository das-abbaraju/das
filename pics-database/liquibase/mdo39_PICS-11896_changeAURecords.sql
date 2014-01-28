--liquibase formatted sql

--changeset mdo:39
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
UPDATE invoice_commission
SET accountUserID = 4149
WHERE accountUserID = 103
AND invoiceID IN (197837 ,200763 ,201087 ,202297 ,202836 ,204999 ,205242 ,205274 ,205418 ,206611 ,207345 ,207353)
;

UPDATE invoice_commission
SET accountUserID = 3417
WHERE accountUserID = 608
AND invoiceID IN (197837 ,200763 ,201087 ,202297 ,202836 ,204999 ,205242 ,205274 ,205418 ,206611 ,207345 ,207353)
;

UPDATE invoice_commission
SET accountUserID = 2027
WHERE accountUserID = 608
AND invoiceID IN (197837 ,200763 ,201087 ,202297 ,202836 ,204999 ,205242 ,205274 ,205418 ,206611 ,207345 ,207353)
;

