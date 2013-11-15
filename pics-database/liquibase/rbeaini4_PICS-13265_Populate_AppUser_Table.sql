--liquibase formatted sql

--changeset rbeaini:4

SET SQL_MODE='';
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO';
TRUNCATE app_user;

INSERT INTO  app_user ( `username` ,  `password` , `hashSalt` ) SELECT DISTINCT `username`,`password`,`id` FROM  users ;

UPDATE users JOIN app_user ON app_user.username  = users.username  AND app_user.password = users.password SET appUserID = app_user.id;
