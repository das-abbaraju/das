-- PICS-7689 fix broken user records
update users
set isGroup='No'
where isGroup NOT in ('Yes', 'No');