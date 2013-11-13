--liquibase formatted sql

--changeset rbeaini:3

TRUNCATE app_user;

INSERT INTO  app_user ( `username` ,  `password` , `hashSalt` ) SELECT DISTINCT `username`,`password`,`id` FROM  users ;

UPDATE users JOIN app_user ON app_user.username  = users.username  AND app_user.password = users.password SET appUserID = app_user.id;
