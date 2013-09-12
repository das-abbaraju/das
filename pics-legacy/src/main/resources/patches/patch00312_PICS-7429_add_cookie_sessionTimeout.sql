update accounts set rememberMeTime = 7 where rememberMeTime = -1;

insert ignore into app_translation (msgkey, locale, msgValue, createdBy, creationDate, qualityRating, applicable)
values('OperatorAccount.rememberMeTimeEnabled', 'en', 'Enable "Remember Me" on login', 1, CURDATE(), 2, 1);

insert ignore into app_translation (msgkey, locale, msgValue, createdBy, creationDate, qualityRating, applicable)
values('OperatorAccount.rememberMeTimeEnabled.fieldhelp', 'en', 'Enable setting the number of days a user can stay logged in', 1, CURDATE(), 2, 1);

update ignore app_translation 
set msgValue = 'User Inactivity Timeout (Minutes)' 
where msgKey = 'OperatorAccount.sessionTimeout' and locale='en';

insert ignore into app_translation (msgkey, locale, msgValue, createdBy, creationDate, qualityRating, applicable)
values('OperatorAccount.sessionTimeout.fieldhelp', 'en', 'If "Remember Me" is not active or clicked on login, user will timeout after this time', 1, CURDATE(), 2, 1);

insert ignore into app_translation (msgkey, locale, msgValue, createdBy, creationDate, qualityRating, applicable)
values('FacilitiesEdit.RememberMeMustBePositive', 'en', '"Remember Me" days must be greater than zero (or disabled)', 1, CURDATE(), 2, 1);


