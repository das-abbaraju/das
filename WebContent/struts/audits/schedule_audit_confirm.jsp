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
		<li><label>Audit Date:</label> <s:date name="availabilitySelected.startDate" format="EEEE, MMM d, yyyy" /></li>
		<li><label>Audit Time:</label> <s:date name="availabilitySelected.startDate" format="h:mm a z" /></li>

		<s:if test="conAudit.conductedOnsite">
			<li><label>Location:</label> <s:property value="conAudit.fullAddress" />
				<a href="ScheduleAudit.action?auditID=<s:property value="conAudit.id" />">Change</a>
			</li>
		</s:if>
		<s:else>
			<li><label>Location:</label> Internet <a href="http://help.picsauditing.com/wiki/Office_Audit" class="help">What is this?</a> </li>
			<li><label>Video Camera:</label>
				<s:radio name="conAudit.needsCamera" theme="pics"
					list="#{false: 'I have my own webcam that I can use for this audit', true: 'Please mail me a webcam for my computer. Use this address: ' + conAudit.fullAddress}" />
			</li>
		</s:else>
		
		<li><label>Primary Contact:</label> <s:property value="conAudit.contractorContact" /></li>
		<li><label>Phone:</label> <s:property value="conAudit.phone" /></li>
		<li><label>Phone2/Email:</label> <s:property value="conAudit.phone2" /></li>
	</ol>
	</fieldset>
	<fieldset class="form submit">
	<div>
	<ol>
		<li><s:checkbox name="confirmed" />By checking this box, I
		understand that if I need to reschedule this audit, <br />
		I must do so before <s:date name="lastCancellationTime" /> or I will be subject to a $150 rescheduling fee.</li>
	</ol>
	<button id="confirmButton" class="picsbutton positive" value="confirm" name="button"
		type="submit">Confirm Audit</button>
	</div>
	</fieldset>
</s:form>

</body>
</html>