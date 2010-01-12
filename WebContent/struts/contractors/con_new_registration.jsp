<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<html>
<head>
<title>Request New Contractor</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css?v=20091231" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css?v=20091231" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/notes.css" />
<s:include value="../jquery.jsp" />
<script type="text/javascript">
$(document).ready(function(){
    $(".datepicker").datepicker();
  });

function changeState(country) {
	$('#state_li').load('StateListAjax.action',{countryString: $('#newContractorCountry').val(), stateString: '<s:property value="newContractor.state.isoCode"/>'});
}
</script>
</head>
<body>

<s:form id="saveContractorForm">
	<s:hidden name="newContractor.id" />
	<fieldset class="form"><legend><span>Details</span></legend>
	<ol>
		<li><label for="saveContractorForm_newContractor_name">Name:</label>
			<s:textfield name="newContractor.name" /></li>
		<li><label for="saveContractorForm_newContractor_contact">Contact
			name:</label> <s:textfield name="newContractor.contact" /></li>
		<li><label for="saveContractorForm_newContractor_phone">Phone:</label>
			<s:textfield name="newContractor.phone" size="20" /></li>
		<li><label for="saveContractorForm_newContractor_email">Email
			address:</label> <s:textfield name="newContractor.email" size="30" /></li>
		<li><label for="saveContractorForm_newContractor_taxID">Tax
			ID:</label> <s:textfield name="newContractor.taxID" size="20" /></li>
	</ol>
	</fieldset>
	<fieldset class="form"><legend><span>Primary
		Address</span></legend>
	<ol>
		<li><label for="saveContractorForm_newContractor_address">Address:</label>
			<s:textfield name="newContractor.address" size="35" /></li>
		<li><label for="saveContractorForm_newContractor_city">City:</label>
			<s:textfield name="newContractor.city" size="20" /></li>
		<li><label for="saveContractorForm_newContractor_state_isoCode">State:</label>
			<s:select list="getStateList()" id="state_sel"
				name="newContractor.state.isoCode" listKey="isoCode" listValue="name"
				value="stateString" /></li>
		<li><label for="saveContractorForm_newContractor_zip">Zip:</label>
			<s:textfield name="newContractor.zip" size="7" /></li>
		<li><label for="newContractorCountry">Country:</label> <s:select
			list="countryList" name="country.isoCode" id="newContractorCountry"
			listKey="isoCode" listValue="name"
			value="newContractor.country.isoCode"
			onchange="countryChanged(this.value)" /></li>
	</ol>
	</fieldset>
	<fieldset class="form"><legend><span>User
		Information</span></legend>
	<ol>
		<li><label
			for="saveContractorForm_newContractor_requestedBy_name">Requested
			By Account:</label> <s:textfield name="newContractor.requestedBy.name"
			size="35" /></li>
		<li><label
			for="saveContractorForm_newContractor_requestedByUserOther">Requested
			By User:</label> <s:textfield name="newContractor.requestedByUserOther"
			size="20" /></li>
		<li><label for="saveContractorForm_newContractor_deadline">Deadline
			Date:</label> <input name="newContractor.deadline" type="text"
			class="forms datepicker" size="10"
			value="<s:date name="newContractor.deadline" format="MM/dd/yyyy" />" />
		</li>
		<li><label
			for="saveContractorForm_newContractor_lastContactedBy_name">Last
			Contacted By:</label> <s:textfield name="newContractor.lastContactedBy.name"
			size="7" /></li>
		<li><label
			for="saveContractorForm_newContractor_lastContactedDate">Date
			Contacted:</label> <input name="newContractor.lastContactedDate" type="text"
			class="forms datepicker" size="10"
			value="<s:date name="newContractor.lastContactedDate" format="MM/dd/yyyy" />" />
		</li>
		<li><label for="saveContractorForm_newContractor_notes">Notes:</label>
			<s:textarea cssStyle="vertical-align: top" name="newContractor.notes"
				cols="40" rows="10" /></li>
		<li><label for="saveContractorForm_newContractor_contactCount">#
			of Times Contacted:</label> <s:textfield name="newContractor.contactCount"
				size="7" /></li>
		<li><label>Handled By:</label> <input type="submit"
			class="picsbutton positive" name="button"
			value="Let Operator Handle This Account" /> <input type="hidden"
			class="picsbutton positive" name="button"
			value="Let PICS Handle This Account" /></li>
		<li><label for="saveContractorForm_newContractor_matchCount">Matches
			Found in PICS:</label> <s:textfield name="newContractor.matchCount" size="7" /></li>
		<li><label for="saveContractorForm_newContractor_contractor_name">Linked
			in PICS:</label> <s:textfield name="newContractor.contractor.name" size="7" /></li>
	</ol>
	</fieldset>
	<fieldset class="form submit">
	<div><input type="submit" class="picsbutton positive"
		name="button" value="Send Email" /> <input type="submit"
		class="picsbutton positive" name="button" value="Contacted" /> <input
		type="submit" class="picsbutton positive" name="button" value="Save" />
	</div>
	</fieldset>
</s:form>

</body>
</html>
