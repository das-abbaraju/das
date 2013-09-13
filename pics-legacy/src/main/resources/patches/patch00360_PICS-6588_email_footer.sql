insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, sourceLanguage, contentDriven)
values ('EmailTemplate.footer.frontHTML', 'en', 'To opt out from further e-mails being sent to ', 1, 1, now(), now(), 'en', 0);

insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, sourceLanguage, contentDriven)
values ('EmailTemplate.footer.middle', 'en', 'visit ', 1, 1, now(), now(), 'en', 0);


insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, sourceLanguage, contentDriven)
values ('EmailTemplate.footer.back', 'en', 'Please allow 7-10 days for this to take effect. Note: Activating an account with that e-mail address in the future will automatically opt it back in.', 1, 1, now(), now(), 'en', 0);