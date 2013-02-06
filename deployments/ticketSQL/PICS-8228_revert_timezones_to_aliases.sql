replace into app_properties values ('Toggle.Canonical_Timezone', 'false', null);

update accounts set timezone = 'Pacific/Samoa' where timezone = 'Pacific/Pago_Pago';
update accounts set timezone = 'US/Aleutian' where timezone = 'America/Adak';
update accounts set timezone = 'US/Alaska' where timezone = 'America/Anchorage';
update accounts set timezone = 'US/Pacific' where timezone = 'America/Los_Angeles';
update accounts set timezone = 'US/Arizona' where timezone = 'America/Phoenix';
update accounts set timezone = 'US/Mountain' where timezone = 'America/Denver';
update accounts set timezone = 'Canada/Saskatchewan' where timezone = 'America/Regina';
update accounts set timezone = 'US/Central' where timezone = 'America/Chicago';
update accounts set timezone = 'US/Eastern' where timezone = 'America/New_York';
update accounts set timezone = 'Canada/Newfoundland' where timezone = 'America/St_Johns';
update accounts set timezone = 'Brazil/DeNoronha' where timezone = 'America/Noronha';
update accounts set timezone = 'Greenwich' where timezone = 'Etc/GMT';

update users set timezone = 'Pacific/Samoa' where timezone = 'Pacific/Pago_Pago';
update users set timezone = 'US/Aleutian' where timezone = 'America/Adak';
update users set timezone = 'US/Alaska' where timezone = 'America/Anchorage';
update users set timezone = 'US/Pacific' where timezone = 'America/Los_Angeles';
update users set timezone = 'US/Arizona' where timezone = 'America/Phoenix';
update users set timezone = 'US/Mountain' where timezone = 'America/Denver';
update users set timezone = 'Canada/Saskatchewan' where timezone = 'America/Regina';
update users set timezone = 'US/Central' where timezone = 'America/Chicago';
update users set timezone = 'US/Eastern' where timezone = 'America/New_York';
update users set timezone = 'Canada/Newfoundland' where timezone = 'America/St_Johns';
update users set timezone = 'Brazil/DeNoronha' where timezone = 'America/Noronha';
update users set timezone = 'Greenwich' where timezone = 'Etc/GMT';