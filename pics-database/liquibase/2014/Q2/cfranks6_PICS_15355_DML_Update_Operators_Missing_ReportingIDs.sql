--liquibase formatted SQL

--changeset cfranks:6
CALL update_max_parent();