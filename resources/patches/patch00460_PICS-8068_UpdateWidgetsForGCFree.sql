delete from widget_user
where userID = 61460
and widgetID = 5;

update widget_user
set sortOrder = 5, `column` = 2
where userID = 61460
and widgetID = 2;