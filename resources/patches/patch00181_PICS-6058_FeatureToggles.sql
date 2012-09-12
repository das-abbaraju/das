alter table app_properties add column ticklerDate datetime;

select @Badge:=value from app_properties where property = 'Toggle.Badge';
update app_properties set value = concat('releaseToUserAudienceLevel(',
@Badge,
')') where property = 'Toggle.Badge';

select @DynamicReports:=value from app_properties where property = 'Toggle.DynamicReports';
update app_properties set value = concat('releaseToUserAudienceLevel(',
@DynamicReports,
')') where property = 'Toggle.DynamicReports';

select @LcCor:=value from app_properties where property = 'Toggle.LcCor';
update app_properties set value = concat('releaseToApplicationAudienceLevel(',
@LcCor,
')') where property = 'Toggle.LcCor';

select @LiveAgent:=value from app_properties where property = 'Toggle.LiveAgent';
update app_properties set value = concat('releaseToUserAudienceLevel(',
@LiveAgent,
')') where property = 'Toggle.LiveAgent';

select @SwitchUserServer:=value from app_properties where property = 'Toggle.SwitchUserServer';
update app_properties set value = concat('releaseToUserAudienceLevel(',
@SwitchUserServer,
')') where property = 'Toggle.SwitchUserServer';
