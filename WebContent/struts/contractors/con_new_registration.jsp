<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<html>
<head>
<title>Request New Contractor</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css" />
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css" />
<style type="text/css">
#operatorForms {
	overflow: auto;
	background-color: white;
}

#hidden #operatorForms {
	display: none;
}

#attachment {
	display: inline-block;
	vertical-align: top;
}
</style>
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/autocomplete/jquery.autocomplete.min.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	$('.fancybox').fancybox();
});

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

function addAttachment(formName, filename) {
	$.fancybox.close();
	var id = filename.substring(0, filename.indexOf('.'));
	
	var attachment = '<span id="' + id + '"><a href="#" class="remove" onclick="removeAttachment(\'' + id
		+ '\'); return false;">' + formName + '</a><input type="hidden" id="' + id + '_input" name="filenames" value="'
		+ filename + '" /><br /></span>';
	
	$('#attachment').append(attachment);
	$('#'+id+'_input').val(filename);
}

function removeAttachment(id) {
	$('span#'+id).remove();
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
				<input type="submit" class="picsbutton" name="button" value="Contacted By Phone" />
			</s:if>
		</li>
		<li><label for="email">Email:</label>
			<s:textfield name="newContractor.email" size="30" id="email" />
			<s:if test="newContractor.id > 0 && newContractor.email.length() > 0">
				<input type="submit" value="Send Email" name="button" class="picsbutton" />
				<s:if test="formsViewable && attachment == null && forms.size() > 0">
					<a href="#operatorForms" class="picsbutton fancybox" title="Add Attachment" onclick="return false;">Add Attachment</a>
					<div id="attachment" style="width: 350px"></div>
				</s:if>
			</s:if>
		</li>
		<li><label for="taxID">Tax ID:</label>
			<s:textfield name="newContractor.taxID" size="9" maxLength="9" id="taxID" /></li>
		<s:if test="assignedCSR != null">
			<li><label>Assigned PICS CSR:</label>
				<s:property value="assignedCSR.name" /> / <s:property value="assignedCSR.phone"/>
			</li>
		</s:if>	
	</ol>
	</fieldset>
	<fieldset class="form"><legend><span>Primary Address</span></legend>
	<ol>
		<li><label for="address">Address:</label>
			<s:textfield name="newContractor.address" size="35" id="address" /></li>
		<li><label for="city">City:</label><s:textfield name="newContractor.city" size="20" id="city" /></li>
		<li><label for="zip">Zip:</label><s:textfield name="newContractor.zip" size="7" id="zip" /></li>
		<li><label for="newContractorCountry">Country:</label>
			<s:select
				list="countryList" name="country.isoCode" id="newContractorCountry"
				listKey="isoCode" listValue="name" value="%{newContractor.country.isoCode}"
				onchange="countryChanged(this.value)" />
			<span class="redMain">*</span></li>
		<li id="state_li"></li>
	</ol>
	</fieldset>
	<fieldset class="form"><legend><span>User Information</span></legend>
	<ol>
		<li><label>Requested By Account:</label>
			<s:select list="operatorsWithCorporate" headerKey="0" headerValue="- Select a Operator -"
				name="requestedOperator" onchange="updateUsersList();" listKey="id" listValue="name"
				value="%{newContractor.requestedBy.id}" />
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
			<li><label>Last Contacted By:</label>
				<s:property value="newContractor.lastContactedBy.name" /><br /></li>
			<li><label>Date	Contacted:</label>
				<s:date name="newContractor.lastContactDate" format="MM/dd/yyyy" /><br /></li>
		</s:if>
		<li><label>Notes:</label>
			<s:textarea cssStyle="vertical-align: top" name="newContractor.notes"
				cols="20" rows="3" /></li>
		<li><label>Who should follow up?:</label>
			<s:radio list="#{'PICS':'PICS','Operator':'Operator'}" name="newContractor.handledBy" theme="pics"/>
		</li>
		<s:if test="newContractor.id > 0">
			<li><label># of Times Contacted:</label>
				<s:property value="newContractor.contactCount"/></li>
			<li><label>Matches Found in PICS:</label>
				<s:property value="newContractor.matchCount"/></li>
			<li><label>PICS Contractor ID:</label>
				<s:if test="permissions.admin">
					<s:textfield name="conID" value="%{newContractor.contractor.id}" size="7" />
				</s:if>
				<s:if test="conAccount != null">
					<a href="ContractorView.action?id=<s:property value="conAccount.id"/>">
					<s:property value="conAccount.name"/></a>
				</s:if>
			</li>
		</s:if>
	</ol>
	</fieldset>
	<fieldset class="form submit">
	 <div>	
	  	<input type="submit" class="picsbutton positive" name="button" value="Save" />
	  	<s:if test="newContractor.id > 0">
		  	<input type="submit" class="picsbutton negative" name="button" value="Close Request" />
		</s:if>
	</div>	
	</fieldset>
</s:form>

<div style="display: none" id="load"></div>

<s:if test="formsViewable && forms.size() > 0">
<div id="hidden"><div id="operatorForms">
	<table class="report">
		<thead>
			<tr>
				<th colspan="2">Forms</th>
				<th>Facility</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="forms" status="stat">
				<tr>
					<td class="right"><s:property value="#stat.index + 1" /></td>
					<td><a href="#" onclick="addAttachment('<s:property value="formName" />','<s:property value="file" />'); return false;">
						<s:property value="formName" /></a></td>
					<td><s:property value="account.name" /></td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</div></div>
</s:if>

</body>
</html>
