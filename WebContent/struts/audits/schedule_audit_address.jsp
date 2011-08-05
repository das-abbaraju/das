<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="%{scope}.title" /></title>
<meta name="help" content="Scheduling_Audits">

<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"></s:include>

<script type="text/javascript" src="http://maps.google.com/maps?file=api&v=2.x&key=<s:property value="@com.picsauditing.actions.audits.ScheduleAudit@GOOGLE_API_KEY"/>"></script>
<script type="text/javascript" src="js/schedule_audit.js?v=<s:property value="version"/>"></script>
<s:if test="conAudit != null">
<script type="text/javascript">
var conID = '<s:property value="conAudit.contractorAccount.id"/>';
$(function() {
	$('#verifyButton').click(function(e) {
		e.preventDefault();
		verifyAddress();
	});
});
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

<div class="info" style="clear:left"><s:text name="%{scope}.message.EnterPrimaryRepresentative" /></div>

<s:form onsubmit="return submitForm();">
	<s:hidden name="auditID" />
	<fieldset class="form">
	<h2 class="formLegend"><s:text name="%{scope}.label.ContactPerson" /></h2>
	<ol>
		<s:if test="permissions.admin">
			<li>
				<s:submit method="edit" cssClass="picsbutton" value="%{getText(scope + '.button.EditScheduleManually')}" /> 
			</li>
		</s:if>
		<li><label><s:text name="User.name" />:</label> <s:textfield name="conAudit.contractorContact" value="%{conAudit.contractorAccount.primaryContact.name}" /></li>
		<li><label><s:text name="User.email" />:</label> <s:textfield name="conAudit.phone2" value="%{conAudit.contractorAccount.primaryContact.email}"/></li>
		<li><label><s:text name="User.phone" />:</label> <s:textfield name="conAudit.phone" value="%{conAudit.contractorAccount.primaryContact.phone}"/></li>
	</ol>
	</fieldset>
	<fieldset class="form">
	<h2 class="formLegend"><s:text name="%{scope}.label.EnterAuditLocation" /></h2>
	<ol>
		<li class="calculatedAddress"><label><s:text name="global.Address" />:</label> <s:textfield id="conAudit_address" name="conAudit.address" size="50" value="%{conAudit.contractorAccount.address}"/> <s:text name="%{scope}.message.NoPOBoxes" /></li>
		<li><label><s:text name="global.Address" /> 2:</label> <s:textfield id="conAudit_address2" name="conAudit.address2" value="%{conAudit.contractorAccount.address2}"/> <s:text name="%{scope}.message.SuiteApartment" /></li>
		<li class="calculatedAddress"><label><s:text name="global.City" />:</label> <s:textfield id="conAudit_city" name="conAudit.city" value="%{conAudit.contractorAccount.city}" /></li>
		<li class="calculatedAddress"><label><s:text name="State" />:</label> <s:textfield id="conAudit_state"
			name="conAudit.state" size="6" value="%{conAudit.contractorAccount.state}" /></li>
		<li><label><s:text name="global.ZipPostalCode" />:</label> <s:textfield id="conAudit_zip" name="conAudit.zip" size="10" value="%{conAudit.contractorAccount.zip}"/></li>
		<li class="calculatedAddress"><label><s:text name="Country" />:</label> <s:select id="conAudit_country" name="conAudit.country"
			value="locale.country" list="countryList" listKey="isoCode" listValue="name"/></li>
		<li id="unverifiedLI" style="display: none;"><s:checkbox id="unverifiedCheckbox"
			onchange="$('#submitButton').toggle()" name="unverifiedCheckbox"></s:checkbox> <s:text name="%{scope}.message.AddressIsCorrect" /></li>
	</ol>
	</fieldset>
	<fieldset class="form submit">
	<div id="mainThinkingDiv"></div>
	<div>
	<button id="verifyButton" class="picsbutton" type="button"><s:text name="%{scope}.button.VerifyAddress" /></button>
	<s:submit id="submitButton" cssStyle="display: none;" cssClass="picsbutton positive" method="address" value="%{getText('button.Next') + ' >>'}" />
	</div>
	</fieldset>
	<s:hidden id="conAudit_latitude" name="conAudit.latitude" />
	<s:hidden id="conAudit_longitude" name="conAudit.longitude" />
</s:form>

</body>
</html>