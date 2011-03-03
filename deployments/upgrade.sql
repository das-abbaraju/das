-- PICS-1717 --
update ref_country rc set rc.english = 'United States Minor Islands', rc.spanish = 'United States Minor Islands', rc.french = 'United States Minor Islands' where rc.isoCode = 'UM';
update ref_country rc set rc.english = 'South Georgia & Sandwich Islands', rc.spanish = 'South Georgia & Sandwich Islands', rc.french = 'South Georgia & Sandwich Islands' where rc.isoCode = 'GS';
-- END --

alter table `users` add column `locale` varchar(5) DEFAULT 'en' NULL;
alter table `accounts` add column `locale` varchar(5) DEFAULT 'en' NULL;


-- PICS-1961
update audit_type_rule set dependentAuditStatus = 'Submitted' where id in(2391,31120);

--PICS-1810
update `app_properties` set `value`='2' where `property` = 'schedule.mindays';
insert into useraccess ()userID, accessType, viewFlag, editFlag, deleteFlag, grantFlag, grantedByID)
values (1029, 'WebcamNotification', 1, 0, 0, 1, 23157), (11068, 'WebcamNotification', 1, 0, 0, 1, 23157);
update `invoice_fee` set `defaultAmount`='199.00',`updatedBy`='23157',`updateDate`= NOW() where `id`='51';