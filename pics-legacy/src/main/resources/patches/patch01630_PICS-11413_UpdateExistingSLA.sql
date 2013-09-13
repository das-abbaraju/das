UPDATE
    contractor_audit ca
    JOIN contractor_audit_operator cao ON cao.auditID=ca.id
SET ca.slaDate=DATE_ADD(ca.creationDate, INTERVAL 14 DAY)
WHERE ca.auditTypeID=2
      AND ca.slaDate IS NOT NULL
      AND ca.slaDate < ca.creationDate
      AND ca.expiresDate IS NULL
      AND cao.visible=1 AND cao.status='Pending'
;