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
			<td><a href="pqf_view.jsp?auditID=<s:property value="id"/>"><s:property value="auditType.auditName"/></a></td>
			<td class="center"><s:date name="closedDate" format="M/d/yy" /></td>
		</tr>
	</s:iterator>
</table>
