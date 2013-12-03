--liquibase formatted sql

--changeset sshacter:14a splitStatements:true endDelimiter:|
DROP VIEW IF EXISTS vwMsg_Key_Locale;

--changeset sshacter:14b splitStatements:true endDelimiter:|
CREATE OR REPLACE DEFINER =`pics_admin`@`%` VIEW	vwMsg_Key_Locale
(
	keyID
,	msgKey
,	locale
,	msgValue
,	description
,	js
,	firstUsed
,	lastUsed
,	qualityRating
)
AS
SELECT
	msg_key.id
,	msg_key.msgKey
,	msg_locale.locale
,	msg_locale.msgValue
,	msg_key.description
,	msg_key.js
,	msg_locale.firstUsed
,	msg_locale.lastUsed
,	msg_locale.qualityRating
FROM
	msg_key
LEFT JOIN
	msg_locale
ON	msg_locale.keyID	= msg_key.id
;
