/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;
**/

/** Update the requiresOQ for all contractors
 * we don't want to run this yet 
update accounts set requiresOQ = 1
where id in (select distinct conid from contractor_audit ca
join pqfdata pd on ca.id = pd.auditid
where pd.questionid = 894
and pd.answer = 'Yes');
**/

-- PICS-737: Updating CSRs in ref_state based on assignment script data
-- Ashley Prather
update ref_state set csrID = 22223
WHERE isoCode in ('AK','AZ','HI','ID','MT','NM','NV','OR','UT','WY') 
AND countryCode = 'US';

-- Derrick Piper
update ref_state set csrID = 23542
where (isoCode in ('TX') OR countryCode = 'CA');

-- Estevan Orozco
update ref_state set csrID = 940
WHERE (isoCode in ('CA','GU','PR','WA') OR countryCode not in ('US','CA'));

-- Kaitlyn O'Malley
update ref_state set csrID = 11067
where isoCode in ('AR','CO','IA','KS','LA','MO','ND','NE','OK','SD','WI')
AND countryCode = 'US';

-- Neal Chawla
update ref_state set csrID = 23550
where isoCode in ('AL','FL','GA','KY','OH','TN')
AND countryCode = 'US';

-- Tiffany Roberson
update ref_state set csrID = 22222
where isoCode in ('IL','IN','MI','MN','MS')
AND countryCode = 'US';

-- Valeree Claudio
update ref_state set csrID = 8397
where isoCode in ('CT','DE','MA','MD','ME','NC','NH','NJ','NY','PA','RI','SC','VA','VT','WV')
AND countryCode = 'US';

/* Deactive unused assessment centers except for OQSG, and set NACE and NCCER to pending */
update accounts set status = 'Deactivated' where type = 'Assessment' and name !='OQSG';

update accounts set status = 'Pending' where id = 11069 or id = 11087;