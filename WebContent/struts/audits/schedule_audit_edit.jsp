<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Schedule Audit</title>
<meta name="help" content="Scheduling_Audits">
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />

<s:include value="../jquery.jsp"></s:include>
<script src="js/jquery/timeentry/jquery.timeentry.min.js" type="text/javascript"></script>
<link href="js/jquery/timeentry/jquery.timeentry.css" media="screen" type="text/css" rel="stylesheet">

<script type="text/javascript" src="http://maps.google.com/maps?file=api&v=2.x&key=<s:property value="@com.picsauditing.actions.audits.ScheduleAudit@GOOGLE_API_KEY"/>"></script>
<script type="text/javascript" src="js/schedule_audit.js?v=<s:property value="version"/>"></script>
<style type="text/css">
#mappreview {
	float: right;
	width: 350px;
	height: 350px;
	border: 2px solid #003768;
}

#mappreview div {
	color: auto;
	font-family: Arial,sans-serif;
	font-size: 100%;
	line-height: 1;
	margin: 0;
	padding: 0;
}
div#needsReschedulingFee {
	display: none;
}
</style>
<script type="text/javascript">
var map;
$(function() {
	<s:if test="needsReschedulingFee">
		$("#scheduledDateDay").click(function() {
			$('#needsReschedulingFee').slideDown();
			$('#scheduledDateTime').attr('disabled', true);
			$('#scheduledDateDay').attr('disabled', true);
			$('#scheduledDateDay').unbind('click');
		});
	</s:if>
	<s:else>
		$("#scheduledDateDay").datepicker({ minDate: new Date(), numberOfMonths: [1, 2] });
	</s:else>
	$('.selector').datepicker();

	if (GBrowserIsCompatible()) {
		map = new GMap2(document.getElementById("mappreview"));
		map.setUIToDefault();
		recenterMap();
	}
});

function recenterMap() {
	map.clearOverlays();
	var point = new GLatLng($('#conAudit_latitude').val(), $('#conAudit_longitude').val());
	map.setCenter(point, 11);
	map.addOverlay(new GMarker(point));
}

var conID = '<s:property value="conAudit.contractorAccount.id"/>';
function useContractor() {
	$.getJSON("ContractorJson.action", {id: conID}, 
		function(con){
			$('form [name=conAudit.contractorContact]').val(con.primaryContact.name);
			$('form [name=conAudit.phone]').val(con.primaryContact.phone);
			$('form [name=conAudit.phone2]').val(con.primaryContact.email);
			$('form [name=conAudit.address]').val(con.address);
			$('form [name=conAudit.city]').val(con.city);
			$('form [name=conAudit.state]').val(con.state);
			$('form [name=conAudit.zip]').val(con.zip);
			$('form [name=conAudit.country]').val(con.country);
			recenterMap();
		});
}
<s:if test="!needsReschedulingFee">
$(function(){
	$('.time').timeEntry({
			ampmPrefix: ' ',
			spinnerImage: 'images/spinnerDefault.png' 
		}
	);
});
</s:if>

function showChooseDate(override) {
	if (override) {
		$('input[name=feeOverride]').val(true);
		$('#needsReschedulingFee').text("This contractor will NOT be charged the rescheduling fee.");
	} else
		$('#needsReschedulingFee').text("This contractor will be charged the rescheduling fee.");

	$('#scheduledDateDay').attr('disabled', false);
	$('#scheduledDateTime').attr('disabled', false);
	$("#scheduledDateDay").datepicker({ minDate: new Date(), numberOfMonths: [1, 2] }).datepicker("show");
	$('.time').timeEntry({
		ampmPrefix: ' ',
		spinnerImage: 'images/spinnerDefault.png' 
	});
}
</script>
</head>
<body>
<s:include value="../contractors/conHeader.jsp" />

<h2>Reschedule <s:property value="conAudit.auditType.auditName"/></h2>

<div>
<a href="MySchedule.action?currentUserID=<s:property value="conAudit.auditor.id"/>" class="picsbutton" target="_BLANK" title="opens in new window">Open <s:property value="conAudit.auditor.name"/>'s Schedule</a>
</div>

<s:form onsubmit="return submitForm();">
	<s:hidden name="auditID" />
	<fieldset class="form">
	<h2 class="formLegend">Date &amp; Time</h2>
	<ol>
		<li><label>Audit Date:</label> <s:textfield name="scheduledDateDay" id="scheduledDateDay"
			value="%{formatDate(conAudit.scheduledDate, 'MM/dd/yyyy')}" /> <s:date name="conAudit.scheduledDate" nice="true" /> </li>
		<s:if test="needsReschedulingFee">
			<input type="hidden" name="feeOverride" value="false" />
			<div id="needsReschedulingFee" class="alert">
				This audit is scheduled to be conducted within 48 hours.
				If the scheduled date is changed, the contractor will be charged a $199 rescheduling fee.<br />
				<input type="button" onclick="showChooseDate(); return false;" value="Continue" class="picsbutton positive" />
				<input type="button" onclick="showChooseDate(true); return false;" value="Override Fee" class="picsbutton" />
			</div>
		</s:if>
		<li><label>Audit Time:</label> <s:textfield name="scheduledDateTime" id="scheduledDateTime"
			value="%{formatDate(conAudit.scheduledDate, 'h:mm a')}" cssClass="time"/> <s:property value="permissions.timezone.displayName"/></li>
		<li><label>Safety Professional:</label> <s:select list="auditorList" listKey="id" listValue="name" name="auditor.id" value="conAudit.auditor.id"/></li>
		<li><label>Location:</label> <s:radio name="conAudit.conductedOnsite" theme="pics"
			list="#{false: 'Web', true: 'On Site (address below)'}" /></li>
		<s:if test="conAudit.contractorAccount.webcam.id > 0">
			<li><label>Current Webcam:</label> <s:property value="conAudit.contractorAccount.webcam" /></li>
		</s:if>
		<s:else>
			<li><label>Webcam:</label> <s:checkbox name="conAudit.needsCamera" /> 
			<label class="input" for="ScheduleAudit_conAudit_needsCamera">Ship webcam to address below (if Location = Web)</label></li>
		</s:else>
	</ol>
	</fieldset>
	<fieldset class="form">
	<h2 class="formLegend">Location</h2>
	<ol style="float: left;">
		<li>Please enter the address at which this audit will be conducted.</li>
		<li><label></label><input type="button" value="Use Contractor Contact Info" onclick="useContractor()"/></li>
		<li><label>Address:</label> <s:textfield id="conAudit_address" name="conAudit.address" /></li>
		<li><label>Address 2:</label> <s:textfield id="conAudit_address2" name="conAudit.address2" /></li>
		<li class="calculatedAddress"><label>City:</label> <s:textfield id="conAudit_city" name="conAudit.city" /></li>
		<li class="calculatedAddress"><label>State/Province:</label> <s:select id="conAudit_state"
			name="conAudit.state" list="stateList" listKey="isoCode" listValue="name" headerKey="" headerValue=" - State/Province - "/></li>
		<li><label>Zip or Postal Code:</label> <s:textfield id="conAudit_zip" name="conAudit.zip" size="10" /></li>
		<li class="calculatedAddress"><label>Country:</label> <s:select id="conAudit_country" name="conAudit.country"
			list="countryList" listKey="isoCode" listValue="name" headerKey="" headerValue=" - Country - " value="locale.country"/></li>
		<li class="calculatedAddress"><label>Latitude:</label> <s:textfield id="conAudit_latitude" name="conAudit.latitude" size="10" /></li>
		<li class="calculatedAddress"><label>Longitude:</label> <s:textfield id="conAudit_longitude" name="conAudit.longitude" size="10" /></li>
		<li id="unverifiedLI" style="display: none;"><s:checkbox id="unverifiedCheckbox"
			onchange="$('#submitButton').toggle()" name="unverifiedCheckbox"></s:checkbox> This address is correct</li>
	</ol>
	<div id="mappreview"></div>
	</fieldset>
	<fieldset class="form">
	<h2 class="formLegend">Contact Person</h2>
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
	<button id="submitButton" class="picsbutton positive" type="submit" name="button" value="Save">Save</button>
	</div>
	</fieldset>
</s:form>


</body>
</html>
