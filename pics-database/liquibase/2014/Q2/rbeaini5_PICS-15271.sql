--liquibase formatted sql

--changeset rbeaini:5
UPDATE users SET resetHash = null WHERE id > 0 AND resetHash = '';
UPDATE users SET resetHash = null WHERE isActive = 'No';

UPDATE app_user au
JOIN users u ON u.appUserID = au.id
SET au.resetHash = u.resetHash
WHERE u.resetHash is not null
AND au.resetHash is null;
