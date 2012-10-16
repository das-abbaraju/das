alter ignore table accounts add rememberMeTime tinyint default -1;
alter ignore table accounts add sessionTimeout tinyint unsigned default 60;

insert ignore into app_translation (msgkey, locale, msgValue, createdBy, creationDate, qualityRating, applicable)
values('Login.PasswordIncorrect', 'en', 'Username and password do not match', 1, CURDATE(), 2, 1);

insert ignore into app_translation (msgkey, locale, msgValue, createdBy, creationDate, qualityRating, applicable)
values('OperatorAccount.rememberMeTimeInDays', 'en', 'Remember Me Timeout', 1, CURDATE(), 2, 1);

insert ignore into app_translation (msgkey, locale, msgValue, createdBy, creationDate, qualityRating, applicable)
values('OperatorAccount.rememberMeTimeInDays.fieldhelp', 'en', 'How many days do you want to stay logged in?', 1, CURDATE(), 2, 1);

insert ignore into app_translation (msgkey, locale, msgValue, createdBy, creationDate, qualityRating, applicable)
values('OperatorAccount.sessionTimeout', 'en', 'Session Timeout', 1, CURDATE(), 2, 1);

insert ignore into app_translation (msgkey, locale, msgValue, createdBy, creationDate, qualityRating, applicable)
values('FacilitiesEdit.Security', 'en', 'Security', 1, CURDATE(), 2, 1);

insert ignore into app_translation (msgkey, locale, msgValue, createdBy, creationDate, qualityRating, applicable)
values('Login.NoPermissionToRememberMe', 'en', 'You clicked remember-me but your account does not allow this setting. Your credentials will be removed when your browser is restarted.', 1, CURDATE(), 2, 1);

update ignore app_translation 
set msgValue = 'This account is locked because of too many failed attempts' 
where msgKey = 'Login.TooManyFailedAttempts' and locale='en';
