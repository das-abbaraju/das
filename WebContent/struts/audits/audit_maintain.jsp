<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="conAudit.auditType.auditName" /> for
<s:property value="conAudit.contractorAccount.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" />

<SCRIPT LANGUAGE="JavaScript" SRC="js/CalendarPopup.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
<SCRIPT LANGUAGE="JavaScript" ID="js1">var cal1 = new CalendarPopup();</SCRIPT>
<SCRIPT LANGUAGE="JavaScript" SRC="js/validateForms.js"></SCRIPT>
<script type="text/javascript" src="js/prototype.js"></script>
</head>
<body>
<s:include value="../contractors/conHeader.jsp" />

<s:if test="message.length() > 0">
	<div id="info"><s:property value="message"/></div>
</s:if>

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
		<span class="labels">Can Delete: </span>
		<span class="fields"><s:checkbox name="conAudit.canDelete"/></span>
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
		<span class="labels">Created Date: </span>
		<span class="fields"><s:textfield name="conAudit.createdDate"/></span>
		<span class="labels">Expires Date: </span>
		<span class="fields"><s:textfield name="conAudit.expiresDate"/></span>
	</div>
	<div class="formRow">
		<span class="labels">Assigned Date: </span>
		<span class="fields"><s:textfield name="conAudit.assignedDate"/></span>
		<span class="labels">Scheduled Date: </span>
		<span class="fields"><s:textfield name="conAudit.scheduledDate"/></span>
	</div>
	<div class="formRow">
		<span class="labels">Completed Date: </span>
		<span class="fields"><s:textfield name="conAudit.completedDate"/></span>
		<span class="labels">Closed Date: </span>
		<span class="fields"><s:textfield name="conAudit.closedDate"/></span>
	</div>
</fieldset>

<div class="buttons">
	<s:hidden name="button" value="save"></s:hidden>
	<a class="positive" onclick="$('ConAuditMaintain').submit(); return false;" href="#">Save</a>
</div>

</s:form>

</body>
</html>
