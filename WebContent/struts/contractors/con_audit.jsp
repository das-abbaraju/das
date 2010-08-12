<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="conAudit.auditType.auditName" /> for
<s:property value="conAudit.contractorAccount.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
</head>
<body>

<s:include value="../audits/audit_catHeader.jsp"/>

<table class="report" style="clear: none;">
	<thead>
		<tr>
		<s:if test="!conAudit.auditType.pqf">
			<th>Num</th>
		</s:if>
			<th>Category</th>
		<s:if test="conAudit.auditType.pqf">
			<th colspan="2">Complete</th>
		</s:if>
		<s:if test="conAudit.auditType.hasRequirements">
			<th colspan="2">Requirements</th>
		</s:if>
		<s:if test="canApply">
			<th>Apply</th>
		</s:if>
		<s:if test="conAudit.auditType.id == 17">
			<th>Score</th>
		</s:if>
		</tr>
	</thead>
	<s:iterator value="categories" status="rowStatus">
		<s:if test="applies">
			<tr>
			<s:if test="!conAudit.auditType.pqf">
				<td class="right"><a name="<s:property value="id" />"><s:property value="category.number" /></a></td>
				</s:if>
				<td><a href="AuditCat.action?auditID=<s:property value="auditID" />&catDataID=<s:property value="id" />"><s:property value="category.name" /></a></td>
			<s:if test="conAudit.auditType.pqf">
				<td class="right"><s:property value="percentCompleted" />%</td>
				<td><s:if test="percentCompleted == 100"><img src="images/okCheck.gif" width="19" height="15" /></s:if></td>
			</s:if>
			<s:if test="conAudit.auditType.hasRequirements">
				<td class="right"><s:property value="percentVerified" />%</td>
				<td><s:if test="percentVerified == 100"><img src="images/okCheck.gif" width="19" height="15" /></s:if></td>
			</s:if>
			<s:if test="canApply">
				<td><s:form method="POST"><s:hidden name="auditID" value="%{auditID}" /><s:hidden name="removeCategoryID" value="%{id}" /><s:submit value="Remove"></s:submit></s:form></td>
			</s:if>
			<s:if test="conAudit.auditType.id == 17">
				<th><s:property value="printableScore"/></th>
			</s:if>
			</tr>
		</s:if>
	</s:iterator>
	<s:iterator value="categories" status="rowStatus">
		<s:if test="!applies && permissions.picsEmployee">
			<tr>
			<s:if test="!conAudit.auditType.pqf">
				<td class="right"><a name="<s:property value="id" />"><s:property value="category.number" /></a></td>
				</s:if>
				<td><a class="inactive" href="AuditCat.action?auditID=<s:property value="auditID" />&catDataID=<s:property value="id" />"><s:property value="category.name" /></a></td>
				<s:if test="conAudit.auditType.pqf">
					<td class="center" colspan="2">N/A</td>
				</s:if>
				<s:if test="conAudit.auditType.hasRequirements">
					<td colspan="2"></td>
				</s:if>
				<s:if test="canApply">
					<td>
					<s:form method="POST"><s:hidden name="auditID" value="%{auditID}" /><s:hidden name="applyCategoryID" value="%{id}"></s:hidden><s:submit value="Add"></s:submit></s:form>
					</td>
				</s:if>
			</tr>
		</s:if>
	</s:iterator>
</table>

<!-- 
<ol>
<s:iterator value="auditCategories" id="category">
	<li><s:include value="con_audit_cat.jsp"/></li>
</s:iterator>
</ol>
 -->

<s:if test="!@com.picsauditing.util.Strings@isEmpty(auditorNotes)">
	<div class="info">
		<b>Safety Professional Notes:</b> <s:property value="auditorNotes"/>
	</div>
</s:if>
<s:if test="conAudit.auditType.pqf">
	<s:if test="permissions.operatorCorporate && conAudit.auditStatus.active && conAudit.percentComplete < 100">
		<div class="info">
 			This PQF was Completed and Active as of <s:date name="conAudit.completedDate" format="MMM d, yyyy" />. 
 			Some sections have been added since this date and will be addressed in January.
		</div>
	</s:if>
</s:if>

<br clear="all"/>
</body>
</html>
