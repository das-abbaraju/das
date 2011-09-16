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
update email_subscription s set s.timePeriod = 'Monthly' where s.subscription = 'OQChanges';
update email_subscription s set s.timePeriod = 'Event' 
where s.timePeriod not in ('Event','None') 
 and s.subscription in ('ContractorInvoices','InsuranceExpiration','AuditOpenRequirements','FinishPICSProcess','PICSSystemNotifications','ContractorFinished','ContractorDeactivation','Webinar');
--

 -- PICS-3287
update pqfData pd
  join contractor_audit ca
    on ca.id = pd.auditID
  join contractor_audit ca2
    on ca2.conID = ca.conID
set pd.auditID = ca2.id, pd.questionID = (case pd.questionID when 7786 then 9186 when 7787 then 9188 when 7788 then 9187 end)
where (pd.questionID = 7786
        or pd.questionID = 7787
        or pd.questionID = 7788)
    and ca2.auditTypeID = 279;

 
-- PICS-3219
update email_template t set t.body = '<SubscriptionHeader>
Below are all the contractors whose flags have been forced. <br/>
#if($forcedflags.size() > 0)
 <h3 style="color: rgb(168, 77, 16)">Forced Flags ($forcedflags.size())</h3>
 <table style="border-collapse: collapse; border: 2px solid #003768; background: #f9f9f9;">
  <thead>
   <tr style="vertical-align: middle; font-size: 13px;font-weight: bold; background: #003768; color: #FFF;">
    <td style="border: 1px solid #e0e0e0; padding: 4px;">Contractor Name</td>
    #if($user.account.corporate)
     <td style="border: 1px solid #e0e0e0; padding: 4px;">Operator Name</td>
    #end
    <td style="border: 1px solid #e0e0e0; padding: 4px;">Flag</td>
    <td style="border: 1px solid #e0e0e0; padding: 4px;">Flag Issue</td>
    <td style="border: 1px solid #e0e0e0; padding: 4px;">Forced By</td>
    <td style="border: 1px solid #e0e0e0; padding: 4px;">Start Date</td>
    <td style="border: 1px solid #e0e0e0; padding: 4px;">End Date</td>
    <td style="border: 1px solid #e0e0e0; padding: 4px;">Notes</td>
   </tr>
  </thead>
  <tbody>
   #foreach( $flag in $forcedflags )
    <tr style="margin:0px">
     <td style="border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;"><a href="http://www.picsorganizer.com/ContractorView.action?id=${flag.get(\'id\')}">${flag.get(\'name\')}</a></td>
     #if($user.account.corporate)
      <td style="border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;"><a>${flag.get(\'opName\')}</a></td>
     #end
     <td style="border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;"><a href="http://www.picsorganizer.com/ContractorFlag.action?id=${flag.get(\'id\')}&opID=${flag.get(\'opId\')}"><img src="http://www.picsorganizer.com/images/icon_${flag.get(\'flag\').toLowerCase()}Flag.gif" width="10" height="12"> ${flag.get(\'flag\')}</a></td>
     <td style="border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;">$i18nCache.getText(${flag.get(\'fLabel\')},$user.locale)</td>
     <td style="border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;">${flag.get(\'forcedBy\')}</td>
     <td style="border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;">${pics_dateTool.format(\'MM/dd/yy\', ${d.get(\'forceBegin\')})}</td>
     <td style="border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;">${pics_dateTool.format(\'MM/dd/yy\', ${d.get(\'forceend\')})}</td>
     #if($flag.get(\'forcedById\') != '')
      <td style="border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;"><a href="http://www.picsorganizer.com/ContractorNotes.action?id=${flag.get(\'id\')}&filter.userID=${flag.get(\'forcedById\')}&filter.category=Flags&filter.keyword=Forced">Notes</a></td>
     #end
    </tr>
   #end
  </tbody>
 </table>	
#end
<TimeStampDisclaimer>
<SubscriptionFooter>' where t.id = 165;
--

update email_template t set t.translated = 1 where t.id in (86,62,51,107,63,60,65,61,71,130,168,45,48,50,10,70,63);