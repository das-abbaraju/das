<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="conAudit.auditType.name" /> for
<s:property value="conAudit.contractorAccount.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
</head>
<body>
<s:include value="../contractors/conHeader.jsp" />

<pics:permission perm="AuditEdit" type="Edit">
	<s:form action="ConAuditMaintain" cssStyle="width: 400px">
		<s:hidden name="auditID" />
	<fieldset class="form">
		<h2 class="formLegend">System Edit</h2>
		<ol>
			<li><label>Safety Professional:</label>
				<s:select name="conAudit.auditor.id" list="auditorList" listKey="id" listValue="name"/></li>
			<li><label>Audit Location:</label>
				<s:textfield name="conAudit.auditLocation"/></li>
			<li><label>Audit For:</label>
				<s:textfield name="conAudit.auditFor"/></li>
			<li><label>Manually Added:</label>
				<s:checkbox name="conAudit.manuallyAdded"></s:checkbox></li>
		</ol>
	</fieldset>
	<fieldset class="form">
		<h2 class="formLegend">Audit Dates</h2>
		<ol>
			<li><label>Created Date:</label>
				<s:textfield cssClass="sysEditDate" name="conAudit.creationDate" value="%{conAudit.creationDate && getText('dates', {conAudit.creationDate})}"/></li>
			<li><label>Expires Date:</label>
				<s:textfield cssClass="sysEditDate" name="conAudit.expiresDate" value="%{conAudit.expiresDate && getText('dates', {conAudit.expiresDate})}"/></li>
			<li><label>Effective Date:</label>
				<s:textfield cssClass="sysEditDate" name="conAudit.effectiveDate" value="%{conAudit.effectiveDate && getText('dates', {conAudit.effectiveDate})}"/></li>
			<li><label>Assigned Date:</label>
				<s:textfield cssClass="sysEditDate" name="conAudit.assignedDate" value="%{conAudit.assignedDate && getText('dates', {conAudit.assignedDate})}"/></li>
			<li><label>Scheduled Date:</label>
				<s:textfield cssClass="sysEditDate" name="conAudit.scheduledDate" value="%{conAudit.scheduledDate && getText('dates', {conAudit.scheduledDate})}"/></li>
			<li><label>Contractor Confirmation:</label>
				<s:textfield cssClass="sysEditDate" name="conAudit.contractorConfirm" value="%{conAudit.contractorConfirm && getText('dates', {conAudit.contractorConfirm})}"/></li>
			<li><label>Safety Professional Confirmation:</label>
				<s:textfield cssClass="sysEditDate" name="conAudit.auditorConfirm" value="%{conAudit.auditorConfirm && getText('dates', {conAudit.auditorConfirm})}"/></li>
			<li style="font-style: italic;">* Dates are in this format: 01/31/08 12:03 AM</li>
		</ol>
	</fieldset>
	<fieldset class="form submit">
		<div>
			<input type="submit" class="picsbutton positive" name="button" value="Save"/>
			<pics:permission perm="AuditEdit" type="Delete">
				<input type="submit" class="picsbutton negative" name="button" value="Delete" onclick="return confirm('Are you sure you want to permanently remove this audit?');"/>
			</pics:permission>
		</div>
	</fieldset>
	</s:form>
</pics:permission>

</body>
</html>
