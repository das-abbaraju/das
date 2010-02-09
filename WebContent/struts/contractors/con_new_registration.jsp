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
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/autocomplete/jquery.autocomplete.min.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css" />
<script type="text/javascript">
$(function() {
	changeState($("#newContractorCountry").val());
	$('.datepicker').datepicker();
	$('#newContractorName').autocomplete('ContractorSelectAjax.action', 
		{
			minChars: 3,
			extraParams: {'filter.accountName': function() {return $('#newContractorName').val();} }
		}
	);
});

function countryChanged(country) {
	changeState(country);
}

function changeState(country) {
	$('#state_li').load('StateListAjax.action',{countryString: $('#newContractorCountry').val(), stateString: '<s:property value="newContractor.state.isoCode"/>'});
}

function updateUsersList() {
	$('#loadUsersList').load('OperatorUserListAjax.action',{opID: $('#saveContractorForm_requestedOperator').val()});
}

function checkUserOther() {
	if ($("#requestedUser").val() == 0)
		$("#requestedByOtherUser").show();
	else
		$("#requestedByOtherUser").hide();
}
</script>
</head>
<body>
<h1>Request New Contractor</h1>
<span class="redMain">* - Indicates required information</span>

<s:include value="../actionMessages.jsp"></s:include>

<s:if test="conAccount != null && conAccount.status.active">
	<div class="info">This contractor has registered an account with PICS on <strong><s:date name="conAccount.creationDate" format="M/d/yyyy" /></strong><br/>
		Click here to <a href="RequestNewContractor.action?requestID=<s:property value="newContractor.id" />&button=Close Request" class="picsbutton positive">Close the Request</a>.</div>
</s:if>


<s:form id="saveContractorForm">
	<s:hidden name="requestID"/>
	<fieldset class="form"><legend><span>Details</span></legend>
	<ol>
		<li><label>Company Name:</label>
			<s:textfield name="newContractor.name" size="35" id="newContractorName" /><span class="redMain">*</span></li>
		<li><label>Contact
			Name:</label> <s:textfield name="newContractor.contact" /><span class="redMain">*</span></li>
		<li><label>Phone:</label>
			<s:textfield name="newContractor.phone" size="20" /><span class="redMain">*</span>
			<s:if test="newContractor.id > 0">
				<input type="submit"
				class="picsbutton positive" name="button" value="Contacted By Phone" />
			</s:if>
		</li>
		<li><label for="saveContractorForm_newContractor_email">Email:</label>
			 <s:textfield name="newContractor.email" size="30" />
			<s:if test="newContractor.id > 0 && newContractor.email.length() > 0">
				<input type="submit" class="picsbutton positive"
				name="button" value="Send Email" />
			</s:if>
		</li>
		<li><label for="saveContractorForm_newContractor_taxID">Tax
			ID:</label> <s:textfield name="newContractor.taxID" size="9" maxLength="9" /></li>
		<s:if test="assignedCSR != null">
			<li><label>Assigned PICS CSR:</label>
				<s:property value="assignedCSR.name" /> / <s:property value="assignedCSR.phone"/>
			</li>
		</s:if>	
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
			value="%{newContractor.country.isoCode}"
			onchange="countryChanged(this.value)" /><span class="redMain">*</span></li>
		<li id="state_li"></li>
	</ol>
	</fieldset>
	<fieldset class="form"><legend><span>User
		Information</span></legend>
	<ol>
		<li><label>Requested
			By Account:</label><s:select list="operatorsWithCorporate" headerKey="0" 
				headerValue="- Select a Operator -" name="requestedOperator" 
				value="%{newContractor.requestedBy.id}" listKey="id" listValue="name" 
				onchange="updateUsersList();" />
			<span class="redMain">*</span>
		</li>
		<s:if test="newContractor.requestedByUser != null || newContractor.requestedByUserOther != null">
			<li id="loadUsersList">
				<label>Requested By User:</label>
				<s:select list="getUsersList(newContractor.requestedBy.id)" listKey="id" listValue="name"
					id="requestedUser" name="requestedUser" value="%{newContractor.requestedByUser.id}"
					headerKey="0" headerValue="- Other -" onclick="checkUserOther();" />
				<span class="redMain">*</span>
				<input type="text" name="newContractor.requestedByUserOther" id="requestedByOtherUser" size="20"
					<s:if test="newContractor.requestedByUser != null && newContractor.requestedBy.users != null">style="display:none;"</s:if>
					value="<s:property value="newContractor.requestedByUserOther" />" />
			</li>
		</s:if>
		<s:else>
			<li id="loadUsersList"></li>
		</s:else>
		<li><label>Registration Deadline:</label> <input name="newContractor.deadline" type="text"
			class="forms datepicker" size="10"
			value="<s:date name="newContractor.deadline" format="MM/dd/yyyy" />" />
		</li>
		<s:if test="newContractor.id > 0">
			<li><label>Last
				Contacted By:</label> <s:property value="newContractor.lastContactedBy.name"
				/><br /></li>
			<li><label>Date
				Contacted:</label><s:date name="newContractor.lastContactDate" format="MM/dd/yyyy" />
				<br />
			</li>
		</s:if>
		<li><label>Notes:</label>
			<s:textarea cssStyle="vertical-align: top" name="newContractor.notes"
				cols="20" rows="3" /></li>
		<li><label>Who should follow up?:</label>
			<s:radio list="#{'PICS':'PICS','Operator':'Operator'}" name="newContractor.handledBy" theme="pics"/>
		</li>
		<s:if test="newContractor.id > 0">
			<li><label>#
				of Times Contacted:</label><s:property value="newContractor.contactCount"/></li>
		<li><label>Matches Found in PICS:</label>
			<s:property value="newContractor.matchCount"/></li>
		<li><label>PICS Contractor ID:</label>
			<s:if test="permissions.admin">
				<s:textfield name="conID" value="%{newContractor.contractor.id}" size="7" />
			</s:if>
			<s:if test="conAccount != null">
				<a href="ContractorView.action?id=<s:property value="conAccount.id"/>"><s:property value="conAccount.name"/></a>
			</s:if>
		</li>
		</s:if>
	</ol>
	</fieldset>
	<fieldset class="form submit">
	 <div>	
	  	<input
		type="submit" class="picsbutton positive" name="button" value="Save" />
	  	<s:if test="newContractor.id > 0">
		  	<input
			type="submit" class="picsbutton negative" name="button" value="Close Request" />
		</s:if>
	</div>	
	</fieldset>
</s:form>

</body>
</html>
