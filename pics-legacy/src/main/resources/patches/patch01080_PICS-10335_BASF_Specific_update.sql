-- update audits to be recalculated
UPDATE
    contractor_audit ca
    JOIN contractor_audit_operator cao ON cao.auditID = ca.id
    JOIN contractor_info ci ON ci.id = ca.conID
SET ci.lastRecalculation = NULL, ca.lastRecalculation = NULL
WHERE ca.auditTypeID=56
      AND cao.status='Pending' AND cao.percentComplete=100