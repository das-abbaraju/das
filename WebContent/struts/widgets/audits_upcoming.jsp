<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<tr>
		<th>Contractor</th>
		<th>Type</th>
		<th>Created</th>
		</tr>
	</thead>
	<s:iterator value="upcoming">
		<tr>
			<td><a href="ContractorView.action?id=<s:property value="audit.contractorAccount.id"/>"><s:property value="audit.contractorAccount.name"/></a></td>
			<td><a href="Audit.action?auditID=<s:property value="audit.id"/>"><s:property value="audit.auditType.auditName"/></a></td>
			<td class="center"><s:date name="creationDate" format="M/d/yy" /></td>
		</tr>
	</s:iterator>
	<s:if test="upcoming.size == 0">
		<tr>
			<td colspan="3" class="center">No upcoming audits</td>
		</tr>
	</s:if>
</table>
