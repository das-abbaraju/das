<%@ taglib prefix="s" uri="/struts-tags" %>
<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<html>
<head>
<title>Edit Operator/Audit Permissions</title>
</head>
<body>
<h1>Edit Operator/Audit Permissions</h1>
<table border="1">
<tr class="blueMain" align="center">
	<td></td>
	<td>No</td>
	<td>Low</td>
	<td>Med</td>
	<td>High</td>
</tr>
<s:iterator value="data" status="test">
<tr class="blueMain">
	<td><s:property value="minRiskLevel"/></td>
	<td><s:property value="" /></td>
	<td></td>
	<td></td>
	<td></td>
</tr>
</s:iterator>
</table>
</body>
</html>
