--liquibase formatted sql

--changeset kchase:20
update token
set velocityCode='${contractor.currentCsr.phone}'
where tokenID=32;

update token
set velocityCode='${contractor.currentCsr.email}'
where tokenID=33;

update app_translation
set msgValue='${contractor.currentCsr.phone}'
where msgKey='Token.32.velocityCode';

update app_translation
set msgValue='${contractor.currentCsr.email}'
where msgKey='Token.33.velocityCode';
