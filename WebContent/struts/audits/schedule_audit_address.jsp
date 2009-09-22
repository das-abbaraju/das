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
<s:if test="conAudit != null">
<script type="text/javascript">
var contractor = {
	'conAudit.contractorContact': '<s:property value="conAudit.contractorAccount.contact"/>',
	'conAudit.phone2': '<s:property value="conAudit.contractorAccount.email"/>',
	'conAudit.phone': '<s:property value="conAudit.contractorAccount.phone"/>',
	'conAudit.address': '<s:property value="conAudit.contractorAccount.address"/>',
	'conAudit.city': '<s:property value="conAudit.contractorAccount.city"/>',
	'conAudit.state': '<s:property value="conAudit.contractorAccount.state"/>',
	'conAudit.zip': '<s:property value="conAudit.contractorAccount.zip"/>',
	'conAudit.country': '<s:property value="conAudit.contractorAccount.country"/>'
};

function useContractor() {
	$.each(contractor, function(k,v) {
		$('form [name='+k+']').val(v);
	});
}
</script>
</s:if>
<s:include value="../jquery.jsp"></s:include>

<script type="text/javascript"
	src="http://maps.google.com/maps?file=api&v=2.x&key=ABQIAAAAzr2EBOXUKnm_jVnk0OJI7xSosDVG8KKPE1-m51RBrvYughuyMxQ-i1QfUnH94QxWIa6N4U6MouMmBA"></script>
<script type="text/javascript" src="js/schedule_audit.js"></script>

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