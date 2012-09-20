insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, sourceLanguage, contentDriven)
values ('Month.Short.Jan', 'en', 'Jan', 1, 1, now(), now(), 'en', 0);
insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, sourceLanguage, contentDriven)
values ('Month.Short.Feb', 'en', 'Feb', 1, 1, now(), now(), 'en', 0);
insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, sourceLanguage, contentDriven)
values ('Month.Short.Mar', 'en', 'Mar', 1, 1, now(), now(), 'en', 0);
insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, sourceLanguage, contentDriven)
values ('Month.Short.Apr', 'en', 'Apr', 1, 1, now(), now(), 'en', 0);
insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, sourceLanguage, contentDriven)
values ('Month.Short.May', 'en', 'May', 1, 1, now(), now(), 'en', 0);
insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, sourceLanguage, contentDriven)
values ('Month.Short.Jun', 'en', 'Jun', 1, 1, now(), now(), 'en', 0);
insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, sourceLanguage, contentDriven)
values ('Month.Short.Jul', 'en', 'Jul', 1, 1, now(), now(), 'en', 0);
insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, sourceLanguage, contentDriven)
values ('Month.Short.Aug', 'en', 'Aug', 1, 1, now(), now(), 'en', 0);
insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, sourceLanguage, contentDriven)
values ('Month.Short.Sep', 'en', 'Sep', 1, 1, now(), now(), 'en', 0);
insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, sourceLanguage, contentDriven)
values ('Month.Short.Oct', 'en', 'Oct', 1, 1, now(), now(), 'en', 0);
insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, sourceLanguage, contentDriven)
values ('Month.Short.Nov', 'en', 'Nov', 1, 1, now(), now(), 'en', 0);
insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, sourceLanguage, contentDriven)
values ('Month.Short.Dec', 'en', 'Dec', 1, 1, now(), now(), 'en', 0);

insert ignore into widget (widgetID, caption, widgetType, synchronous, url, chartType) 
values (37, 'Operator Flag Year History', 'Chart', '0', 'OperatorFlagYearHistoryAjax.action', 'StackedColumn2D');

insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, sourceLanguage, contentDriven)
values ('Widget.37.caption', 'en', 'Operator Flag History', 1, 1, now(), now(), 'en', 0);
insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, sourceLanguage, contentDriven)
values ('Widget.37.caption', 'de', 'Auftraggeber Flaggengeschichte', 1, 1, now(), now(), 'en', 0);
insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, sourceLanguage, contentDriven)
values ('Widget.37.caption', 'es', 'Operador de Historia de la bandera', 1, 1, now(), now(), 'en', 0);
insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, sourceLanguage, contentDriven)
values ('Widget.37.caption', 'fi', 'Operaattorin merkkihistoria', 1, 1, now(), now(), 'en', 0);
insert ignore into app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, sourceLanguage, contentDriven)
values ('Widget.37.caption', 'fr', 'Historique des signalements par opérateur', 1, 1, now(), now(), 'en', 0);

update widget_user set widgetID = 37 where widgetID = 24 and userID = 616;