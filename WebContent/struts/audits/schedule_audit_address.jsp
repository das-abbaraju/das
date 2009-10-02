<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Schedule Audit</title>
<meta name="help" content="Scheduling_Audits">

<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<s:include value="../jquery.jsp"></s:include>

<script type="text/javascript" src="http://maps.google.com/maps?file=api&v=2.x&key=<s:property value="@com.picsauditing.actions.audits.ScheduleAudit@GOOGLE_API_KEY"/>"></script>
<script type="text/javascript" src="js/schedule_audit.js"></script>
<s:if test="conAudit != null">
<script type="text/javascript">
var conID = '<s:property value="conAudit.contractorAccount.id"/>';
function useContractor() {
	$.getJSON("ContractorJson.action?id=" + conID,
		function(con){
			$('form [name=conAudit.contractorContact]').val(con.contact);
			$('form [name=conAudit.phone]').val(con.phone);
			$('form [name=conAudit.phone2]').val(con.email);
			$('form [name=conAudit.address]').val(con.address);
			$('form [name=conAudit.city]').val(con.city);
			$('form [name=conAudit.state]').val(con.state);
			$('form [name=conAudit.zip]').val(con.zip);
			$('form [name=conAudit.country]').val(con.country);
		});
}
</script>
</s:if>
<style type="text/css">
#auditHeader,#auditHeaderNav {
	display: none;
}

.calculatedAddress {
	display: none;
}
</style>

</head>
<body>
<s:include value="../contractors/conHeader.jsp" />

<s:form onsubmit="return submitForm();">
	<s:hidden name="auditID" />
	<fieldset class="form"><legend><span>Contact Person</span></legend>
	<ol>
		<li>Please enter your company's primary representative for this audit.</li>
		<li><label></label><input type="button" value="Use Contractor Contact Info" onclick="useContractor()"/></li>
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
	<button id="submitButton" style="display: none;" class="picsbutton positive" type="submit" name="button"
		value="address">Next &gt;&gt;</button>
	</div>
	</fieldset>
	<s:hidden id="conAudit_latitude" name="conAudit.latitude" />
	<s:hidden id="conAudit_longitude" name="conAudit.longitude" />
</s:form>

</body>
</html>