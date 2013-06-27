-- update velocity tags
UPDATE app_translation app
SET msgValue="${contractor.currentCsr.name}"
WHERE app.msgKey = 'Token.31.velocityCode';

UPDATE app_translation app
SET msgValue="${contractor.currentCsr.phone}"
WHERE app.msgKey = 'Token.32.velocityCode';

UPDATE app_translation app
SET msgValue="${contractor.currentCsr.email}"
WHERE app.msgKey = 'Token.33.velocityCode';

UPDATE app_translation app
SET msgValue="${contractor.currentCsr.fax}"
WHERE app.msgKey = 'Token.34.velocityCode';
