<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<tr>
		<th>Contractor</th>
		<th>Type</th>
		<th>Scheduled</th>
		<th>Location</th>
		</tr>
	</thead>
	<s:iterator value="upcoming">
		<tr>
			<td><a href="ContractorView.action?id=<s:property value="contractorAccount.id"/>"><s:property value="contractorAccount.name"/></a></td>
			<td><a href="Audit.action?auditID=<s:property value="id"/>"><s:property value="auditType.auditName"/></a></td>
			<td class="center"><s:date name="scheduledDate" format="M/d/yy" /></td>
			<td><s:property value="auditLocation" /></td>
		</tr>
	</s:iterator>
	<s:if test="upcoming.size == 0">
		<tr>
			<td colspan="4" class="center">No currently schedule audits</td>
		</tr>
	</s:if>
</table>
