CREATE TABLE IF NOT EXISTS app_user
(
	id	INT(11) NOT NULL AUTO_INCREMENT COMMENT "Authentication / Authorization ID"
,	username	VARCHAR(64) NOT NULL COMMENT "Unique user name"
,	`password` 	VARCHAR(64) NOT NULL COMMENT "The user's encrypted password"
,	hashSalt 	VARCHAR(64) NOT NULL COMMENT "Random data that are used as an additional input to a one-way function that hashes a password or passphrase"
,	resetHash 	VARCHAR(64) NOT NULL COMMENT "Password revocery hash"
,	lastLogin	DATETIME NULL COMMENT "The last date and time this user logged in"
,	PRIMARY KEY (id)
,	UNIQUE INDEX akApp_User (username)
)
COLLATE=utf8_general_ci CHECKSUM=0 DELAY_KEY_WRITE=0
;
