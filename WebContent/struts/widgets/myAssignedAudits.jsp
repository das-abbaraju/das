<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
	<tr>
	    <td>Contractor</td>
	    <td>Type</td>
	    <td>Status</td>
	    <td>Assigned</td>
	</tr>
	</thead>
	<s:iterator value="newlyAssigned">
		<tr>
			<td><a href="ContractorView.action?id=<s:property value="contractorAccount.id"/>"><s:property value="contractorAccount.name"/></a></td>
			<td><a href="Audit.action?auditID=<s:property value="id"/>"><nobr><s:property value="auditType.auditName"/></nobr></a></td>
			<td><s:property value="auditStatus"/></td>
			<td class="center"><s:date name="assignedDate" format="M/d/yy" /></td>
		</tr>
	</s:iterator>
</table>
