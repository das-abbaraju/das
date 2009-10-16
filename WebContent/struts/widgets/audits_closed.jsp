<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<tr>
		<th>Contractor</th>
		<th>Type</th>
		<th>Closed</th>
		</tr>
	</thead>
	<s:iterator value="recentlyClosed">
		<tr>
			<td><a href="ContractorView.action?id=<s:property value="contractorAccount.id"/>"><s:property value="contractorAccount.name"/></a></td>
			<td><a href="Audit.action?auditID=<s:property value="id"/>"><s:property value="auditType.auditName"/><s:if test="auditFor != null"> - <s:property value="auditFor"/></s:if></a></td>
			<td class="center"><s:date name="closedDate" format="M/d/yy" /></td>
		</tr>
	</s:iterator>
	<s:if test="recentlyClosed.size == 0">
		<tr>
			<td colspan="4" class="center">No currently schedule audits</td>
		</tr>
	</s:if>
</table>
