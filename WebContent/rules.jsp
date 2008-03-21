<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head><title>Rules</title></head>
<body>
<table>
<s:iterator value="rulesRows">
	<tr>
		<td><s:property value="sequence"/></td>
		<td><s:property value="tableName"/></td>
		<td><a href="RulesList.action?rowID=<s:property value="rowID"/>">Edit</a></td>
	</tr>
</s:iterator>
</table>
</body>
</html>
