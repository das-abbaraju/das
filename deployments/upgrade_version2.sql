/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;
*/


/* Update all operator.parentID's with the values that are in the facilities table */
update operators child
  join facilities f
    on child.id = f.opid
  join operators parent
    on f.corporateid = parent.id
set child.parentid = parent.id;

/* Since there are multiple facilities table entries for BASF Hub users
 * these should be updated afterwards*/
update operators child
  join facilities f
    on child.id = f.opid
  join operators parent
    on parent.id = f.corporateID
set child.parentid = parent.id
where parent.id in(select
                     id
                   from accounts
                   where name like "%BASF HUB%");


/*
 * Set the parentid of BASF Hub operators to BASF corporate
 */
update operators o
  join accounts a
    on o.id = a.id
set o.parentid = 6115
where a.name like "%BASF HUB%";
