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

<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
<script
	src="http://maps.google.com/maps?file=api&v=2.x&key=ABQIAAAAzr2EBOXUKnm_jVnk0OJI7xSosDVG8KKPE1-m51RBrvYughuyMxQ-i1QfUnH94QxWIa6N4U6MouMmBA"
	type="text/javascript"></script>
<script type="text/javascript">

function findAddress() {
	startThinking({message: "Validating Address"});
	if (GBrowserIsCompatible()) {
		var geocoder = new GClientGeocoder();
		if (geocoder) {
			var address = $("#conAudit_address").val() + ", " + $("#conAudit_zip").val();
			geocoder.getLocations(
				address,
				function(matches) {
					try {
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
						
						$("#verifyButton").hide();
						$("#submitButton").show();
						$(".calculatedAddress").show("slow");
					} catch(err) {
						$("#submitButton").hide();
						$("#verifyButton").show();
						$(".calculatedAddress").hide();
						alert(err);
					}
					stopThinking();
				}
			);
		}
	}
}

</script>

</head>
<body>
<s:include value="../contractors/conHeader.jsp" />

<s:form>
	<s:hidden name="auditID" />
	<fieldset class="form"><legend><span>Contact Person</span></legend>
	<ol>
		<li>Please enter your company's primary representative for this audit.</li>
		<li><label>Name:</label> <s:textfield name="conAudit.contractorContact" /></li>
		<li><label>Primary Phone:</label> <s:textfield name="conAudit.phone" /></li>
		<li><label>Alternate Phone or Email:</label> <s:textfield name="conAudit.phone2" /></li>
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
	</ol>
	</fieldset>
	<fieldset class="form submit">
	<div id="mainThinkingDiv"></div>
	<div>
	<button id="verifyButton" class="picsbutton positive" type="button" onclick="findAddress();">Verify Address</button>
	<button id="submitButton" style="display: none;" class="picsbutton positive" type="submit" name="button" value="address">Next &gt;&gt;</button>
	</div>
	</fieldset>
	<s:hidden id="conAudit_latitude" name="conAudit.latitude" />
	<s:hidden id="conAudit_longitude" name="conAudit.longitude" />
</s:form>

</body>
</html>