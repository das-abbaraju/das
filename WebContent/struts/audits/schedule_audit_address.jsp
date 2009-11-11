<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Schedule Audit</title>
<meta name="help" content="Scheduling_Audits">

<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=20091105" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=20091105" />
<s:include value="../jquery.jsp"></s:include>
<script src="js/jquery/cluetip/jquery.cluetip.js" type="text/javascript"></script>
<link href="js/jquery/cluetip/jquery.cluetip.css" media="screen" type="text/css" rel="stylesheet">

<script type="text/javascript" src="http://maps.google.com/maps?file=api&v=2.x&key=<s:property value="@com.picsauditing.actions.audits.ScheduleAudit@GOOGLE_API_KEY"/>"></script>
<script type="text/javascript" src="js/schedule_audit.js?v=20091105"></script>
<s:if test="conAudit != null">
<script type="text/javascript">
var conID = '<s:property value="conAudit.contractorAccount.id"/>';
var conJSON = null;
function useContractor() {
	$('form [name=conAudit.contractorContact]').val(conJSON.contact);
	$('form [name=conAudit.phone]').val(conJSON.phone);
	$('form [name=conAudit.phone2]').val(conJSON.email);
	$('form [name=conAudit.address]').val(conJSON.address);
	$('form [name=conAudit.city]').val(conJSON.city);
	$('form [name=conAudit.state]').val(conJSON.state);
	$('form [name=conAudit.zip]').val(conJSON.zip);
	$('form [name=conAudit.country]').val(conJSON.country);
}
function showContractor() {
	$.each(conJSON, function(k,v) {
			$('#'+k).text(v);
		}
	);
}
$(function() {
	$.getJSON("ContractorJson.action?id=" + conID,
		function(con){
			conJSON = con;
			$('#con_tip span').each(function() {
				$(this).text(conJSON[$(this).attr('id')]);
			}
		);
	});
	$('#showContractor').cluetip({
			local: true,
			attribute: 'href',
			titleAttribute: 'rel',
			arrows: true,
			cluetipClass: 'jtip'
		}
	);
});
</script>
</s:if>
<style type="text/css">
#auditHeader,#auditHeaderNav {
	display: none;
}

.calculatedAddress {
	display: none;
}
#con_tip ul {
	list-style: none;
}
</style>

</head>
<body>
<s:include value="../contractors/conHeader.jsp" />

<s:form onsubmit="return submitForm();">
	<s:hidden name="auditID" />
	<s:hidden name="button" value="address"/>
	<fieldset class="form"><legend><span>Contact Person</span></legend>
	<ol>
		<li>Please enter your company's primary representative for this audit.</li>
		<li><label></label><input type="button" value="Same as Primary" onclick="useContractor()"/> <a id="showContractor" href="#con_tip" rel="<s:property value="contractor.name"/>">Preview</a></li>
		<li><label>Name:</label> <s:textfield name="conAudit.contractorContact" /></li>
		<li><label>Email:</label> <s:textfield name="conAudit.phone2" /></li>
		<li><label>Phone:</label> <s:textfield name="conAudit.phone" /></li>
	</ol>
	</fieldset>
	<fieldset class="form"><legend><span>Enter the Audit Location</span></legend>
	<ol>
		<li>Please enter the address at which this audit will be conducted.</li>
		<li><label>Address:</label> <s:textfield id="conAudit_address" name="conAudit.address" size="50" /> No PO Boxes</li>
		<li><label>Address 2:</label> <s:textfield id="conAudit_address2" name="conAudit.address2" /> Suite/Apartment</li>
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
	<fieldset class="form submit">
	<div id="mainThinkingDiv"></div>
	<div>
	<button id="verifyButton" class="picsbutton" type="button" onclick="verifyAddress()">Verify Address</button>
	<button id="submitButton" style="display: none;" class="picsbutton positive" type="submit">Next &gt;&gt;</button>
	</div>
	</fieldset>
	<s:hidden id="conAudit_latitude" name="conAudit.latitude" />
	<s:hidden id="conAudit_longitude" name="conAudit.longitude" />
</s:form>

<div id="con_tip">
<ul>
	<li><label>Name:</label> <span id="contact"></span></li>
	<li><label>Phone:</label> <span id="phone"></span></li>
	<li><label>Email:</label> <span id="email"></span></li>
	<li><label>Address:</label> <span id="address"></span></li>
	<li><label>City:</label> <span id="city"></span></li>
	<li><label>State:</label> <span id="state"></span></li>
	<li><label>Zip:</label> <span id="zip"></span></li>
	<li><label>Country:</label> <span id="country"></span></li>
</ul>
</div>

</body>
</html>