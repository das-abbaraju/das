update app_translation a 
join ref_state b on b.isoCode = substr(a.msgkey, 15, 2)
join ref_country c on b.countryCode = c.isoCode 
set msgKey = concat('AuditQuestion.CA-', substr(a.msgkey, 15,2), '.name')
where a.msgValue = concat(b.english, ', ', c.english)
    and a.msgkey like 'AuditQuestion%' and b.countryCode = 'CA'
and locale='en'
