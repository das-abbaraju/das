--liquibase formatted sql

--changeset mdo:66
ALTER TABLE `pics_config`.`report`
  CHANGE `filterExpression` `filterExpression` VARCHAR(200) CHARSET utf8 COLLATE utf8_general_ci NULL;
