--liquibase formatted SQL
--changeset sshacter:10

ALTER TABLE	accounts
CHANGE	naics	naics	VARCHAR(10) CHARSET latin1 COLLATE latin1_swedish_ci DEFAULT '0'   NULL,
CHANGE 	naicsValid	naicsValid	TINYINT(4) DEFAULT 0  NULL
;
