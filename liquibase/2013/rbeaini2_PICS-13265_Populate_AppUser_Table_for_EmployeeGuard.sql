--liquibase formatted sql

--changeset rbeaini:2

INSERT INTO  app_user ( username ,  `password` ) SELECT DISTINCT username,`password` FROM  users ;
 
UPDATE  users JOIN app_user ON app_user.username  = users.username  SET appUserID = app_user.id;
