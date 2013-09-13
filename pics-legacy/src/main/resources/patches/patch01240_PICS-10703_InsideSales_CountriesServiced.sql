-- | Anthony   | Rodriguez | 50606 |
insert into user_country (userID, isoCode)
  select 50606, 'CA' from dual
  where not exists (select id from user_country where userID = 50606 and isoCode = 'CA');
insert into user_country (userID, isoCode)
  select 50606, 'US' from dual
  where not exists (select id from user_country where userID = 50606 and isoCode = 'US');
-- | Chris     | Ha        | 54929 |
insert into user_country (userID, isoCode)
  select 54929, 'CA' from dual
  where not exists (select id from user_country where userID = 54929 and isoCode = 'CA');
insert into user_country (userID, isoCode)
  select 54929, 'US' from dual
  where not exists (select id from user_country where userID = 54929 and isoCode = 'US');
-- | Rikki     | Marquis   | 44965 |
insert into user_country (userID, isoCode)
  select 44965, 'CA' from dual
  where not exists (select id from user_country where userID = 44965 and isoCode = 'CA');
insert into user_country (userID, isoCode)
  select 44965, 'US' from dual
  where not exists (select id from user_country where userID = 44965 and isoCode = 'US');
-- | Chris     | Hendricks | 54928 |
insert into user_country (userID, isoCode)
  select 54928, 'CA' from dual
  where not exists (select id from user_country where userID = 54928 and isoCode = 'CA');
insert into user_country (userID, isoCode)
  select 54928, 'US' from dual
  where not exists (select id from user_country where userID = 54928 and isoCode = 'US');
-- | Matt      | Bevens    | 79347 |
insert into user_country (userID, isoCode)
  select 79347, 'GB' from dual
  where not exists (select id from user_country where userID = 79347 and isoCode = 'GB');
insert into user_country (userID, isoCode)
  select 79347, 'ZA' from dual
  where not exists (select id from user_country where userID = 79347 and isoCode = 'ZA');
-- | Stewart   | Gatehouse | 93315 |
insert into user_country (userID, isoCode)
  select 93315, 'GB' from dual
  where not exists (select id from user_country where userID = 93315 and isoCode = 'GB');
insert into user_country (userID, isoCode)
  select 93315, 'ZA' from dual
  where not exists (select id from user_country where userID = 93315 and isoCode = 'ZA');
-- | Lauren    | Reardon   | 91624 |
insert into user_country (userID, isoCode)
  select 91624, 'GB' from dual
  where not exists (select id from user_country where userID = 91624 and isoCode = 'GB');
insert into user_country (userID, isoCode)
  select 91624, 'ZA' from dual
  where not exists (select id from user_country where userID = 91624 and isoCode = 'ZA');
-- | Joel      | Wood      | 65929 |
insert into user_country (userID, isoCode)
  select 65929, 'GB' from dual
  where not exists (select id from user_country where userID = 65929 and isoCode = 'GB');
insert into user_country (userID, isoCode)
  select 65929, 'ZA' from dual
  where not exists (select id from user_country where userID = 65929 and isoCode = 'ZA');
