-- PICS-1717 --
update ref_country rc set rc.english = 'United States Minor Islands', rc.spanish = 'United States Minor Islands', rc.french = 'United States Minor Islands' where rc.isoCode = 'UM';
update ref_country rc set rc.english = 'South Georgia & Sandwich Islands', rc.spanish = 'South Georgia & Sandwich Islands', rc.french = 'South Georgia & Sandwich Islands' where rc.isoCode = 'GS';
-- END --

alter table `users` add column `locale` varchar(5) DEFAULT 'en' NULL;
alter table `accounts` add column `locale` varchar(5) DEFAULT 'en' NULL;
