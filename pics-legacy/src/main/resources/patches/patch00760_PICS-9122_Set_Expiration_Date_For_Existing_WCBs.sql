UPDATE contractor_audit
	SET contractor_audit.expiresDate = '2012-01-31 23:59:59'
	WHERE contractor_audit.auditFor = '2011'
	AND contractor_audit.auditTypeID in (145, 146, 143, 170, 261, 168, 148, 147, 169, 166, 167, 144);
	
UPDATE contractor_audit
	SET contractor_audit.expiresDate = '2013-01-31 23:59:59'
	WHERE contractor_audit.auditFor = '2012'
	AND contractor_audit.auditTypeID in (145, 146, 143, 170, 261, 168, 148, 147, 169, 166, 167, 144);
	
UPDATE contractor_audit
	SET contractor_audit.expiresDate = '2014-01-31 23:59:59'
	WHERE contractor_audit.auditFor = '2013'
	AND contractor_audit.auditTypeID in (145, 146, 143, 170, 261, 168, 148, 147, 169, 166, 167, 144);