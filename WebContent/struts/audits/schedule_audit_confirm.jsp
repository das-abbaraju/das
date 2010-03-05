<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" errorPage="../../exception_handler.jsp"%>
<html>
<head>
<title>Schedule Audit</title>
<meta name="help" content="Scheduling_Audits">
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<style type="text/css">
#auditHeader,#auditHeaderNav {
	display: none;
}
</style>
</head>
<body>
<s:include value="../contractors/conHeader.jsp" />

<div class="alert">Your audit is NOT scheduled yet. You must confirm the details below before your appointment
will be made.</div>
<s:form>
	<s:hidden name="auditID" />
	<s:hidden name="availabilitySelectedID" />
	<s:hidden name="button" value="confirm"/>
	<fieldset class="form"><legend><span>Audit Confirmation</span></legend>
	<ol>
		<li>Please confirm the information below:</li>
		<li><label>Audit Date:</label> <s:property
			value="formatDate(availabilitySelected.startDate, 'EEEE, MMM d, yyyy')" /></li>
		<li><label>Audit Time:</label> <s:property value="formatDate(availabilitySelected.startDate, 'h:mm a z')" /></li>

		<s:if test="conAudit.conductedOnsite">
			<li><label>Location:</label> <s:property value="conAudit.fullAddress" /> <a
				href="ScheduleAudit.action?auditID=<s:property value="conAudit.id" />">Change</a></li>
		</s:if>
		<s:else>
			<li><label>Location:</label> Web &nbsp;&nbsp;&nbsp;&nbsp;<a
				href="http://help.picsauditing.com/wiki/Office_Audit" class="help" style="font-size: 10px" target="_BLANK">What
			is a Web Audit?</a></li>
			<li><label>Video Camera:</label> <s:radio name="conAudit.needsCamera" theme="pics"
				list="#{false: 'I have my own webcam that I can use for this audit', true: 'Please mail me a webcam for my computer. Use this address: ' + conAudit.fullAddress}" />
				<a class="edit" href="ScheduleAudit.action?auditID=<s:property value="conAudit.id" />">Change Address</a>
			</li>
		</s:else>

		<li><label>Primary Contact:</label> <s:property value="conAudit.contractorContact" /></li>
		<li><label>Email:</label> <s:property value="conAudit.phone2" /></li>
		<li><label>Phone:</label> <s:property value="conAudit.phone" /></li>
	</ol>
	</fieldset>
	<fieldset class="form submit">
	<div>
	<ol>
		<li><s:checkbox name="confirmed" />By checking this box, I understand that if I need to reschedule this audit, <br />
		I must do so before <s:property value="formatDate(lastCancellationTime)" /> or I will
		be subject to a $150 rescheduling fee.</li>
	</ol>
	<button id="confirmButton" class="picsbutton positive" type="submit">Confirm Audit</button>
	</div>
	</fieldset>
</s:form>

</body>
</html>