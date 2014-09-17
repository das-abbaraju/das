--liquibase formatted sql
--changeset sshacter:13

ALTER TABLE email_subscription
CHARSET=utf8, COLLATE=utf8_general_ci
;
ALTER TABLE email_subscription
CHANGE userID userID INT(11) NOT NULL
;
ALTER TABLE email_subscription
ADD CONSTRAINT 	fk1_email_subscription FOREIGN KEY (userID) REFERENCES users(id) ON UPDATE RESTRICT ON DELETE RESTRICT
;
