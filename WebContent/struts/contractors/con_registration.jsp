<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Contractor Registration</title>
<meta name="help" content="User_Manual_for_Contractors">
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=20091231" />
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=20091231" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=20091231" />	
<s:include value="../jquery.jsp"/>
<script type="text/javascript">	
function checkUsername(username) {
	$('#username_status').text('checking availability of username...');
	var data = {userID: 0, username: username};
	$('#username_status').load('user_ajax.jsp', data);
}

function checkTaxId(taxId) {
	startThinking({div:'taxId_status', message: ' checking availability of Tax ID...'});
	var data = {taxId: taxId, button: 'taxId', country: $('#contractorCountry').val()};
	$('#taxId_status').load('ContractorValidateAjax.action', data);
}

function checkName(name) {
	startThinking({div:'name_status', message: ' checking availability of name...'});
	var data = {companyName: name, button: 'name'};
	$('#name_status').load('ContractorValidateAjax.action', data);
}

function changeCountry(country) {
	checkTaxId($('#contractorTaxId').val());
	changeState(country);
}

function changeState(country) {
	$('#state_li').load('StateListAjax.action',{countryString: $('#contractorCountry').val(), prefix: 'contractor.'});
	$('#country_display').val($('#contractorCountry').find('option[selected]').text());
	if (country == 'US')
		$('#taxIdLabel').text('Tax ID:');
	else if (country == 'CA')
		$('#taxIdLabel').text('Business Number:');
	else
		$('#taxIdLabel').text('Tax Number:');
		
}

$(function(){
	changeState($('#contractorCountry').val());
})
</script>
</head>
<body>
<s:include value="registrationHeader.jsp"></s:include>
<span class="redMain">* - Indicates required information</span>
<s:form method="POST">
	<br clear="all" />
	<table>
		<tr>
			<td style="vertical-align: top; width: 50%;">
				<fieldset class="form">
					<legend><span>Company Details</span></legend>
					<ol>
						<li><label>Company Name:</label>
							<s:textfield name="contractor.name" size="35" onchange="checkName(this.value);"/><span class="redMain">*</span>
							<div id="name_status"></div></li>
						<li><label>DBA Name: </label>
							<s:textfield name="contractor.dbaName" size="35" />
						</li>
						<li><label>Country:</label>
							<s:select list="countryList" id="contractorCountry"
							name="contractor.country.isoCode"
							onchange="changeCountry(this.value);"
							value="locale.country"
							headerKey="" headerValue="- Country -"
							listKey="isoCode" listValue="name"
							/><span class="redMain">*</span></li>
						<li><label id="taxIdLabel">Tax ID:</label> <s:textfield name="contractor.taxId" id="contractorTaxId"
							size="9" maxLength="9" onchange="checkTaxId(this.value);" />
							<span class="redMain">* Only digits 0-9, no dashes</span>
							<span id="taxId_status"></span>
							</li>
						<li><label>Web URL:</label> 
							<s:textfield name="contractor.webUrl" size="35" />Example: www.site.com</li>
						<li><label>Phone:</label>
							<s:textfield name="contractor.phone" size="20" /><span class="redMain">*</span></li>
						<li><label>Fax:</label>
							<s:textfield name="contractor.fax" size="20" /></li>
					</ol>
				</fieldset>
				<fieldset class="form">
					<legend><span>Primary Address</span></legend>
					<ol>
						<li><label>Address:</label>
							<s:textfield name="contractor.address" size="35" /><span class="redMain">*</span></li>
						<li><label>City:</label> 
							<s:textfield name="contractor.city" size="35" /><span class="redMain">*</span></li>
						<li><label>Country:</label> <input type="text" disabled="disabled" id="country_display"/></li>
						<li id="state_li">
						</li>
						<li><label>Zip/Postal Code:</label>
							<s:textfield name="contractor.zip" size="10" /><span class="redMain">*</span></li>
					</ol>
				</fieldset>
				<fieldset class="form">
					<legend><span>Industry Details</span></legend>
					<ol>
						<li><label>Industry:</label> <s:select list="industryList"
							name="contractor.industry" /></li>
						<li><label>Main Trade:</label> <s:select list="tradeList"
							name="contractor.mainTrade" headerKey=""
							headerValue="- Choose a trade -" listKey="question"
							listValue="question" /><span class="redMain">*</span></li>
					</ol>
				</fieldset>
				<fieldset class="form">
					<legend><span>Company Identification</span></legend>
					<ol>
						<li><label>Description:</label>
							<s:textarea name="contractor.description" cols="60" rows="15" />
							<br/>Include up to 2000 words to describe your company. 
                       		<br>
                       		<span class="blueMain">Suggestion:</span> Copy and paste text from the &quot;About&quot; section 
                     			on your web site or company brochure.
						</li>
					</ol>
				</fieldset>
				<fieldset class="form">
					<legend><span>Primary Contact</span></legend>
					<ol>
						<li><label>Name:</label>
							<s:textfield name="user.name" size="20" /><span class="redMain">*</span></li>
						<li><label>Email:</label>
							<s:textfield name="user.email" size="35" /><span class="redMain">* We send vital information to this email</span></li>
						<li><label>Username:</label>
					 		<s:textfield name="user.username" onchange="checkUsername(this.value);"/>
					 		<span id="username_status"></span><span class="redMain">* Please type in your desired user name</span>
					 	</li>
						<li><label>Password:</label> 
							<s:password name="password"/>
							<span class="redMain">* At least 5 characters long and different from your username</span></li>
						<li><label>Confirm Password:</label> 
							<s:password name="confirmPassword"/>
						</li>
						<li>You'll have the opportunity to create more users later.</li>
					</ol>
				</fieldset>
				<fieldset class="form submit">
					<div>
						<input type="submit" class="picsbutton positive" name="button" value="Create Account"/>
					</div>
				</fieldset>
			</td>
		</tr>
	</table>
</s:form>
</body>
</html>
