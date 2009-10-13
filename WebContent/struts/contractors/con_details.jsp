<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="contractor.name" /></title>

<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="http://maps.google.com/maps?file=api&v=2.x&key=<s:property value="@com.picsauditing.actions.audits.ScheduleAudit@GOOGLE_API_KEY"/>"></script>
<script>
var map;
$(
function () {
	if (GBrowserIsCompatible()) {
		var geocoder = new GClientGeocoder();
		if (geocoder) {
			var address = '<s:property value="contractor.address+', '+contractor.zip"/>';
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
						if (GBrowserIsCompatible()) {
							map = new GMap2(document.getElementById("mappreview"));
							map.setUIToDefault();
							map.clearOverlays();
							var point = new GLatLng(latlong[1], latlong[0]);
							map.setCenter(point, 11);
							map.addOverlay(new GMarker(point));
						}
					} catch(err) {
						$.gritter.add({title: 'Address Verification', text: err});
					}
				}
			);
			return false;
		}
	} else {
		$(".calculatedAddress").show("slow");
		$("#submitButton").show();
	}
}
);
</script>
<style>
#content div.column {
	width: 47%;
	margin: 0px 5px;
	padding: 5px;
	float: left;
	border: 1px solid #000;
	position: relative;
}
div.contractor_block {
	border: 1px solid #000;
	float: left;
	width: 98%;
	position: relative;
}
div.contractor_info {
	float: left;
}
div.contractor_info ul {
	list-style: none;
	margin-left: 0px;
}
#content div.contractor_description {
	background-color:#797B7A;
	color:#F7F7F7;
	padding:5px 10px;
	clear: left;
	width: 96%;
}
img.contractor_logo {
	float: left;
	max-width: 40%;
	/* IE Image max-width */
	width: expression(this.width > 225 ? '45%' : true);
}
#mappreview {
	width: 95%;
	height: 300px;
}
#mappreview div {
	color: auto;
	font-family: Arial,sans-serif;
	font-size: 100%;
	line-height: 1;
	margin: 0;
	padding: 0;
}

</style>
</head>
<body>
<s:include value="conHeader.jsp" />
<s:if test="!contractor.activeB">
	<div id="alert">This contractor is not active.
	<s:if test="contractor.lastPayment != null">They last paid on <s:property value="contractor.lastPayment"/>.</s:if>
	</div>
</s:if>
<s:if test="contractor.acceptsBids">
	<s:if test="canUpgrade">
		<div id="info">This is a Trial Only Account and will expire on <strong><s:date name="contractor.paymentExpires" format="M/d/yyyy" /></strong><br/>
		Click <a href="ContractorView.action?id=<s:property value="id" />" class="picsbutton positive">Upgrade to Full Membership</a> to continue working at your selected facilities.</div>
	</s:if>
	<s:else>
		<div id="alert">This is a Trial Contractor Account.</div>
	</s:else>
</s:if>
<s:elseif test="contractor.paymentOverdue && (permissions.admin || permissions.contractor)">
	<div id="alert">This contractor has an outstanding invoice due</div>
</s:elseif>
<s:if test="permissions.admin && !contractor.mustPayB">
	<div id="alert">This account has a lifetime free membership</div>
</s:if>


<div class="column">
	<div class="contractor_block">
		<img src="ContractorLogo.action?id=<s:property value="id"/>" class="contractor_logo" />
		<div class="contractor_info">
			<h4><s:property value="contractor.name"/></h4>
			<div>
				<ul>
					<li><s:property value="contractor.address"/></li>
					<li>
						<s:property value="contractor.city"/>,
						<s:property value="contractor.state"/>,
						<s:property value="contractor.zip"/>
					</li>
				</ul>
			</div>
			<div>
				<ul>
					<li>Contact: <span class="value"><s:property value="contractor.contact" /></span></li>
					<li>Phone: <span class="value"><s:property value="contractor.phone" /></span></li>
					<s:if test="contractor.phone2"><li>Other Phone: <span class="value"><s:property value="contractor.phone2" /></span></li></s:if>
					<s:if test="contractor.fax"><li>Fax: <span class="value"><s:property value="contractor.fax" /></span></li></s:if>
					<li>Email: <strong><a href="mailto:<s:property value="contractor.email" />" class="value"><s:property value="contractor.email" /></a></strong></li>
					<s:if test="contractor.webUrl.length() > 0"><li>Web site: <strong><a href="http://<s:property value="contractor.webUrl" />" class="value" target="_blank"><s:property value="contractor.webUrl" /></a></strong></li></s:if>
					<s:if test="@com.picsauditing.util.Strings@isEmpty(contractor.brochureFile) == false">
						<li><a href="DownloadContractorFile.action?id=<s:property value="id" />" target="_BLANK">Company Brochure</a></li>
					</s:if>
				</ul>
			</div>
		</div>
		<div class="contractor_info contractor_description"><s:property value="contractor.descriptionHTML" escape="false" /></div>
	</div>
</div>
<div class="column">
	<div class="contractor_block">
		<div id="mappreview"></div>
	</div>
</div>

<br clear="all" />

</body>
</html>
