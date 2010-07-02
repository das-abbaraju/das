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

-- PICS-727: Begin --
update generalcontractors set forcedBy = 776 where id = 5716;
update generalcontractors set forcedBy = 987 where id in (748, 9867, 11077, 11078, 12815, 23831);
update generalcontractors set forcedBy = 556 where id = 12764;
update generalcontractors set forcedBy = 2173 where id in (16317, 17795, 17797, 17798);
update generalcontractors set forcedBy = 2175 where id in (17842, 17843, 17844);
update generalcontractors set forcedBy = 8597 where id = 28914;
update generalcontractors set forcedBy = 11429 where id in (34391, 35382, 36494, 36864, 40366);
update generalcontractors set forcedBy = 10569 where id = 35912;
-- PICS-727: END --

/* Deactive unused assessment centers except for OQSG, and set NACE and NCCER to pending */
update accounts set status = 'Deactivated' where type = 'Assessment' and name !='OQSG';

update accounts set status = 'Pending' where id = 11069 or id = 11087;

/* Set Contractor logo to null where it is blank or No */
update contractor_info set logo_file = NULL where logo_file = '' or logo_file = 'No';