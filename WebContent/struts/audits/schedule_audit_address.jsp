<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Schedule Audit</title>
<meta name="help" content="Scheduling_Audits">

<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<style type="text/css">
#auditHeader,#auditHeaderNav {
	display: none;
}
.calculatedAddress {
	display: none;
}

</style>

<s:include value="../jquery.jsp"></s:include>
<script
	src="http://maps.google.com/maps?file=api&v=2.x&key=ABQIAAAAzr2EBOXUKnm_jVnk0OJI7xSosDVG8KKPE1-m51RBrvYughuyMxQ-i1QfUnH94QxWIa6N4U6MouMmBA"
	type="text/javascript"></script>
<script type="text/javascript">
// Statuses:
// 0 = No address entered
// 1 = Verified address, ready to submit
// 10 = Can't communicate with GMail
// 11 = Incompatible browser
// 12 = GeoCoder couldn't instantiate
// 13 = Invalid address

var status = 0;

$(document).ready(function() {
	if (isVerified()) {
		$(".calculatedAddress").show("slow");
		$("#submitButton").show();
	}
});

function isVerified() {
	if ($("#conAudit_latitude").val() == 0)
		return false;
	if ($("#conAudit_longitude").val() == 0)
		return false;

	return true;
}

function unVerify() {
	$("#conAudit_latitude").val(0);
	$("#conAudit_longitude").val(0);
	$("#submitButton").hide();
	$("#unverifiedLI").show();
}

function verifyAddress() {
	if (GBrowserIsCompatible()) {
		var geocoder = new GClientGeocoder();
		if (geocoder) {
			startThinking({message: "Validating Address"});
			var address = $("#conAudit_address").val() + ", " + $("#conAudit_zip").val();
			geocoder.getLocations(
				address,
				function(matches) {
					try {
						// $.gritter.removeAll();
						if (matches.Placemark.length == 0) {
							throw("Address (" + address + ") could not be found");
						}
						
						var detail = matches.Placemark[0].AddressDetails;
						var latlong = matches.Placemark[0].Point.coordinates;
						if (detail.Accuracy < 8) {
							throw("Address (" + address + ") could not be found accurately");
						}

						$("#conAudit_address").val(detail.Country.AdministrativeArea.Locality.Thoroughfare.ThoroughfareName);
						$("#conAudit_country").val(detail.Country.CountryNameCode);
						$("#conAudit_state").val(detail.Country.AdministrativeArea.AdministrativeAreaName);
						$("#conAudit_city").val(detail.Country.AdministrativeArea.Locality.LocalityName);
						$("#conAudit_latitude").val(latlong[0]);
						$("#conAudit_longitude").val(latlong[1]);

						$("#unverifiedLI").hide();
						$("#submitButton").show();
					} catch(err) {
						unVerify();
						$.gritter.add({title: 'Address Verification', text: err});
					}
					$(".calculatedAddress").show("slow");
					stopThinking();
				}
			);
			return false;
		}
	} else {
		$(".calculatedAddress").show("slow");
		$("#submitButton").show();
	}
}

function submitForm() {
	if (!isVerified()) {
		if ($("#unverifiedCheckbox").val())
			return true;
		verifyAddress();
		return false;
	}
	return true;
}

</script>

</head>
<body>
<s:include value="../contractors/conHeader.jsp" />

<s:form onsubmit="return submitForm();">
	<s:hidden name="auditID" />
	<fieldset class="form"><legend><span>Contact Person</span></legend>
	<ol>
		<li>Please enter your company's primary representative for this audit.</li>
		<li><label>Name:</label> <s:textfield name="conAudit.contractorContact" /></li>
		<li><label>Email:</label> <s:textfield name="conAudit.phone2" /></li>
		<li><label>Phone:</label> <s:textfield name="conAudit.phone" /></li>
	</ol>
	</fieldset>
	<fieldset class="form"><legend><span>Enter the Audit Location</span></legend>
	<ol>
		<li>Please enter the address at which this audit will be conducted.</li>
		<li><label>Address:</label> <s:textfield id="conAudit_address" name="conAudit.address" /></li>
		<li><label>Address 2:</label> <s:textfield id="conAudit_address2" name="conAudit.address2" /></li>
		<li class="calculatedAddress"><label>City:</label> <s:textfield id="conAudit_city" name="conAudit.city" /></li>
		<li class="calculatedAddress"><label>State/Province:</label> <s:textfield id="conAudit_state" name="conAudit.state" size="6" /></li>
		<li><label>Zip or Postal Code:</label> <s:textfield id="conAudit_zip" name="conAudit.zip" size="10" /></li>
		<li class="calculatedAddress"><label>Country:</label> <s:textfield id="conAudit_country" name="conAudit.country" size="6" /></li>
		<li id="unverifiedLI" style="display: none;"><s:checkbox id="unverifiedCheckbox" onchange="$('#submitButton').toggle()" name="unverifiedCheckbox"></s:checkbox> This address is correct</li>
	</ol>
	</fieldset>
	<fieldset class="form submit">
	<div id="mainThinkingDiv"></div>
	<div>
	<button id="verifyButton" class="picsbutton" type="button" onclick="verifyAddress()" >Verify Address</button>
	<button id="submitButton" style="display: none;" class="picsbutton positive" type="submit" name="button" value="address">Next &gt;&gt;</button>
	</div>
	</fieldset>
	<s:hidden id="conAudit_latitude" name="conAudit.latitude" />
	<s:hidden id="conAudit_longitude" name="conAudit.longitude" />
</s:form>

</body>
</html>