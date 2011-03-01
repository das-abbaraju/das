<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:text name="%{scope}.title" /></title>
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
		$("#scheduledDateTime,#scheduledDateDay").click(function() {
			$('#needsReschedulingFee').slideDown();
			$('#scheduledDateTime').attr('disabled', true);
			$('#scheduledDateDay').attr('disabled', true);
			$('#scheduledDateTime,#scheduledDateDay').unbind('click');
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
		$('#needsReschedulingFee').text('<s:text name="%{scope}.message.NoReschedulingFee" />' + "This contractor will NOT be charged the rescheduling fee if the audit date and time is changed.");
	} else
		$('#needsReschedulingFee').text('<s:text name="%{scope}.message.ReschedulingFee" />' + "This contractor will be charged the rescheduling fee if the audit date and time is changed.");

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

<h2><s:text name="%{scope}.label.Reschedule"><s:param><s:text name="%{conAudit.auditType.getI18nKey('name')}" /></s:param></s:text></h2>

<div>
<a href="MySchedule.action?currentUserID=<s:property value="conAudit.auditor.id"/>" class="picsbutton" target="_BLANK" title="opens in new window"><s:text name="%{scope}.button.OpenAuditorSchedule"><s:param><s:property value="conAudit.auditor.name"/></s:param></s:text></a>
</div>

<s:form onsubmit="return submitForm();">
	<s:hidden name="auditID" />
	<fieldset class="form">
	<h2 class="formLegend"><s:text name="%{scope}.label.DateTime" /></h2>
	<ol>
		<li><label><s:text name="%{scope}.label.AuditDate" />:</label> <s:textfield name="scheduledDateDay" id="scheduledDateDay"
			value="%{formatDate(conAudit.scheduledDate, 'dd/MM/yyyy')}" /> <s:date name="conAudit.scheduledDate" nice="true" /> </li>
		<s:if test="needsReschedulingFee">
			<input type="hidden" name="feeOverride" value="false" />
			<div id="needsReschedulingFee" class="alert">
				<s:text name="%{scope}.message.ReschedulingWarning"><s:param value="%{rescheduling.amount}" /></s:text><br />
				<input type="button" onclick="showChooseDate(); return false;" value="<s:text name="button.Continue" />" class="picsbutton positive" />
				<s:if test="permissions.userId == 1029 || permissions.userId == 935 || permissions.userId == 11503 || permissions.userId == 9615">
					<!-- This option is available for Mina, Harvey, Gary, and Rick only -->
					<input type="button" onclick="showChooseDate(true); return false;" value="<s:text name="%{scope}.button.OverrideFee" />" class="picsbutton" />
				</s:if>
			</div>
		</s:if>
		<li><label><s:text name="%{scope}.label.AuditTime" />:</label> <s:textfield name="scheduledDateTime" id="scheduledDateTime"
			value="%{formatDate(conAudit.scheduledDate, 'h:mm a')}" cssClass="time"/> <s:property value="permissions.timezone.displayName"/></li>
		<li><label><s:text name="global.SafetyProfessional" />:</label> <s:select list="auditorList" listKey="id" listValue="name" name="auditor.id" value="conAudit.auditor.id"/></li>
		<li><label><s:text name="%{scope}.label.Location" />:</label> <s:radio name="conAudit.conductedOnsite" theme="pics"
			list="#{false: getText(scope + '.message.Web'), true: getText(scope + '.message.OnSite')}" /></li>
		<s:if test="conAudit.contractorAccount.webcam.id > 0">
			<li><label><s:text name="%{scope}.label.CurrentWebcam" />:</label> <s:property value="conAudit.contractorAccount.webcam" /></li>
		</s:if>
		<s:else>
			<li><label><s:text name="%{scope}.label.Webcam" />:</label> <s:checkbox name="conAudit.needsCamera" /> 
			<label class="input" for="ScheduleAudit_conAudit_needsCamera"><s:text name="%{scope}.message.ShipWebcam" /></label></li>
		</s:else>
	</ol>
	</fieldset>
	<fieldset class="form">
	<h2 class="formLegend"><s:text name="%{scope}.label.Location" /></h2>
	<ol style="float: left;">
		<li><s:text name="%{scope}.message.AddressConducted" /></li>
		<li><label></label><input type="button" value="<s:text name="%{scope}.button.UseContractorInfo" />" onclick="useContractor()"/></li>
		<li><label><s:text name="global.Address" />:</label> <s:textfield id="conAudit_address" name="conAudit.address" /></li>
		<li><label><s:text name="global.Address" /> 2:</label> <s:textfield id="conAudit_address2" name="conAudit.address2" /></li>
		<li class="calculatedAddress"><label><s:text name="global.City" />:</label> <s:textfield id="conAudit_city" name="conAudit.city" /></li>
		<li class="calculatedAddress"><label><s:text name="global.State" />:</label> <s:select id="conAudit_state"
			name="conAudit.state" list="stateList" listKey="isoCode" listValue="name" headerKey="" headerValue=" - State/Province - "/></li>
		<li><label><s:text name="global.ZipPostalCode" />:</label> <s:textfield id="conAudit_zip" name="conAudit.zip" size="10" /></li>
		<li class="calculatedAddress"><label>Country:</label> <s:select id="conAudit_country" name="conAudit.country"
			list="countryList" listKey="isoCode" listValue="name" headerKey="" headerValue=" - Country - " value="locale.country"/></li>
		<li class="calculatedAddress"><label><s:text name="%{scope}.label.Latitude" />:</label> <s:textfield id="conAudit_latitude" name="conAudit.latitude" size="10" /></li>
		<li class="calculatedAddress"><label><s:text name="%{scope}.label.Longitude" />:</label> <s:textfield id="conAudit_longitude" name="conAudit.longitude" size="10" /></li>
		<li id="unverifiedLI" style="display: none;"><s:checkbox id="unverifiedCheckbox"
			onchange="$('#submitButton').toggle()" name="unverifiedCheckbox"></s:checkbox> <s:text name="%{scope}.message.AddressIsCorrect" /></li>
	</ol>
	<div id="mappreview"></div>
	</fieldset>
	<fieldset class="form">
	<h2 class="formLegend"><s:text name="%{scope}.label.ContactPerson" /></h2>
	<ol>
		<li><s:text name="%{scope}.message.PrimaryRepresentative" /></li>
		<li><label><s:text name="User.name" />:</label> <s:textfield name="conAudit.contractorContact" /></li>
		<li><label><s:text name="User.email" />:</label> <s:textfield name="conAudit.phone2" /></li>
		<li><label><s:text name="User.phone" />:</label> <s:textfield name="conAudit.phone" /></li>
	</ol>
	</fieldset>
	<fieldset class="form submit">
	<div id="mainThinkingDiv"></div>
	<div>
	<button id="verifyButton" class="picsbutton" type="button" onclick="verifyAddress()"><s:text name="%{scope}.button.VerifyAddress" /></button>
	<button id="submitButton" class="picsbutton positive" type="submit" name="button" value="Save"><s:text name="button.Save" /></button>
	</div>
	</fieldset>
</s:form>


</body>
</html>
