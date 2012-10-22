-- PICS-7689 fix broken user record
update users
set isGroup='No'
where id=78375;