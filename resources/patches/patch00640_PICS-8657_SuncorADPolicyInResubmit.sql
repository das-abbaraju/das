-- move Suncor A&D from Resubmit to Complete
-- SELECT * from
UPDATE
contractor_audit_operator cao
JOIN contractor_audit ca ON ca.id = cao.auditID
SET cao.status='Complete'
where ca.auditTypeID = 425
and cao.status in ('Resubmit', 'Resubmitted');
