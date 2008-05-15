<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="conAudit.auditType.auditName" /> for
<s:property value="conAudit.contractor.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
</head>
<body>
<s:include value="audit_header.jsp">
	<s:param name="conAudit" value="conAudit" />
	<s:param name="id" value="conAudit.contractorAccount.id" />
	<s:param name="auditID" value="auditID" />
</s:include>

<table class="report">
	<thead>
		<tr>
			<th>Num</th>
			<th>Category</th>
			<th>% Complete</th>
		</tr>
	</thead>
	<s:iterator value="ncmsCategories" status="rowStatus">
		<tr<s:if test="status == 'N/A'"> class="na"</s:if>>
			<td class="right"><s:property value="#rowStatus.index + 1" /></td>
			<td><s:property value="name" /></td>
			<td class="right"><s:property value="status" /></td>
		</tr>
	</s:iterator>
</table>
</body>
</html>
