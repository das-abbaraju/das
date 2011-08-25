-- PICS-2680
-- update unique code for all questions related to office locations for any state/province available
update audit_question aq
join app_translation appt on msgkey = concat('AuditQuestion.',aq.id,'.name')
join ref_state rs on (rs.english = substring_index(msgValue,',',1) or (english like 'Newfoundland%' and msgValue like 'Newfoundland%') or (english like 'District%' and msgValue like 'Washington D%'))
set aq.uniqueCode = rs.isoCode
where aq.id in (1619,1620,1622,1623,1624,1625,1626,1627,1628,1629,1630,1631,1632,1633,1634,1635,1636,1637,1639,1640,1641,1642,1643,1644,1645,1646,1647,1650,1651,1652,1653,1654,
1655,1658,1659,1661,1662,1664,1666,1668,1669,1670,1671,1672,1673,1674,1675,1676,1677,1678,1679,1681,1682,1621,1638,1648,1649,1656,1657,1660,1663,1665,1667,1680,1683);
--

-- PICS-3099
insert into app_properties (property, value)
	values ('subscription.limit', '5');
insert into app_properties (property, value)
	values ('subscription.enable', 'true');
update email_subscription s set s.timePeriod = 'Event' 
where s.timePeriod not in ('Event','None') 
 and s.subscription in ('ContractorInvoices','InsuranceExpiration','AuditOpenRequirements','FinishPICSProcess','PICSSystemNotifications','ContractorFinished','ContractorDeactivation');
--