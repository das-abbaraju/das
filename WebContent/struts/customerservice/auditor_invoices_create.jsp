<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Create Auditor Invoices</title>
<link rel="stylesheet" href="css/reports.css?v=20091105" />
</head>
<body>
<h1>Create Auditor Invoices</h1>

<s:include value="../actionMessages.jsp" />

<s:form>
	<s:hidden name="listCount" />
	<s:submit name="button" value="Create Payment Batch"></s:submit>
</s:form>

<table class="report">
	<tr>
		<th>Auditor</th>
		<th>Contractor</th>
		<th>Audit</th>
		<th>Type</th>
		<th>Date</th>
	</tr>
	<s:iterator value="list">
		<tr>
			<td><s:property value="auditor.name" /></td>
			<td><a href="ContractorView.action?id=<s:property value="contractorAccount.id"/>" target="_BLANK"><s:property
				value="contractorAccount.name" /></a></td>
			<td><a href="Audit.action?auditID=<s:property value="id"/>" target="_BLANK"><s:property value="id" /></a></td>
			<td><s:property value="auditType.auditName" /></td>
			<td><s:date name="completedDate" format="MMM d" /></td>
		</tr>
	</s:iterator>
</table>

</body>
</html>