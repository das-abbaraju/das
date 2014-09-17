--liquibase formatted sql

--changeset dabbaraju:5
--preConditions onFail MARK_RAN

-- Below script insert data into IDP_USER table for the
-- mapping of windows login and Pics user id

INSERT INTO idp_user(userId, idpUserName, idp)
SELECT user.id, substring(user.email,1,instr(user.email,'@')-1 ),'picsad'
FROM users user
JOIN app_user au ON au.id = user.appUserID
WHERE
user.accountID = '1100' AND
user.isGroup = 'No' AND
user.email LIKE '%picsauditing%' AND
user.isactive = 'Yes' AND
user.email not like '%info@picsauditing.com%';
