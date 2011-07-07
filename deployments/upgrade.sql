update audit_type set scoreType='Percent' where scoreable;

update invoice_fee set visible = 0 where id in (3,297,299,301,309,317,325);

-- PICS-2787
update app_translation 
set msgValue = replace(msgValue,'(US)','(US/Canada)')
where msgKey like '%TimeZone%'
and (msgValue like '%Pacific%(US)%'
or msgValue like '%Mountain%(US)%'
or msgValue like '%Central%(US)%'
or msgValue like '%Eastern%(US)%');