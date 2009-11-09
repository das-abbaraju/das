<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="contractor.name" /></title>

<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css?v=20091105" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/notes.css" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/audit.css?v=20091105" />
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
	}
}
);
$(function(){
	$('#audit_tabs').tabs();

	$('#con_tasks').load('ContractorTasksAjax.action?id=<s:property value="id"/>');
});
</script>
<style>
#content div.column {
	width: 47%;
	margin: 0px 5px;
	padding: 5px;
	float: left;
	position: relative;
}
#content div.contractor_block {
	float: left;
	width: 98%;
	position: relative;
	margin-bottom: 5px;
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
	border: 1px solid #000;
}
#mappreview div {
	color: auto;
	font-family: Arial,sans-serif;
	font-size: 100%;
	line-height: 1;
	margin: 0;
	padding: 0;
}
#content div.shorttable {
	line-height: normal;
}
#content div.scroll {
	max-height: 300px;
	/* IE Image max-width */
	height: expression(this.width > 300 ? '300' : true);
	overflow-y: scroll;
	float: left;
}
</style>
</head>
<body>
<s:include value="conHeader.jsp" />
<s:if test="!contractor.activeB">
	<div class="alert">This contractor is not active.
	<s:if test="contractor.lastPayment != null">They last paid on <s:property value="contractor.lastPayment"/>.</s:if>
	</div>
</s:if>
<s:if test="contractor.acceptsBids">
	<s:if test="canUpgrade">
		<div id="info">This is a BID-ONLY Account and will expire on <strong><s:date name="contractor.paymentExpires" format="M/d/yyyy" /></strong><br/>
		Click <a href="ContractorView.action?id=<s:property value="id" />" class="picsbutton positive">Upgrade to Full Membership</a> to continue working at your selected facilities.</div>
	</s:if>
	<s:else>
		<div class="alert">This is a BID-ONLY Contractor Account.</div>
	</s:else>
</s:if>
<s:elseif test="contractor.paymentOverdue && (permissions.admin || permissions.contractor)">
	<div class="alert">This contractor has an outstanding invoice due</div>
</s:elseif>
<s:if test="permissions.admin && !contractor.mustPayB">
	<div class="alert">This account has a lifetime free membership</div>
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
		
	</div>
	<div class="contractor_block">
		<div id="audit_tabs">
		     <ul>
		         <li><a href="ContractorDetailsAjax.action?id=<s:property value="id"/>&button=upcoming" title="upcoming"><span>Upcoming Audits</span></a></li>
		         <li><a href="ContractorDetailsAjax.action?id=<s:property value="id"/>&button=current" title="current"><span>Current Audits</span></a></li>
		     </ul>
		     <div id="upcoming" class="shorttable scroll"></div>
		     <div id="current" class="shorttable scroll"></div>
			<br clear="all"/>
		</div>
	</div>
	<div class="contractor_block" id="con_tasks"></div>
	<div class="contractor_block shorttable">
		<s:iterator value="contractor.oshas">
			<s:iterator value="value">
				<s:iterator value="value">
					<s:include value="../audits/audit_cat_osha2.jsp"/>
				</s:iterator>
			</s:iterator>
		</s:iterator>
	</div>
	<div class="contractor_block shorttable">
		<table class="report">
		<thead>
			<tr>
				<td>Year</td>
				<td>EMR</td>
			</tr>
		</thead>
		<s:iterator value="contractor.emrs">
			<tr>
				<td><s:property value="key"/></td>
				<td><s:property value="value.answer"/></td>
			</tr>
		</s:iterator>
		</table>
	</div>
</div>

<div class="column">
	<div class="contractor_block">
		<div id="mappreview"></div>
	</div>
	<div class="contractor_block">
		<div class="shorttable scroll">
			<table class="report">
				<thead>
					<tr>
						<td>Flag</td>
						<td>Operator</td>
						<td>Waiting On</td>
					</tr>	
				</thead>
			<s:iterator value="activeOperators">
				<tr>
					<td>
						<s:if test="flag != null">
							<a href="ContractorFlag.action?id=<s:property value="contractor.id" />&opID=<s:property value="operatorAccount.id" />"><s:property value="flag.flagColor.smallIcon" escape="false" /></a>
						</s:if>
						<s:else>
							<a href="ContractorFlag.action?id=<s:property value="contractor.id" />&opID=<s:property value="operatorAccount.id" />"><img src="images/icon_Flag.gif" width="10" height="12" border="0" title="Blank"/></a>
						</s:else>
					</td>
					<td>
						<a href="ContractorFlag.action?id=<s:property value="contractor.id" />&opID=<s:property value="operatorAccount.id" />"><s:property value="operatorAccount.name" /></a>
					</td>
					<td>
						<s:property value="flag.waitingOn"/>
					</td>
				</tr>
			</s:iterator>
			</table>
		</div>
	</div>
	<div class="contractor_block">
		<div class="shorttable scroll">
			<table class="report">
				<thead>
					<tr>
						<th>Transaction</th>
						<th>#</th>
						<th>Date</th>
						<th>Amount</th>
						<th>Outstanding</th>
						<s:if test="permissions.admin">
							<th>Status</th>
						</s:if>	
					</tr>
				</thead>
				<tbody>
					<s:iterator value="transactions">
						<s:set name="url" value="" />
						<s:if test="class.simpleName == 'Invoice'">
							<s:set name="url" value="'InvoiceDetail.action?invoice.id='+id" />
						</s:if>
						<s:elseif test="class.simpleName == 'Payment'">
							<pics:permission perm="Billing">
								<s:set name="url" value="'PaymentDetail.action?payment.id='+id" />
							</pics:permission>
						</s:elseif>
						<tr
							<s:if test="#url.length() > 0">
								class="clickable <s:if test="status.void"> inactive</s:if> " 
								onclick="window.location = '<s:property value="#url"/>'"
							</s:if>
							>
							<td><s:property value="class.simpleName" /></td>
							<td class="right"><s:if test="#url.length() > 0">
								<a href="<s:property value="#url" />"><s:property value="id" /></a>
							</s:if><s:else>
								<s:property value="id" />
							</s:else></td>
							<td class="right"><s:date name="creationDate" format="M/d/yy" /></td>
							<td class="right">$<s:property value="totalAmount" /></td>
							<td class="right">$ <s:if
								test="class.simpleName.equals('Payment') && status.toString() == 'Unpaid' && balance > 0">
								-</s:if> <s:property value="balance" /></td>
							<s:if test="permissions.admin">
								<td><s:property value="status"/></td>
							</s:if>
						</tr>
					</s:iterator>
				</tbody>
			</table>
		</div>
	</div>
</div>

<div class="contractor_block">
	<s:include value="../notes/account_notes_embed.jsp"/>
</div>
<br clear="all" />

</body>
</html>
