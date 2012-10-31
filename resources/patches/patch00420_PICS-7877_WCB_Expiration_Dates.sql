update contractor_audit
	set contractor_audit.expiresDate = '2012-01-31 23:59:59'
	where contractor_audit.auditTypeID in (145, 146, 143, 170, 261, 168, 148, 147, 169, 166, 167, 144)
	and contractor_audit.auditFor = '2011' 
	and contractor_audit.expiresDate is not null;


update contractor_audit
	set contractor_audit.expiresDate = '2013-01-31 23:59:59'
	where contractor_audit.auditTypeID in (145, 146, 143, 170, 261, 168, 148, 147, 169, 166, 167, 144)
	and contractor_audit.auditFor = '2012' 
	and contractor_audit.expiresDate is not null;


update contractor_audit
	set contractor_audit.expiresDate = '2014-01-31 23:59:59'
	where contractor_audit.auditTypeID in (145, 146, 143, 170, 261, 168, 148, 147, 169, 166, 167, 144)
	and contractor_audit.auditFor = '2013' 
	and contractor_audit.expiresDate is not null;