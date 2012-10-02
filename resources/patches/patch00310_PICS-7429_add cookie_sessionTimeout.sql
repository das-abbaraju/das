alter ignore table accounts add rememberMeTime tinyint;
alter ignore table accounts add sessionTimeout tinyint;

insert ignore into app_translation (msgkey, locale, msgValue, createdBy, creationDate, qualityRating, applicable)
values('Login.PasswordIncorrect', 'en', 'Username and password do not match', 1, CURDATE(), 2, 1);

insert ignore into app_translation (msgkey, locale, msgValue, createdBy, creationDate, qualityRating, applicable)
values('OperatorAccount.rememberMeTime', 'en', 'Remember Me Timeout', 1, CURDATE(), 2, 1);

insert ignore into app_translation (msgkey, locale, msgValue, createdBy, creationDate, qualityRating, applicable)
values('inOperatorAccount.rememberMeTime.fieldhelp', 'en', 'How many days do you want to stay logged in?', 1, CURDATE(), 2, 1);

insert ignore into app_translation (msgkey, locale, msgValue, createdBy, creationDate, qualityRating, applicable)
values('OperatorAccount.sessionTimeout', 'en', 'Session Timeout', 1, CURDATE(), 2, 1);

insert ignore into app_translation (msgkey, locale, msgValue, createdBy, creationDate, qualityRating, applicable)
values('FacilitiesEdit.Security', 'en', 'Security', 1, CURDATE(), 2, 1);

update ignore app_translation 
set msgValue = 'This account is locked because of too many failed attempt' 
where msgKey = 'Login.TooManyFailedAttempts' and locale='en' 
