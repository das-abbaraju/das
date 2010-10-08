<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Schedule Audit</title>
<meta name="help" content="Scheduling_Audits">

<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"></s:include>

<script type="text/javascript" src="http://maps.google.com/maps?file=api&v=2.x&key=<s:property value="@com.picsauditing.actions.audits.ScheduleAudit@GOOGLE_API_KEY"/>"></script>
<script type="text/javascript" src="js/schedule_audit.js?v=<s:property value="version"/>"></script>
<s:if test="conAudit != null">
<script type="text/javascript">
var conID = '<s:property value="conAudit.contractorAccount.id"/>';
</script>
</s:if>
<style type="text/css">
#auditHeader,#auditHeaderNav {
	display: none;
}

.calculatedAddress {
}
</style>

</head>
<body>
<s:include value="../contractors/conHeader.jsp" />

<div class="info" style="clear:left">
	Please enter your company's primary representative for this audit. <br/>
	By default we have used the information for the primary contact on your account.
</div>

<s:form onsubmit="return submitForm();">
	<s:hidden name="auditID" />
	<s:hidden name="button" value="address"/>
	<fieldset class="form">
	<h2 class="formLegend">Contact Person</h2>
	<ol>
		<s:if test="permissions.admin">
			<li><a class="picsbutton" href="?button=edit&auditID=<s:property value="auditID"/>">Edit Schedule Manually</a></li>
		</s:if>
		<li><label>Name:</label> <s:textfield name="conAudit.contractorContact" value="%{conAudit.contractorAccount.primaryContact.name}" /></li>
		<li><label>Email:</label> <s:textfield name="conAudit.phone2" value="%{conAudit.contractorAccount.primaryContact.email}"/></li>
		<li><label>Phone:</label> <s:textfield name="conAudit.phone" value="%{conAudit.contractorAccount.primaryContact.phone}"/></li>
	</ol>
	</fieldset>
	<fieldset class="form">
	<h2 class="formLegend">Enter the Audit Location</h2>
	<ol>
		<li class="calculatedAddress"><label>Address:</label> <s:textfield id="conAudit_address" name="conAudit.address" size="50" value="%{conAudit.contractorAccount.address}"/> No PO Boxes</li>
		<li><label>Address 2:</label> <s:textfield id="conAudit_address2" name="conAudit.address2" value="%{conAudit.contractorAccount.address2}"/> Suite/Apartment</li>
		<li class="calculatedAddress"><label>City:</label> <s:textfield id="conAudit_city" name="conAudit.city" value="%{conAudit.contractorAccount.city}" /></li>
		<li class="calculatedAddress"><label>State/Province:</label> <s:textfield id="conAudit_state"
			name="conAudit.state" size="6" value="%{conAudit.contractorAccount.state}" /></li>
		<li><label>Zip or Postal Code:</label> <s:textfield id="conAudit_zip" name="conAudit.zip" size="10" value="%{conAudit.contractorAccount.zip}"/></li>
		<li class="calculatedAddress"><label>Country:</label> <s:select id="conAudit_country" name="conAudit.country"
			value="locale.country" list="countryList" listKey="isoCode" listValue="name"/></li>
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

</body>
</html>