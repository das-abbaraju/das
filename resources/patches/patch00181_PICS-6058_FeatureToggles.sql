alter table app_properties add column ticklerDate datetime;

select @Badge:=value from app_properties where property = 'Toggle.Badge';
insert ignore into app_properties (property, value) values ('Toggle.Badge_v2', concat('releaseToUserAudienceLevel(',
@Badge,
')'));

select @DynamicReports:=value from app_properties where property = 'Toggle.DynamicReports';
insert ignore into app_properties (property, value) values ('Toggle.DynamicReports_v2', concat('releaseToUserAudienceLevel(',
@DynamicReports,
')'));

select @LcCor:=value from app_properties where property = 'Toggle.LcCor';
insert ignore into app_properties (property, value) values ('Toggle.LcCor_v2', concat('releaseToApplicationAudienceLevel(',
@LcCor,
')'));

select @LiveAgent:=value from app_properties where property = 'Toggle.LiveAgent';
insert ignore into app_properties (property, value) values ('Toggle.LiveAgent_v2', concat('releaseToUserAudienceLevel(',
@LiveAgent,
')'));

select @SwitchUserServer:=value from app_properties where property = 'Toggle.SwitchUserServer';
insert ignore into app_properties (property, value) values ('Toggle.SwitchUserServer_v2', concat('releaseToUserAudienceLevel(',
@SwitchUserServer,
')'));

delete from app_properties where property like 'Toggle.%_v2' and value is null;
