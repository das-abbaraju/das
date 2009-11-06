<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="conAudit.auditType.auditName" /> for
<s:property value="conAudit.contractor.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=20091105" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=20091105" />
</head>
<body>
<s:include value="conHeader.jsp" />

<table class="report">
	<thead>
		<tr>
			<th>Num</th>
			<th>Category</th>
			<th>% Complete</th>
		</tr>
	</thead>
	<s:iterator value="ncmsCategories" status="rowStatus">
		<tr<s:if test="status == 'N/A'"> class="notapp"</s:if>>
			<td class="right"><s:property value="#rowStatus.index + 1" /></td>
			<td><s:property value="name" /></td>
			<td class="center"><s:property value="status" /></td>
		</tr>
	</s:iterator>
</table>
</body>
</html>
