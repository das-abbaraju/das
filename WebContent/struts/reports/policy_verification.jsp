<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>PQF Verification</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>PQF Verification</h1>
<s:include value="filters.jsp" />
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

<table class="report">
	<thead>
	<tr>
		<td></td>
		<td><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></td>
		<td>Policy</td>
		<td>Status</td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="get('name')"/></a></td>
			<td><a href="Audit.action?auditID=<s:property value="get('auditID')"/>"><s:property value="get('auditName')"/></a></td>
			<td><s:property value="get('auditStatus')"/></td>
		
		</tr>
	</s:iterator>	
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</body>
</html>