--liquibase formatted sql

--changeset mdo:63
UPDATE contractor_operator_number
SET opID = 62700
WHERE opID = 62697;