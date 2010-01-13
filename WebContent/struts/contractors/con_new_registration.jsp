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
$(function() {
	changeState($("#newContractorCountry").val());
	$('.datepicker').datepicker();
});

function countryChanged(country) {
	changeState(country);
}

function changeState(country) {
	$('#state_li').load('StateListAjax.action',{countryString: $('#newContractorCountry').val(), stateString: '<s:property value="newContractor.state.isoCode"/>'});
}
</script>
</head>
<body>
<h1>Request New Contractor</h1>

<s:form id="saveContractorForm">
	<s:hidden name="newContractor.id" />
	<fieldset class="form"><legend><span>Details</span></legend>
	<ol>
		<li><label>Company Name:</label>
			<s:textfield name="newContractor.name" size="35"/></li>
		<li><label>Contact
			Name:</label> <s:textfield name="newContractor.contact" /></li>
		<li><label>Phone:</label>
			<s:textfield name="newContractor.phone" size="20" />
			<input type="submit"
			class="picsbutton positive" name="button" value="Contacted By Phone" />
		</li>
		<li><label for="saveContractorForm_newContractor_email">Email:</label>
			 <s:textfield name="newContractor.email" size="30" />
			<input type="submit" class="picsbutton positive"
			name="button" value="Send Email" />
		</li>
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
		<li><label for="saveContractorForm_newContractor_zip">Zip:</label>
			<s:textfield name="newContractor.zip" size="7" /></li>
		<li><label for="newContractorCountry">Country:</label> <s:select
			list="countryList" name="country.isoCode" id="newContractorCountry"
			listKey="isoCode" listValue="name"
			value="newContractor.country.isoCode"
			onchange="countryChanged(this.value)" /></li>
		<li id="state_li"></li>
	</ol>
	</fieldset>
	<fieldset class="form"><legend><span>User
		Information</span></legend>
	<ol>
		<li><label>Requested
			By Account:</label><s:select list="operatorsWithCorporate" headerKey="" headerValue="- Select a Operator -" name="newContractor.requestedBy" value="%{newContractor.requestedBy.id}" listKey="id" listValue="name"/>
		</li>
		<li><label>Requested
			By User:</label>
			<s:select list="usersList" listKey="id" listValue="name" name="newContractor.requestedByUser" value="%{newContractor.requestedByUser.id}" headerKey="0" headerValue="- Other-"/> 
			<s:textfield name="newContractor.requestedByUserOther" size="20" /></li>
		<li><label>Deadline
			Date:</label> <input name="newContractor.deadline" type="text"
			class="forms datepicker" size="10"
			value="<s:date name="newContractor.deadline" format="MM/dd/yyyy" />" />
		</li>
		<li><label>Last
			Contacted By:</label> <s:property value="newContractor.lastContactedBy.name"
			/></li>
		<li><label>Date
			Contacted:</label><s:date name="newContractor.lastContactDate" format="MM/dd/yyyy" />
		</li>
		<li><label>Notes:</label>
			<s:textarea cssStyle="vertical-align: top" name="newContractor.notes"
				cols="40" rows="10" /></li>
		<li><label>#
			of Times Contacted:</label><s:property value="newContractor.contactCount"/></li>
		<li><label>Handled By:</label> 
			This contractor will be contacted by a PICS Customer Service Representative.
			<br/>Click here to 
			<input type="submit"
			class="picsbutton positive" name="button"
			value="Handle This Account" /> <input type="hidden"
			class="picsbutton positive" name="button"
			value="Let PICS Handle This Account" /></li>
		<li><label>Matches Found in PICS:</label>
			<s:property value="newContractor.matchCount"/></li>
		<li><label>Linked
			in PICS:</label><s:textfield name="newContractor.contractor.id" size="7" /></li>
	</ol>
	</fieldset>
	<fieldset class="form submit">
	 <div>	
	  	<input
		type="submit" class="picsbutton positive" name="button" value="Save" />
	  	<input
		type="submit" class="picsbutton negative" name="button" value="Close Account" />
	</div>	
	</fieldset>
</s:form>

</body>
</html>
