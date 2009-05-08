/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;
*/
UPDATE audit_type set classType = 'PQF'
where auditName like 'PQF%';

update flagoshacriteria set lwcrHurdleType = 'None', trirHurdleType = 'None', fatalitiesHurdleType = 'None';

update flagoshacriteria set lwcrHurdleType = 'Absolute' where flagLwcr = 'Yes';
update flagoshacriteria set trirHurdleType = 'Absolute' where flagTrir = 'Yes';
update flagoshacriteria set fatalitiesHurdleType = 'Absolute' where flagFatalities = 'Yes';

