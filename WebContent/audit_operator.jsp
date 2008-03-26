<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp"%>
<html>
<head>
<title>Edit Operator/Audit Permissions</title>
</head>
<body>
<h1>Edit Operator/Audit Permissions</h1>
<a href="AuditOperator.action?auditId=1">PQF</a>
<table border="1">
	<tr class="blueMain" align="center">
		<td></td>
		<td>No</td>
		<td>Low</td>
		<td>Med</td>
		<td>High</td>
	</tr>
	<s:iterator value="data">
		<tr class="blueMain">
			<td><s:property value="operatorID" /><s:property value="auditID" /></td>
			<td><s:property value="operatorName" /><s:property value="auditName" /></td>
			<td><s:property value="riskLevel" /></td>
		</tr>
	</s:iterator>
</table>
</body>
</html>
