<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="conAudit.auditType.auditName" /> for
<s:property value="conAudit.contractorAccount.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" />
<script src="js/prototype.js" type="text/javascript"></script>
<script type="text/javascript" src="js/scriptaculous/scriptaculous.js?load=effects"></script>
<script type="text/javascript">
	function resubmitPqf(verifyCheckBox) {
		var data = verifyCheckBox.checked;
		if(data) {
			$('submit').disabled = false;	
		}
		else
			$('submit').disabled = true;		
	}
</script>
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
		<s:if test="conAudit.auditStatus.pending || conAudit.auditStatus.incomplete || conAudit.auditType.pqf">
			<th colspan="2">Complete</th>
		</s:if>
		<s:if test="conAudit.auditStatus.submitted && conAudit.auditType.hasRequirements">
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
		<s:if test="appliesB">
			<tr>
			<s:if test="!conAudit.auditType.pqf">
				<td class="right"><a name="<s:property value="id" />"><s:property value="category.number" /></a></td>
				</s:if>
				<td><a href="AuditCat.action?auditID=<s:property value="auditID" />&catDataID=<s:property value="id" />"><s:property value="category.category" /></a></td>
			<s:if test="conAudit.auditStatus.pending || conAudit.auditStatus.incomplete || conAudit.auditType.pqf">
				<td class="right"><s:property value="percentCompleted" />%</td>
				<td><s:if test="percentCompleted == 100"><img src="images/okCheck.gif" width="19" height="15" /></s:if></td>
			</s:if>
			<s:if test="conAudit.auditStatus.submitted && conAudit.auditType.hasRequirements">
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
		<s:if test="!appliesB && permissions.picsEmployee">
			<tr class="notapp">
			<s:if test="!conAudit.auditType.pqf">
				<td class="right"><a name="<s:property value="id" />"><s:property value="category.number" /></a></td>
				</s:if>
				<td><a href="AuditCat.action?auditID=<s:property value="auditID" />&catDataID=<s:property value="id" />"><s:property value="category.category" /></a></td>
				<s:if test="conAudit.auditStatus.pending || conAudit.auditType.pqf">
					<td class="center" colspan="2">N/A</td>
				</s:if>
				<s:if test="conAudit.auditStatus.submitted && conAudit.auditType.hasRequirements">
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
<s:if test="!@com.picsauditing.util.Strings@isEmpty(auditorNotes)">
	<div id="info">
		<b>Auditor Notes:</b> <s:property value="auditorNotes"/>
	</div>
</s:if>
<s:if test="conAudit.auditType.pqf">
	<div id="info">
		The OSHA and EMR categories have been moved to the Annual Update.
	</div>
</s:if>
<br clear="both"/>
</body>
</html>
