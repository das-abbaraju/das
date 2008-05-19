<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="conAudit.auditType.auditName" /> for
<s:property value="conAudit.contractor.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<style>
table.report tr.na {
	color: #999;
	//display: none;
}

table.report tr.na a {
	font-style: italic;
	color: #999;
}
</style>
</head>
<body>
<s:include value="conHeader.jsp" />

<span class="message"><s:property value="message" /></span>
<s:if test="conAudit.auditType.hasRequirements">
<s:form action="pqf_verify.jsp">
<s:hidden name="auditID" />
<s:submit value="Generate Requirements" />
</s:form>
</s:if>
<table class="report">
	<thead>
		<tr>
			<th>Num</th>
			<th>Category</th>
			<th colspan="2">Complete</th>
			<s:if test="conAudit.auditStatus.name() == 'Pending'">
			</s:if>
			<s:if test="conAudit.auditStatus.name() == 'Submitted'">
				<th colspan="2">Verified</th>
			</s:if>
		</tr>
	</thead>
	<s:iterator value="categories" status="rowStatus">
		<s:if test="appliesB">
			<tr>
				<td class="right"><s:property value="category.number" /></td>
				<td><a href="<s:property value="catUrl" />?auditID=<s:property value="auditID" />&catID=<s:property value="category.id" />"><s:property value="category.category" /></a></td>
				<td class="right"><s:property value="percentCompleted" />%</td>
				<td><s:if test="percentCompleted == 100"><img src="images/okCheck.gif" width="19" height="15" /></s:if></td>
				<s:if test="conAudit.auditStatus.name() == 'Submitted'">
					<td class="right"><s:property value="percentVerified" />%</td>
					<td><s:if test="percentVerified == 100"><img src="images/okCheck.gif" width="19" height="15" /></s:if></td>
				</s:if>
			</tr>
		</s:if>
		<s:else>
			<tr class="na">
				<td class="right"><s:property value="category.number" /></td>
				<td><a href="<s:property value="catUrl" />?auditID=<s:property value="auditID" />&catID=<s:property value="category.id" />"><s:property value="category.category" /></a></td>
				<td class="center" colspan="2">N/A</td>
				<s:if test="conAudit.auditStatus.name() == 'Submitted'">
					<td colspan="2"></td>
				</s:if>
			</tr>
		</s:else>
	</s:iterator>
</table>

</body>
</html>
