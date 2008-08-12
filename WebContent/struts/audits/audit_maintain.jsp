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

<s:form action="ConAuditMaintain">
	<s:hidden name="auditID" />

<fieldset>
	<div class="formRow">
		<span class="labels">Audit Status: </span>
		<span class="fields">
			<span class="fields"><s:select name="conAudit.auditStatus" list="@com.picsauditing.jpa.entities.AuditStatus@values()"/></span>
		</span>
	</div>
	<div class="formRow">
		<span class="labels">Auditor: </span>
		<span class="fields"><s:select name="conAudit.auditor.id" list="auditorList" listKey="id" listValue="name"/></span>
	</div>

	<div class="formRow">
		<span class="labels">Audit Location: </span>
		<span class="fields"><s:textfield name="conAudit.auditLocation"/></span>
	</div>

	<div class="formRow">
		<span class="labels">Complete: </span>
		<span class="fields"><s:textfield name="conAudit.percentComplete" size="4"/>%</span>
		<span class="labels">Verified: </span>
		<span class="fields"><s:textfield name="conAudit.percentVerified" size="4"/>%</span>
	</div>
</fieldset>

<fieldset>
	<hr />

	<div class="formRow">
		<span class="labels"><strong>Dates are in this format: 01/31/08 12:03 AM</strong></span>
	</div>


	<div class="formRow">
		<span class="labels">Created Date: </span>
		<span class="fields"><s:textfield name="conAudit.createdDate" value="%{conAudit.createdDate && getText('dates', {conAudit.createdDate})}"/></span>
		<span class="labels">Expires Date: </span>
		<span class="fields"><s:textfield name="conAudit.expiresDate" value="%{conAudit.expiresDate && getText('dates', {conAudit.expiresDate})}"/></span>
	</div>
	<div class="formRow">
		<span class="labels">Assigned Date: </span>
		<span class="fields"><s:textfield name="conAudit.assignedDate" value="%{conAudit.assignedDate && getText('dates', {conAudit.assignedDate})}"/></span>
		<span class="labels">Scheduled Date: </span>
		<span class="fields"><s:textfield name="conAudit.scheduledDate" value="%{conAudit.scheduledDate && getText('dates', {conAudit.scheduledDate})}"/></span>
	</div>
	<div class="formRow">
		<span class="labels">Completed Date: </span>
		<span class="fields"><s:textfield name="conAudit.completedDate" value="%{conAudit.completedDate && getText('dates', {conAudit.completedDate})}"/></span>
		<span class="labels">Closed Date: </span>
		<span class="fields"><s:textfield name="conAudit.closedDate" value="%{conAudit.closedDate && getText('dates', {conAudit.closedDate})}"/></span>
	</div>
	<div class="formRow">
		<span class="labels">Contractor Confirm: </span>
		<span class="fields"><s:textfield name="conAudit.contractorConfirm" value="%{conAudit.contractorConfirm && getText('dates', {conAudit.contractorConfirm})}"/></span>
		<span class="labels">Auditor Confirm: </span>
		<span class="fields"><s:textfield name="conAudit.auditorConfirm" value="%{conAudit.auditorConfirm && getText('dates', {conAudit.auditorConfirm})}"/></span>
	</div>
</fieldset>

<div class="buttons">
	<button class="positive" name="button" value="Save" type="submit">Save</button>
</div>

</s:form>

</body>
</html>
