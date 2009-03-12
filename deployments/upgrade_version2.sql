/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;
*/

-- remove facilities referencing 'AES Corporate' - bad data
delete
from facilities
where corporateID = (select
                       id
                     from accounts
                     where name = "AES Corporate");
-- Set all operators to the parents, if there is only one record in the facilities table
update operators child
  join facilities f
    on child.id = f.opid
  join operators parent
    on parent.id = f.corporateID
set child.parentid = parent.id
where child.id in(Select
                    t.opid
                  from (select
                          f.opid,
                          count(f.opid) as total
                        from operators child
                          join facilities f
                            on child.id = f.opid
                          join operators parent
                            on parent.id = f.corporateid
                        group by f.opid) t
                  where total = 1);
-- Update the BASF Hubs to be children of Corporate (6115)
update operators o
  join accounts a
    on o.id = a.id
set o.parentid = 6115
where a.name like "%BASF Hub%";
-- Set all of the BASF hubs to be parents of their respective operators
update (operators child
   join accounts cha
     on child.id = cha.id)
  join facilities f
    on f.opid = child.id
  join (operators parent
   join accounts pa
     on parent.id = pa.id)
    on f.corporateid = parent.id
set child.parentID = parent.id
where pa.name like "%BASF Hub%";
-- Fix SOPUS, CRI, and ConocoPhillips
update operators child
  join facilities f
    on f.opid = child.id
  join operators parent
    on f.corporateid = parent.id
set child.parentID = parent.id
Where parent.id = 1488 or parent.id = 2020 or parent.id = 1937;
-- Set SOPUS and CRI to be children of Shell
update operators
set parentid = 1336
where id = 1488
     or id = 1937;
