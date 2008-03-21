<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head><title>Rules</title></head>
<body>
<table>
<s:iterator value="rulesRows">
	<tr>
		<td><s:property value="rowID"/></td>
		<td><s:property value="tableName"/></td>
	</tr>
</s:iterator>
</table>
</body>
</html>