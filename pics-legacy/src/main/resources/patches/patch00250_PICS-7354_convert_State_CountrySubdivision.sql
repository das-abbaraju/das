update audit_option_group set name='CountrySubdivision', uniqueCode = 'CountrySubdivision' where name='State' and uniquecode='State';

update pqfdata a join ref_state b on a.answer = b.english 
set a.answer = b.isocode 
where a.questionid = 96;


update pqfdata c join ref_state b on c.answer = b.isoCode
set answer = concat(b.countryCode, '-', b.isoCode)
where c.questionID = 96;

update audit_option_value a join ref_state b on a.uniquecode = b.isoCode
set uniquecode = concat(b.countryCode, '-', b.isoCode)
where uniquecode != '' and uniquecode is not null and typeid = 28;


update audit_option_value 
set uniquecode = concat('US-', uniquecode) 
where uniquecode in ('PR', 'GU', 'AS', 'MP', 'UM', 'VI');