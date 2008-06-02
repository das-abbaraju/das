<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="conAudit.auditType.auditName" /> for
<s:property value="conAudit.contractorAccount.name" /></title>
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
<s:form>
	<s:hidden name="auditID" />
	<s:if test="canSubmit">
		<s:hidden name="auditStatus" value="Submitted" />
		<s:submit value="%{'Submit '.concat(conAudit.auditType.auditName)}" />
	</s:if>
	<s:if test="canClose">
		<s:hidden name="auditStatus" value="Active" />
		<s:submit value="%{'Close '.concat(conAudit.auditType.auditName)}" />
	</s:if>
</s:form>

<table class="report">
	<thead>
		<tr>
			<th>Num</th>
			<th>Category</th>
		<s:if test="conAudit.auditStatus.name() == 'Pending' || conAudit.auditType.pqf">
			<th colspan="2">Complete</th>
		</s:if>
		<s:if test="conAudit.auditStatus.name() == 'Submitted' && conAudit.auditType.hasRequirements">
			<th colspan="2">Requirements</th>
		</s:if>
		</tr>
	</thead>
	<s:iterator value="categories" status="rowStatus">
		<s:if test="appliesB">
			<tr>
				<td class="right"><s:property value="category.number" /></td>
				<td><a href="<s:property value="catUrl" />?auditID=<s:property value="auditID" />&catID=<s:property value="category.id" />&id=<s:property value="conAudit.contractorAccount.id" />"><s:property value="category.category" /></a></td>
			<s:if test="conAudit.auditStatus.name() == 'Pending' || conAudit.auditType.pqf">
				<td class="right"><s:property value="percentCompleted" />%</td>
				<td><s:if test="percentCompleted == 100"><img src="images/okCheck.gif" width="19" height="15" /></s:if></td>
			</s:if>
			<s:if test="conAudit.auditStatus.name() == 'Submitted' && conAudit.auditType.hasRequirements">
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
				<s:if test="conAudit.auditStatus.name() == 'Submitted' && conAudit.auditType.hasRequirements">
					<td colspan="2"></td>
				</s:if>
			</tr>
		</s:else>
	</s:iterator>
</table>

</body>
</html>
