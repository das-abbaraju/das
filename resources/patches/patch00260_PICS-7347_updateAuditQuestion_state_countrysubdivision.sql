update app_translation a join (select
          english
        from ref_state
        where countrycode in ('US', 'CA')) b
    on a.msgValue = b.english
set msgkey = concat('AuditQuestion.US-', SUBSTR(msgkey, 15, 10))
where not ceil(substr(msgkey, 14, 5))
    and msgkey like 'AuditQuestion%';