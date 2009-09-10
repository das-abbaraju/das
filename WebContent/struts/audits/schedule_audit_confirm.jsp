<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" errorPage="../../exception_handler.jsp"%>
<html>
<head>
<title>Schedule Audit</title>
<meta name="help" content="Scheduling_Audits">
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
<style type="text/css">
#auditHeader,#auditHeaderNav {
	display: none;
}
</style>
</head>
<body>
<s:include value="../contractors/conHeader.jsp" />

<s:form>
	<s:hidden name="auditID" />
	<s:hidden name="availabilitySelectedID" />
	<fieldset class="form"><legend><span>Audit Confirmation</span></legend>
	<ol>
		<li>Please confirm the information below:</li>
		<li><label>Primary Contact:</label> <s:property value="conAudit.contractorContact" /></li>
		<li><label>Method:</label> <s:property value="conAudit.auditLocation" /></li>
		<li><label>Location:</label> <s:if test="conAudit.conductedOnsite">
			<s:property value="conAudit.fullAddress" />
			<a href="ScheduleAudit.action?auditID=<s:property value="conAudit.id" />">Change</a>
		</s:if><s:else>
				Internet<br />
			<s:radio name="conAudit.needsCamera" theme="pics"
				list="#{true: 'Please mail me a webcam for my computer. Use this address: ' + conAudit.fullAddress, false: 'I have my own webcam that I can use for this audit'}" />
		</s:else></li>

		<li><label>Audit Date:</label> <s:date name="availabilitySelected.startDate" format="EEEE, MMM d, yyyy" /></li>
		<li><label>Audit Time:</label> <s:date name="availabilitySelected.startDate" format="h:mm a z" /></li>

		<li><label>Auditor Name:</label> <s:property value="availabilitySelected.user.name" /></li>
		<li><label>Auditor Email:</label> <s:property value="availabilitySelected.user.email" /></li>
		<li><label>Auditor Phone:</label> <s:property value="availabilitySelected.user.phone" /></li>
		<li><label>Auditor Fax:</label> <s:property value="availabilitySelected.user.fax" /></li>
		<li>If you have any questions about your up coming audit, please contact your assigned auditor directly.</li>
	</ol>
	</fieldset>
	<fieldset class="form submit">
	<div>
	<ol>
		<li><s:checkbox name="confirmed" /> By checking this box, I
		understand that if I need to reschedule this audit, <br />
		I must do so before <s:date name="lastCancellationTime" /> or I will be subject to a $150 rescheduling fee.</li>
	</ol>
	<button id="confirmButton" class="picsbutton positive" value="confirm" name="button"
		type="submit">Confirm Audit</button>
	</div>
	</fieldset>

	<div id="info">Congratulations, your audit is now scheduled. You should receive a confirmation email for your
	records.</div>
</s:form>

</body>
</html>