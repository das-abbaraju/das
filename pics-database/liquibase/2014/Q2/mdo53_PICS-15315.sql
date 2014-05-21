--liquibase formatted sql

--changeset mdo:53
ALTER TABLE `ref_country`
  CHANGE `csrPhone` `csrPhone` VARCHAR(100) CHARSET utf8 COLLATE utf8_general_ci NULL,
  CHANGE `csrFax` `csrFax` VARCHAR(100) CHARSET utf8 COLLATE utf8_general_ci NULL,
  CHANGE `isrPhone` `isrPhone` VARCHAR(100) CHARSET utf8 COLLATE utf8_general_ci NULL,
  CHANGE `isrFax` `isrFax` VARCHAR(100) CHARSET utf8 COLLATE utf8_general_ci NULL;
