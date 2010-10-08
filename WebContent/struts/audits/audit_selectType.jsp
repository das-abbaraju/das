<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Audit Management</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
</head>
<body>
<h1>Audit Management</h1>
<table class="report">
<thead>
<tr>
	<th>Order</th>
	<th>Class</th>
	<th>Audit Type</th>
</tr>
</thead>
<s:iterator value="auditTypes">
<tr>
	<td class="center"><s:property value="displayOrder"/></td>
	<td><s:property value="classType"/></td>
	<td><a href="ManageAuditType.action?id=<s:property value="id"/>"><s:property value="auditName"/></a></td>
</tr>
</s:iterator>
<tr><td class="center" colspan="3"><a class="add" href="?button=Add New">Add New</a></td></tr>
</table>
</body>
</html>
