--liquibase formatted sql

--changeset lkam:5
--preConditions onFail MARK_RAN

-- ALTER TABLE invoice_fee ADD COLUMN discountOperatorID INT(11) DEFAULT 0 NULL AFTER commissionEligible;

INSERT INTO invoice_fee(fee, defaultAmount, visible, feeClass, minFacilities, maxFacilities, qbFullName,createdBy,updatedBy,creationDate,updateDate, discountOperatorID)
VALUES
('Suncor Discount DocuGUARD™ for 1 Operators', 100, 1, 'DocuGUARD', 1, 1, 'DISC-DGVEN1', 38586, 38586, NOW(), NOW(), 10566),
('Suncor Discount DocuGUARD™ for 2-4 Operators', 100, 1, 'DocuGUARD', 2, 4, 'DISC-DGVEN2', 38586, 38586, NOW(), NOW(), 10566),
('Suncor Discount DocuGUARD™ for 5-8 Operators', 100, 1, 'DocuGUARD', 5, 8, 'DISC-DGVEN5', 38586, 38586, NOW(), NOW(), 10566),
('Suncor Discount DocuGUARD™ for 9-12 Operators', 100, 1, 'DocuGUARD', 9, 12, 'DISC-DGVEN9', 38586, 38586, NOW(), NOW(), 10566),
('Suncor Discount DocuGUARD™ for 13-19 Operators', 100, 1, 'DocuGUARD', 13, 19, 'DISC-DGVEN13', 38586, 38586, NOW(), NOW(), 10566),
('Suncor Discount InsureGUARD™ for 1 Operators', 50, 1, 'InsureGUARD', 1, 1, 'DISC-IGVEN1', 38586, 38586, NOW(), NOW(), 10566),
('Suncor Discount InsureGUARD™ for 2-4 Operators', 130, 1, 'InsureGUARD', 2, 4, 'DISC-IGVEN2', 38586, 38586, NOW(), NOW(), 10566),
('Suncor Discount InsureGUARD™ for 5-8 Operators', 300, 1, 'InsureGUARD', 5, 8, 'DISC-IGVEN5', 38586, 38586, NOW(), NOW(), 10566),
('Suncor Discount InsureGUARD™ for 9-12 Operators', 250, 1, 'InsureGUARD', 9, 12, 'DISC-IGVEN9', 38586, 38586, NOW(), NOW(), 10566),
('Suncor Discount InsureGUARD™ for 13-19 Operators', 200, 1, 'InsureGUARD', 13, 19, 'DISC-IGVEN13', 38586, 38586, NOW(), NOW(), 10566),
('Suncor Discount AuditGUARD™ for 1 Operators', 100, 1, 'AuditGUARD', 1, 1, 'DISC-AGVEN1', 38586, 38586, NOW(), NOW(), 10566),
('Suncor Discount AuditGUARD™ for 2-4 Operators', 200, 1, 'AuditGUARD', 2, 4, 'DISC-AGVEN2', 38586, 38586, NOW(), NOW(), 10566),
('Suncor Discount AuditGUARD™ for 5-8 Operators', 800, 1, 'AuditGUARD', 5, 8, 'DISC-AGVEN5', 38586, 38586, NOW(), NOW(), 10566),
('Suncor Discount AuditGUARD™ for 9-12 Operators', 500, 1, 'AuditGUARD', 9, 12, 'DISC-AGVEN9', 38586, 38586, NOW(), NOW(), 10566)
ON DUPLICATE KEY UPDATE fee = VALUES(fee),
defaultAmount = VALUES(defaultAmount),
visible = VALUES(visible),
feeClass = VALUES(feeClass),
minFacilities = VALUES(minFacilities),
maxFacilities = VALUES(maxFacilities),
qbFullName = VALUES(qbFullName),
discountOperatorID = VALUES(discountOperatorID);
