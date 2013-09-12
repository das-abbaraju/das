alter table app_properties modify column `value` varchar(4000);

replace into app_properties (property, value) 
values ('Toggle.v7Menus', 'userIsMemberOfAny(["PICS Developer", "PICS Stakeholder -Test Round 2"]) || permissions.username == "jo.burns@basf.com"');
