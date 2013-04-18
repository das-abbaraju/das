-- Update complete manual audits with no expiration date
-- expiration is set to 36 months from when they were moved to complete
UPDATE
    contractor_audit ca
    JOIN contractor_audit_operator cao ON cao.auditID = ca.id
SET ca.expiresDate = TIMESTAMP(DATE(DATE_ADD(cao.updateDate, INTERVAL 36 MONTH)), "23:59:59")
WHERE ca.auditTypeID=2
      AND ca.expiresDate IS NULL
      AND cao.status='Complete'
      AND cao.visible = 1;