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
<s:include value="conHeader.jsp" />

<span class="message"><s:property value="message" /></span>
<table class="report">
	<thead>
		<tr>
			<th>Num</th>
			<th>Category</th>
			<s:if test="conAudit.auditStatus.name() == 'Pending'">
				<th colspan="2">Complete</th>
			</s:if>
			<s:if test="conAudit.auditStatus.name() == 'Submitted'">
				<th colspan="2">Complete</th>
				<th colspan="2">Verified</th>
			</s:if>
		</tr>
	</thead>
	<s:iterator value="categories" status="rowStatus">
		<tr>
			<td class="right"><s:property value="category.number" /></td>
			
			<td><a href="<s:property value="catUrl" />?auditID=<s:property value="auditID" />&catID=<s:property value="category.id" />"><s:property value="category.category" /></a></td>
			
			<s:if test="conAudit.auditStatus.name() == 'Pending'">
				<td class="right"><s:property value="percentCompleted" />%</td>
				<td><s:if test="percentCompleted == 100"><img src="images/okCheck.gif" width="19" height="15" /></s:if></td>
			</s:if>
			<s:if test="conAudit.auditStatus.name() == 'Submitted'">
				<td class="right"><s:property value="percentCompleted" />%</td>
				<td><s:if test="percentCompleted == 100"><img src="images/okCheck.gif" width="19" height="15" /></s:if></td>
				<td class="right"><s:property value="percentVerified" />%</td>
				<td><s:if test="percentVerified == 100"><img src="images/okCheck.gif" width="19" height="15" /></s:if></td>
			</s:if>
		</tr>
	</s:iterator>
</table>
</body>
</html>
