update contractor_audit_operator
	join (select * from (select caow.id, max(caow.updateDate), caow.caoID, caow.status, temp.CaoStatus, caow.notes
		from contractor_audit_operator_workflow caow
		join (select distinct cao.id, cao.status as 'CaoStatus' from contractor_audit ca
			join audit_type adt on adt.id = ca.auditTypeID
			join contractor_audit_operator cao on cao.auditID = ca.id
			where adt.classType = 'Policy'
			and cao.status = 'Incomplete') as temp on temp.id = caow.caoID
		group by caow.caoID) as temp
		where (temp.notes like '%upload a valid certificate%' 
			or temp.notes like '%invalid certificate%'
			or temp.notes like '%valid certificate%'
			or temp.notes like '%along with Certificate of Insurance%'
			or temp.notes like '%Please upload your workers comp policy%'
			or temp.notes like '%We do not accept:  Declarations%'
			or temp.notes like '%do not accept Declarations%'
			or temp.notes like '%Please upload your excess%'
			or temp.notes like '%No policy is provided%'
			or temp.notes like '%certificates are expired%'
			or temp.notes like '%upload all forms listed on the Certificate%'
			or temp.notes like '%upload your Certificate of Insurance%'
			or temp.notes like '%attach your certificate of insurance%'
			or temp.notes like '%upload is your COI%'
			or temp.notes like '%upload your COI%'
			or temp.notes like '%Uploaded incorrect Certificate%'
			or temp.notes like '%upload all forms along with the Certificate of Insurance%')) as temp2 on temp2.caoID = contractor_audit_operator.id
	set contractor_audit_operator.auditSubStatus = 'NoValidCertificate'
	where contractor_audit_operator.auditSubStatus is null;



update contractor_audit_operator
	join (select * from (select caow.id, max(caow.updateDate), caow.caoID, caow.status, temp.CaoStatus, caow.notes
	from contractor_audit_operator_workflow caow
	join (select distinct cao.id, cao.status as 'CaoStatus' from contractor_audit ca
		join audit_type adt on adt.id = ca.auditTypeID
		join contractor_audit_operator cao on cao.auditID = ca.id
		where adt.classType = 'Policy'
		and cao.status = 'Incomplete') as temp on temp.id = caow.caoID
	group by caow.caoID) as temp
	where (temp.notes like '%limits not met%' 
		or temp.notes like '%does not meet minimum requirement%'
		or temp.notes like '%limits%')
	and temp.notes not like '%Statutory%') as temp2 on temp2.caoID = contractor_audit_operator.id
	set contractor_audit_operator.auditSubStatus = 'LimitsNotMet'
	where contractor_audit_operator.auditSubStatus is null;

	

update contractor_audit_operator
	join (select * from (select caow.id, max(caow.updateDate), caow.caoID, caow.status, temp.CaoStatus, caow.notes
	from contractor_audit_operator_workflow caow
	join (select distinct cao.id, cao.status as 'CaoStatus' from contractor_audit ca
		join audit_type adt on adt.id = ca.auditTypeID
		join contractor_audit_operator cao on cao.auditID = ca.id
		where adt.classType = 'Policy'
		and cao.status = 'Incomplete') as temp on temp.id = caow.caoID
	group by caow.caoID) as temp
	where temp.notes like '%Additional Insured%') as temp2 on temp2.caoID = contractor_audit_operator.id
	set contractor_audit_operator.auditSubStatus = 'NoAdditionalInsured'
	where contractor_audit_operator.auditSubStatus is null;



update contractor_audit_operator
	join (select * from (select caow.id, max(caow.updateDate), caow.caoID, caow.status, temp.CaoStatus, caow.notes
	from contractor_audit_operator_workflow caow
	join (select distinct cao.id, cao.status as 'CaoStatus' from contractor_audit ca
		join audit_type adt on adt.id = ca.auditTypeID
		join contractor_audit_operator cao on cao.auditID = ca.id
		where adt.classType = 'Policy'
		and cao.status = 'Incomplete') as temp on temp.id = caow.caoID
	group by caow.caoID) as temp
	where (temp.notes like '%waiver of subrogation%'
		or temp.notes like '%No Waiver%')) as temp2 on temp2.caoID = contractor_audit_operator.id
	set contractor_audit_operator.auditSubStatus = 'NoWaiverOfSubrogation'
	where contractor_audit_operator.auditSubStatus is null;



update contractor_audit_operator
	join (select * from (select caow.id, max(caow.updateDate), caow.caoID, caow.status, temp.CaoStatus, caow.notes
	from contractor_audit_operator_workflow caow
	join (select distinct cao.id, cao.status as 'CaoStatus' from contractor_audit ca
		join audit_type adt on adt.id = ca.auditTypeID
		join contractor_audit_operator cao on cao.auditID = ca.id
		where adt.classType = 'Policy'
		and cao.status = 'Incomplete') as temp on temp.id = caow.caoID
	group by caow.caoID) as temp
	where temp.notes like '%certificate holder%') as temp2 on temp2.caoID = contractor_audit_operator.id
	set contractor_audit_operator.auditSubStatus = 'CertificateHolder'
	where contractor_audit_operator.auditSubStatus is null;


update contractor_audit_operator
	join (select * from (select caow.id, max(caow.updateDate), caow.caoID, caow.status, temp.CaoStatus, caow.notes
	from contractor_audit_operator_workflow caow
	join (select distinct cao.id, cao.status as 'CaoStatus' from contractor_audit ca
		join audit_type adt on adt.id = ca.auditTypeID
		join contractor_audit_operator cao on cao.auditID = ca.id
		where adt.classType = 'Policy'
		and cao.status = 'Incomplete') as temp on temp.id = caow.caoID
	group by caow.caoID) as temp
	where (temp.notes like '%provide endorsement%'
		or temp.notes like '%does not have a policy number%'
		or temp.notes like '%Changed Status for%'
		or temp.notes like '%upload your Industry Rate Letter%'
		or temp.notes like '%Verbiage need to read differently%'
		or temp.notes like '%upload your most recent Workplace Injury Summary Report%'
		or temp.notes like '%upload your most recent Workplace Injury Summary Report%')) as temp2 on temp2.caoID = contractor_audit_operator.id
	set contractor_audit_operator.auditSubStatus = 'Other'
	where contractor_audit_operator.auditSubStatus is null;



UPDATE contractor_audit_operator set auditSubStatus = 'NoValidCertificate' WHERE id = 1281679;
UPDATE contractor_audit_operator set auditSubStatus = 'NoValidCertificate' WHERE id = 1329124;
UPDATE contractor_audit_operator set auditSubStatus = 'LimitsNotMet' WHERE id = 1347338;
UPDATE contractor_audit_operator set auditSubStatus = 'NoValidCertificate' WHERE id = 1395131;
UPDATE contractor_audit_operator set auditSubStatus = 'LimitsNotMet' WHERE id = 1469214;
UPDATE contractor_audit_operator set auditSubStatus = 'Other' WHERE id = 1498732;
UPDATE contractor_audit_operator set auditSubStatus = 'NoValidCertificate' WHERE id = 1523823;
UPDATE contractor_audit_operator set auditSubStatus = 'Other' WHERE id = 1524396;
UPDATE contractor_audit_operator set auditSubStatus = 'Other' WHERE id = 1524398;
UPDATE contractor_audit_operator set auditSubStatus = 'Other' WHERE id = 1524399;
UPDATE contractor_audit_operator set auditSubStatus = 'Other' WHERE id = 1524403;
UPDATE contractor_audit_operator set auditSubStatus = 'Other' WHERE id = 1573314;
UPDATE contractor_audit_operator set auditSubStatus = 'LimitsNotMet' WHERE id = 1588268;
UPDATE contractor_audit_operator set auditSubStatus = 'LimitsNotMet' WHERE id = 1632347;