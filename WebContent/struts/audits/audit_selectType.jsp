<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="../../includes/main.jsp"%>
<html>
<head>
<title>Audit Management</title>
</head>
<body>
<h1>Audit Management</h1>
<div>
<ul>
	<s:iterator value="auditTypes">
	<li><a href="ManageAuditType.action?id=<s:property value="auditTypeID"/>"><s:property value="auditName"/></a>
	&nbsp;&nbsp;<a class="blueSmall" href="pqf_editSubCategories.jsp?auditTypeID=<s:property value="auditTypeID"/>">old</a></li>
	</s:iterator>
	<li><a href="?button=Add New">Add New</a></li>
</ul>
</div>
</body>
</html>
