replace into app_properties values ('Toggle.Canonical_Timezone', 'true', null);

update accounts set timezone = 'Pacific/Pago_Pago' where timezone = 'Pacific/Samoa';
update accounts set timezone = 'America/Adak' where timezone = 'US/Aleutian';
update accounts set timezone = 'America/Anchorage' where timezone = 'US/Alaska';
update accounts set timezone = 'America/Los_Angeles' where timezone = 'US/Pacific';
update accounts set timezone = 'America/Phoenix' where timezone = 'US/Arizona';
update accounts set timezone = 'America/Denver' where timezone = 'US/Mountain';
update accounts set timezone = 'America/Regina' where timezone = 'Canada/Saskatchewan';
update accounts set timezone = 'America/Chicago' where timezone = 'US/Central';
update accounts set timezone = 'America/New_York' where timezone = 'US/Eastern';
update accounts set timezone = 'America/St_Johns' where timezone = 'Canada/Newfoundland';
update accounts set timezone = 'America/Noronha' where timezone = 'Brazil/DeNoronha';
update accounts set timezone = 'Etc/GMT' where timezone = 'Greenwich';
update accounts set timezone = 'America/Anchorage' where timezone = 'SystemV/YST9';

update users set timezone = 'Pacific/Pago_Pago' where timezone = 'Pacific/Samoa';
update users set timezone = 'America/Adak' where timezone = 'US/Aleutian';
update users set timezone = 'America/Anchorage' where timezone = 'US/Alaska';
update users set timezone = 'America/Los_Angeles' where timezone = 'US/Pacific';
update users set timezone = 'America/Phoenix' where timezone = 'US/Arizona';
update users set timezone = 'America/Denver' where timezone = 'US/Mountain';
update users set timezone = 'America/Regina' where timezone = 'Canada/Saskatchewan';
update users set timezone = 'America/Chicago' where timezone = 'US/Central';
update users set timezone = 'America/New_York' where timezone = 'US/Eastern';
update users set timezone = 'America/St_Johns' where timezone = 'Canada/Newfoundland';
update users set timezone = 'America/Noronha' where timezone = 'Brazil/DeNoronha';
update users set timezone = 'Etc/GMT' where timezone = 'Greenwich';
update users set timezone = 'America/Anchorage' where timezone = 'SystemV/YST9';