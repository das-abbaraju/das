<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="conAudit.auditType.auditName" /> for
<s:property value="conAudit.contractorAccount.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" />

<SCRIPT LANGUAGE="JavaScript" SRC="js/validateForms.js"></SCRIPT>
<script type="text/javascript" src="js/prototype.js"></script>
</head>
<body>
<s:include value="../contractors/conHeader.jsp" />

<s:form action="ConAuditMaintain" cssStyle="width: 400px">
	<s:hidden name="auditID" />
<fieldset>
	<legend><span>System Edit</span></legend>
	<ol>
		<li><label>Audit Status:</label>
			<s:select name="conAudit.auditStatus" list="@com.picsauditing.jpa.entities.AuditStatus@values()"/></li>
		<li><label>Auditor:</label>
			<s:select name="conAudit.auditor.id" list="auditorList" listKey="id" listValue="name"/></li>
		<li><label>Audit Location:</label>
			<s:textfield name="conAudit.auditLocation"/></li>
		<li><label>Complete:</label>
			<s:textfield name="conAudit.percentComplete" size="4"/>%</li>
		<li><label>Verified:</label>
			<s:textfield name="conAudit.percentVerified" size="4"/>%</li>
	</ol>
</fieldset>
<fieldset>
	<legend><span>Audit Dates</span></legend>
	<ol>
		<li><label>Created Date:</label>
			<s:textfield name="conAudit.createdDate" value="%{conAudit.createdDate && getText('dates', {conAudit.createdDate})}"/></li>
		<li><label>Expires Date:</label>
			<s:textfield name="conAudit.expiresDate" value="%{conAudit.expiresDate && getText('dates', {conAudit.expiresDate})}"/></li>
		<li><label>Assigned Date:</label>
			<s:textfield name="conAudit.assignedDate" value="%{conAudit.assignedDate && getText('dates', {conAudit.assignedDate})}"/></li>
		<li><label>Scheduled Date:</label>
			<s:textfield name="conAudit.scheduledDate" value="%{conAudit.scheduledDate && getText('dates', {conAudit.scheduledDate})}"/></li>
		<li><label>Completed Date:</label>
			<s:textfield name="conAudit.completedDate" value="%{conAudit.completedDate && getText('dates', {conAudit.completedDate})}"/></li>
		<li><label>Closed Date:</label>
			<s:textfield name="conAudit.closedDate" value="%{conAudit.closedDate && getText('dates', {conAudit.closedDate})}"/></li>
		<li><label>Contractor Confirmation:</label>
			<s:textfield name="conAudit.contractorConfirm" value="%{conAudit.contractorConfirm && getText('dates', {conAudit.contractorConfirm})}"/></li>
		<li><label>Auditor Confirmation:</label>
			<s:textfield name="conAudit.auditorConfirm" value="%{conAudit.auditorConfirm && getText('dates', {conAudit.auditorConfirm})}"/></li>
		<li style="font-style: italic;">* Dates are in this format: 01/31/08 12:03 AM</li>
	</ol>
</fieldset>
<fieldset class="submit">
	<div class="buttons">
		<button class="positive" name="button" value="Save" type="submit">Save</button>
		<pics:permission perm="AuditEdit" type="Delete">
			<button name="button" value="Delete" type="submit">Delete</button>
		</pics:permission>
	</div>
</fieldset>
</s:form>

</body>
</html>
