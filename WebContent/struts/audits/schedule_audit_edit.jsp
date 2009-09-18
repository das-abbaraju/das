<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" errorPage="../../exception_handler.jsp"%>
<html>
<head>
<title>Schedule Audit</title>
<meta name="help" content="Scheduling_Audits">
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />

<s:include value="../jquery.jsp"></s:include>

<script type="text/javascript"
	src="http://maps.google.com/maps?file=api&v=2.x&key=ABQIAAAAzr2EBOXUKnm_jVnk0OJI7xSosDVG8KKPE1-m51RBrvYughuyMxQ-i1QfUnH94QxWIa6N4U6MouMmBA"></script>
<script type="text/javascript" src="js/schedule_audit.js"></script>
<script type="text/javascript">
$(function() {
	$("#ScheduleAudit_scheduledDateDay").datepicker({ minDate: new Date(), numberOfMonths: [1, 2] });
	$('.selector').datepicker();
});
</script>
</head>
<body>
<s:include value="../contractors/conHeader.jsp" />

<h2>Reschedule <s:property value="conAudit.auditType.auditName"/></h2>

<s:form onsubmit="return submitForm();">
	<s:hidden name="auditID" />
	<s:hidden id="conAudit_latitude" name="conAudit.latitude" />
	<s:hidden id="conAudit_longitude" name="conAudit.longitude" />
	<fieldset class="form"><legend><span>Date &amp; Time</span></legend>
	<ol>
		<li><label>Audit Date:</label> <s:textfield name="scheduledDateDay"
			value="%{formatDate(conAudit.scheduledDate, 'MM/dd/yyyy')}" /> <s:date name="conAudit.scheduledDate" nice="true" /> </li>
		<li><label>Audit Time:</label> <s:textfield name="scheduledDateTime"
			value="%{formatDate(conAudit.scheduledDate, 'h:mm a')}" /> <s:property value="permissions.timezone.displayName"/></li>
		<li><label>Location:</label> <s:radio name="conAudit.conductedOnsite" theme="pics"
			list="#{false: 'Web', true: 'On Site (address below)'}" /></li>
		<s:if test="conAudit.contractorAccount.webcam.id > 0">
			<li><label>Current Webcam:</label> <s:property value="conAudit.contractorAccount.webcam" /></li>
		</s:if>
		<s:else>
			<li><label>Webcam:</label> <s:checkbox name="conAudit.needsCamera" /> Ship webcam to address below (if Location = Web)</li>
		</s:else>
	</ol>
	</fieldset>
	<fieldset class="form"><legend><span>Location</span></legend>
	<ol>
		<li>Please enter the address at which this audit will be conducted.</li>
		<li><label>Address:</label> <s:textfield id="conAudit_address" name="conAudit.address" /></li>
		<li><label>Address 2:</label> <s:textfield id="conAudit_address2" name="conAudit.address2" /></li>
		<li class="calculatedAddress"><label>City:</label> <s:textfield id="conAudit_city" name="conAudit.city" /></li>
		<li class="calculatedAddress"><label>State/Province:</label> <s:textfield id="conAudit_state"
			name="conAudit.state" size="6" /></li>
		<li><label>Zip or Postal Code:</label> <s:textfield id="conAudit_zip" name="conAudit.zip" size="10" /></li>
		<li class="calculatedAddress"><label>Country:</label> <s:textfield id="conAudit_country" name="conAudit.country"
			size="6" /></li>
		<li id="unverifiedLI" style="display: none;"><s:checkbox id="unverifiedCheckbox"
			onchange="$('#submitButton').toggle()" name="unverifiedCheckbox"></s:checkbox> This address is correct</li>
	</ol>
	</fieldset>
	<fieldset class="form"><legend><span>Contact Person</span></legend>
	<ol>
		<li>Please enter the primary representative for this audit.</li>
		<li><label>Name:</label> <s:textfield name="conAudit.contractorContact" /></li>
		<li><label>Email:</label> <s:textfield name="conAudit.phone2" /></li>
		<li><label>Phone:</label> <s:textfield name="conAudit.phone" /></li>
	</ol>
	</fieldset>
	<fieldset class="form submit">
	<div id="mainThinkingDiv"></div>
	<div>
	<button id="verifyButton" class="picsbutton" type="button" onclick="verifyAddress()">Verify Address</button>
	<button id="submitButton" class="picsbutton positive" type="submit" name="button" value="save">Save</button>
	</div>
	</fieldset>
</s:form>


</body>
</html>
