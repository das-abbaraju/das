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

<div>
	<h1>
	<s:if test="conAudit">
		Edit Audit: <s:property value="conAudit.contractorAccount.name"/> / <s:property value="conAudit.auditType.auditName"/>
	</s:if>
	<s:else>
		Create Audit
	</s:else>
	</h1>
</div>

<s:property value="message == 'Success' ? 'Audit Saved Successfully' : message"/>

<s:form action="AuditSave">

<s:if test="conAudit">
	<s:hidden name="audit.id" value="%{conAudit.id}"/>
</s:if>			


<fieldset>
	<div class="formRow">
		<span class="labels" style="width: 400px;">Contractor: </span>
		<span class="fields">
			<s:if test="conAudit">
				<s:property value="conAudit.contractorAccount.name"/>
			</s:if>
			<s:else>
				<s:select name="contractorID" value="conAudit.contractorAccount.id" list="allContractors" listKey="id" listValue="name" headerKey="0" headerValue="-- Select a Contractor --"/>
			</s:else>			
		</span>
	</div>
	<div class="formRow">
		<span class="labels">Audit Type: </span>
		<s:if test="conAudit">
			<span class="fields"><s:property value="conAudit.auditType.auditName"/></span>
		</s:if>			
		<s:else>
			<s:select name="conAudit.auditType.auditTypeID" list="allAuditTypes" listKey="auditTypeID" listValue="auditName" headerKey="0" headerValue="-- Select an audit type --"/>
		</s:else>			

		<span class="labels">Audit Status: </span>
		<span class="fields">
			<span class="fields"><s:select name="conAudit.auditStatus" list="@com.picsauditing.jpa.entities.AuditStatus@getValuesWithDefault()"/></span>
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
		<span class="fields"><s:textfield name="conAudit.canDelete"/>(true or false)</span>
	</div>

	<div class="formRow">
		<span class="labels">Percent Complete: </span>
		<span class="fields"><s:textfield name="conAudit.percentComplete"/>%</span>
		<span class="labels">Percent Verified: </span>
		<span class="fields"><s:textfield name="conAudit.percentVerified"/>%</span>
	</div>


	
</fieldset>


<fieldset>
	<h2>Dates</h2>
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
	<s:submit value="Save" />
</div>

</s:form>

</body>
</html>
