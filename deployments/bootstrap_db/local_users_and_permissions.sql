create user 'pics'@'%' identified by '8arTyRev';
create user 'pics_admin'@'%' identified by 'e4639db1b2766b9ad4fcd9db71354e02';
create user 'picsro'@'%' identified by '6liEsbAr';
-- create user 'pics_translations'@'%' identified by 'hAp2viNy';
grant select, insert, update, delete, drop, execute, create view, trigger on pics_bootstrap.* to 'pics_admin'@'%';
grant select, insert, update, delete, drop, execute, create view, trigger on log_archive.* to 'pics_admin'@'%';
grant all on pics_bootstrap.* to 'pics'@'%';
grant all on log_archive.* to 'pics'@'%';
grant select on pics_bootstrap.* to 'picsro'@'%';
-- grant all on pics_translations.* to 'pics_translations'@'%';

