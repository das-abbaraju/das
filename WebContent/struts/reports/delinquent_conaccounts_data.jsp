<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<div class="helpOnRight">
These contractors will be deactivated from the PICS system in the next few days. If you expect to do 
work with any of these contractors, please encourage them to renew their 
membership by contacting PICS.
</div>

<table class="report" style="clear : none;">
	<thead>
	<tr>
		<td colspan="2">Contractor Name</td>
		<td>Due Date</td>
		<td>Days Left</td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td>
				<a href="ContractorView.action?id=<s:property value="[0].get('id')"/>">
				<s:property value="[0].get('name')" /></a>
			</td>
			<td class="center"><s:date name="[0].get('dueDate')" format="M/d/yy" /></td>
			<td class="center"><s:property value="[0].get('DaysLeft')" /></td>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>