--liquibase formatted sql

--changeset sshacter:1
CREATE TABLE IF NOT EXISTS _lbexample
(
    id int(11) NOT NULL
  , name varchar(255)
  , PRIMARY KEY(id)
)
COLLATE=utf8_general_ci CHECKSUM=0 DELAY_KEY_WRITE=0
;

