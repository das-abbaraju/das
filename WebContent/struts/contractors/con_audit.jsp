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
</s:include>

<span class="message"><s:property value="message" /></span>
<table class="report">
	<thead>
		<tr>
			<th>Num
			</td>
			<th>Category
			</td>
			<th>Complete</th>
			<th></th>
		</tr>
	</thead>
	<s:iterator value="categories" status="rowStatus">
		<tr>
			<td class="right"><s:property value="category.number" /></td>
			<td><a href="pqf_view.jsp?auditID=<s:property value="auditID" />&catID=<s:property value="category.id" />"><s:property value="category.category" /></a></td>
			<td class="right"><s:property value="percentCompleted" />%</td>
			<td><s:if test="percentCompleted == 100"><img src="images/okCheck.gif" width="19" height="15" /></s:if></td>
		</tr>
	</s:iterator>
</table>
</body>
</html>
