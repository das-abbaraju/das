update widget set chartType = 'Pie2D' where widgetID = 23;

-- PICS-2801
update flag_criteria set category="Insurance AMB Class" where label like '%Class%';
update flag_criteria set category="Insurance AMB Rating" where label like '%Rating%';
update flag_criteria set optionCode='ExcessEachOccurrence' where category="Insurance Criteria" and description like '%plus Excess Each Occurrence%';
update flag_criteria set optionCode='ExcessAggregate' where category="Insurance Criteria" and description like '%plus Excess Aggregate%';
--

-- PICS-3219
update app_translation t set t.msgValue = '<SubscriptionHeader>
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
<SubscriptionFooter>', t.updatedBy = 20952, t.updateDate = now() where t.msgKey = 'EmailTemplate.165.translatedBody';  
--