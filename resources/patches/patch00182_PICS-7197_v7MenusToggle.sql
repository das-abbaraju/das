alter table app_properties modify column `value` varchar(4000);

replace into app_properties (property, value) 
values ('Toggle.v7Menus', 'userIsMemberOf("PICS Developer") || permissions.username == "jo.burns@basf.com"');
