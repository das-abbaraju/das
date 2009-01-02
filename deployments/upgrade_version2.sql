/** UPGRADE TABLES AND COLUMNS DDL **/

alter table `pqfdata` 
	change `dataID` `id` bigint(20) unsigned   NOT NULL auto_increment first, 
	drop key `PRIMARY`, add PRIMARY KEY(`id`);

alter table `pqfdata` 
	add column `parentID` bigint(20) unsigned   NULL after `questionID`, 
	drop column `num`, 
	drop key `questionContractor`, add KEY `questionContractor`(`auditID`,`questionID`,`parentID`);

update pqfdata set parentID = null;

alter table `pqfquestions` 
	change `questionID` `id` smallint(6)   NOT NULL auto_increment first,
	drop key `PRIMARY`, add PRIMARY KEY(`id`);

alter table `pqfquestions` 
	change `subCategoryID` `subCategoryID` smallint(6)   NOT NULL DEFAULT '0' after `id`, 
	add column `createdBy` int(11)   NULL after `question`, 
	add column `updatedBy` int(11)   NULL after `createdBy`, 
	change `dateCreated` `creationDate` datetime   NOT NULL after `updatedBy`, 
	change `lastModified` `updateDate` datetime   NOT NULL after `creationDate`, 
	add column `allowMultipleAnswers` tinyint(4)   NULL DEFAULT '0' after `isRequired`, 
	add column `parentID` smallint(5) unsigned   NULL after `isRedFlagQuestion`;


/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;


update notes set userID from whois and opID
update notes set deletedUserID from whoDeleted and opID


insert into note (accountID, creationDate, createdBy, summary, noteCategory, priority, viewableBy, body)
select id, accountDate, 959, 'Contractor Notes Pre-Oct08', 'General', 3, 1, notes
from contractor_info
where notes > '';

insert into note (accountID, creationDate, createdBy, summary, noteCategory, priority, viewableBy, body)
select id, accountDate, 959, 'PICS-only Notes Pre-Oct08', 'General', 3, 1100, adminNotes
from contractor_info
where adminNotes > '';

insert into note (accountID, creationDate, createdBy, updatedBy, updateDate, summary, noteCategory, status, priority, viewableBy, body)
select conID, timeStamp, case ISNULL(userID) when 1 then 959 else userID end, deletedDate, deletedUserID, note, 'General', case isDeleted when 1 then 0 else 2 end, 3, opID, null
from notes
where length(note) <= 250;

insert into note (accountID, creationDate, createdBy, updatedBy, updateDate, summary, noteCategory, status, priority, viewableBy, body)
select conID, timeStamp, case ISNULL(userID) when 1 then 959 else userID end, deletedDate, deletedUserID, substring(note, 1, 255), 'General', case isDeleted when 1 then 0 else 2 end, 3, opID, substring(note, 255)
from notes
where length(note) > 250;
*/


/*
These contractors have two certificates for at least two separate operators, but the liabilitylimits are not the same.  Which

select * from certificates_old d where
exists ( 
	select contractor_id, type from certificates_old a where 
	d.contractor_id = a.contractor_id
	and d.type = a.type
	and not exists
	(
	select *
	from certificates_old b
	where b.contractor_id = a.contractor_id
	group by contractor_id, type
	having avg(b.liabilitylimit) = a.liabilitylimit
	)
)
order by contractor_id, type
*/


--create the audits
		--insert into contractor_audit
		select distinct null auditid, at.auditTypeId, cert.contractor_id, now() createddate, 
			case when cert.status = 'Expired' then 'Expired'
				when cert.verified = 'Yes' then 'Active'
				when cert.verified = 'No' then 'Submitted' end status,
			cert.expdate,
			null auditorid,
			now() assigneddate,
			now() scheduleddate,
			case when cert.status = 'Expired' then cert.expdate
				when cert.status = 'Pending' then null
				when cert.verified = 'Yes' then cert.lastSentDate
				when cert.verified = 'No' then null end completeddate, 

			case when cert.status = 'Expired' then cert.expdate
				when cert.status = 'Pending' then null
				when cert.verified = 'Yes' then cert.lastSentDate
				when cert.verified = 'No' then null end closeddate,
			null requestedbyopid,
			null auditlocation,
			case when cert.status = 'Expired' then 100
				when cert.status = 'Pending' then 0
				when cert.status = 'Rejected' then 0
				when cert.verified = 'Approved' then 100 
				when cert.verified = 'Yes' then 100 end percentcomplete,

			case when cert.verified = 'Yes' then 100 else 0 end percentverified,
			null contractorconfirm,
			null auditorconfirm,
			0 manuallyadded,
			case when c_dup.cert_id is not null then concat('',c_dup.cert_id) else null end auditfor
		from certificates_old cert 
			join (
				Select 'General Liability' as audittype,'General Liability' as certtype union
				Select 'Workers Comp','Worker''s Comp' union
				Select 'Automobile Liability','Automobile' union
				Select 'Excess/Umbrella Liability','Excess/Umbrella' union
				Select 'Professional Liability','Professional Liability' union
				Select 'Pollution Liability','Pollution Liability' union
				Select 'Contractor Liability','Contractor Liability' union
				Select 'Employer''s Liability','Employer''s Liability' union
				Select 'E&O Liability','E&O' ) mapper on cert.type = mapper.certtype

			join audit_type at on at.auditName = mapper.audittype and at.classType = 'Policy'
		left outer join (
			select * from certificates_old d_dup where
				exists ( 
					select contractor_id, type from certificates_old a_dup where 
					d_dup.contractor_id = a_dup.contractor_id
					and d_dup.type = a_dup.type
					and not exists
					(
					select *
					from certificates_old b_dup
					where b_dup.contractor_id = a_dup.contractor_id
					group by contractor_id, type
					having avg(b_dup.liabilitylimit) = a_dup.liabilitylimit
					)
				)
			) c_dup on c_dup.cert_id = cert.cert_id
		where not exists (
			select * from contractor_audit exist_check where exist_check.conid = cert.contractor_id and exist_check.audittypeid = at.audittypeid
		)


	
	
--create the conauditoperators
	--insert into contractor_audit_operator
	select null id, ca.auditid, cert.operator_id, 
			case when cert.status = 'Expired' then 'Pending' else cert.status end , 

	cert.reason notes, 12345 createdby, 12345 updatedby, now() creationDate, now() updateDate
	from certificates_old cert 
		join (
			Select 'General Liability' as audittype,'General Liability' as certtype union
			Select 'Workers Comp','Worker''s Comp' union
			Select 'Automobile Liability','Automobile' union
			Select 'Excess/Umbrella Liability','Excess/Umbrella' union
			Select 'Professional Liability','Professional Liability' union
			Select 'Pollution Liability','Pollution Liability' union
			Select 'Contractor Liability','Contractor Liability' union
			Select 'Employer''s Liability','Employer''s Liability' union
			Select 'E&O Liability','E&O' ) mapper on cert.type = mapper.certtype

		join audit_type at on at.auditName = mapper.audittype and at.classType = 'Policy'
		join contractor_audit ca on ca.conid = cert.contractor_id and ca.audittypeid = at.audittypeid

	where not exists
		(select * from contractor_audit_operator cao where cao.auditid = ca.auditid and cao.opid = cert.operator_id)


--create the pqfdata
	--limits
		--insert into pqfdata
		select null id, ca.auditid, questionmapper.qid, null, cert.liabilitylimit, '', '0000-00-00', null, 'No', 12345, now(), 12345, now()
		from certificates_old cert 
			join (
				Select 'General Liability' as audittype,'General Liability' as certtype union
				Select 'Workers Comp','Worker''s Comp' union
				Select 'Automobile Liability','Automobile' union
				Select 'Excess/Umbrella Liability','Excess/Umbrella' union
				Select 'Professional Liability','Professional Liability' union
				Select 'Pollution Liability','Pollution Liability' union
				Select 'Contractor Liability','Contractor Liability' union
				Select 'Employer''s Liability','Employer''s Liability' union
				Select 'E&O Liability','E&O' ) mapper on cert.type = mapper.certtype
			join audit_type at on at.auditName = mapper.audittype and at.classType = 'Policy'
			join contractor_audit ca on ca.conid = cert.contractor_id and ca.audittypeid = at.audittypeid
			join (
				Select 'General Liability' as audittype, 2074 qid union
				Select 'Workers Comp', 2149 union
				Select 'Automobile Liability',2155 union
				Select 'Excess/Umbrella Liability',2165 union
				Select 'Professional Liability',2167 union
				Select 'Pollution Liability',2173 union
				Select 'Contractor Liability',2179 union
				Select 'Employer''s Liability',2185 union
				Select 'Employer''s Liability',2186 union
				Select 'Employer''s Liability',2187 union
				Select 'E&O Liability',2191 ) questionmapper on questionmapper.audittype = at.auditname
		where not exists ( select * from pqfdata abc where abc.auditid = ca.auditid and abc.questionid = questionmapper.qid ) 		
	
	--expiration dates		
		--insert into pqfdata	
	select null id, ca.auditid, questionmapper.qid, null, cert.expdate, '', '0000-00-00', null, 'No', 12345, now(), 12345, now()
	from certificates_old cert 
		join (
			Select 'General Liability' as audittype,'General Liability' as certtype union
			Select 'Workers Comp','Worker''s Comp' union
			Select 'Automobile Liability','Automobile' union
			Select 'Excess/Umbrella Liability','Excess/Umbrella' union
			Select 'Professional Liability','Professional Liability' union
			Select 'Pollution Liability','Pollution Liability' union
			Select 'Contractor Liability','Contractor Liability' union
			Select 'Employer''s Liability','Employer''s Liability' union
			Select 'E&O Liability','E&O' ) mapper on cert.type = mapper.certtype
		join audit_type at on at.auditName = mapper.audittype and at.classType = 'Policy'
		join contractor_audit ca on ca.conid = cert.contractor_id and ca.audittypeid = at.audittypeid
		join (
			Select 'General Liability' as audittype, 2082 qid union
			Select 'Workers Comp', 2105 union
			Select 'Automobile Liability',2111 union
			Select 'Excess/Umbrella Liability',2117 union
			Select 'Professional Liability',2123 union
			Select 'Pollution Liability',2129 union
			Select 'Contractor Liability',2135 union
			Select 'Employer''s Liability',2141 union
			Select 'E&O Liability',2147 ) questionmapper on questionmapper.audittype = at.auditname
	where not exists ( select * from pqfdata abc where abc.auditid = ca.auditid and abc.questionid = questionmapper.qid ) 		


	--file extensions
		--insert into pqfdata
		select null id, ca.auditid, questionmapper.qid, null, cert.ext, '', '0000-00-00', null, 'No', 12345, now(), 12345, now()
	from certificates_old cert 
		join (
			Select 'General Liability' as audittype,'General Liability' as certtype union
			Select 'Workers Comp','Worker''s Comp' union
			Select 'Automobile Liability','Automobile' union
			Select 'Excess/Umbrella Liability','Excess/Umbrella' union
			Select 'Professional Liability','Professional Liability' union
			Select 'Pollution Liability','Pollution Liability' union
			Select 'Contractor Liability','Contractor Liability' union
			Select 'Employer''s Liability','Employer''s Liability' union
			Select 'E&O Liability','E&O' ) mapper on cert.type = mapper.certtype
		join audit_type at on at.auditName = mapper.audittype and at.classType = 'Policy'
		join contractor_audit ca on ca.conid = cert.contractor_id and ca.audittypeid = at.audittypeid
		join (
			Select 'General Liability' as audittype, 2073 qid union
			Select 'Workers Comp', 2101 union
			Select 'Automobile Liability',2107 union
			Select 'Excess/Umbrella Liability',2113 union
			Select 'Professional Liability',2119 union
			Select 'Pollution Liability',2125 union
			Select 'Contractor Liability',2131 union
			Select 'Employer''s Liability',2137 union
			Select 'E&O Liability',2143 ) questionmapper on questionmapper.audittype = at.auditname
		where not exists ( select * from pqfdata abc where abc.auditid = ca.auditid and abc.questionid = questionmapper.qid ) 
		
	

--create the auditcatdata
		--insert into pqfcatdata
		select null id, catmapper.catid, ca.auditid, 0,0,0,'Yes',0,0,0,0
			from certificates_old cert 
				join (
					Select 'General Liability' as audittype,'General Liability' as certtype union
					Select 'Workers Comp','Worker''s Comp' union
					Select 'Automobile Liability','Automobile' union
					Select 'Excess/Umbrella Liability','Excess/Umbrella' union
					Select 'Professional Liability','Professional Liability' union
					Select 'Pollution Liability','Pollution Liability' union
					Select 'Contractor Liability','Contractor Liability' union
					Select 'Employer''s Liability','Employer''s Liability' union
					Select 'E&O Liability','E&O' ) mapper on cert.type = mapper.certtype
				join audit_type at on at.auditName = mapper.audittype and at.classType = 'Policy'
				join contractor_audit ca on ca.conid = cert.contractor_id and ca.audittypeid = at.audittypeid
				join (
					Select 'General Liability' as audittype, 160 catid union
					Select 'Workers Comp', 161 union
					Select 'Automobile Liability',162 union
					Select 'Excess/Umbrella Liability',163 union
					Select 'Professional Liability',172 union
					Select 'Pollution Liability',173 union
					Select 'Contractor Liability',174 union
					Select 'Employer''s Liability',175 union
					Select 'E&O Liability',176 ) catmapper on catmapper.audittype = at.auditname
				where not exists ( select * from pqfcatdata abc where abc.auditid = ca.auditid and abc.catid = catmapper.catid ) 
		

/*
we need to have something that:
	periodically migrates additionalinsureds
	make all gap questions have an effective date of X - Y	
*/