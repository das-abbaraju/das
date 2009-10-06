<%@ taglib prefix="s" uri="/struts-tags"%>
<h3><s:property value="paidDate"/> - <s:property value="auditList.get(0).auditor.name"/> - <s:property value="auditList.size()"/> Audits</h3>
<table class="report">
	<thead>
		<tr>
			<td>Audit ID</td>
			<td>Audit Type</td>
			<td>Contractor</td>
			<td>Completed Date</td>
		</tr>
	</thead>
	<s:iterator value="auditList">
	<tr>
		<td><a href="Audit.action?auditID=<s:property value="id"/>"><s:property value="id"/></a></td>
		<td><s:property value="auditType.auditName"/></td>
		<td><a href="ContractorView.action?id=<s:property value="contractorAccount.id"/>"><s:property value="contractorAccount.name"/></a></td>
		<td><s:date name="completedDate" format="MM/dd/yyyy hh:mm a"/></td>
	</tr>
	</s:iterator>
</table>