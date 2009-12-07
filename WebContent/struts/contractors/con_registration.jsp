<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Contractor Registration</title>
<meta name="help" content="User_Manual_for_Contractors">
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=20091105" />
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=20091105" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=20091105" />	
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
<script type="text/javascript">	
function checkUsername(username) {
	$('#username_status').text('checking availability of username...');
	var data = {userID: 0, username: username};
	$('#username_status').load('user_ajax.jsp', data);
}

function checkTaxId(taxId) {
	startThinking({div:'taxId_status', message: ' checking availability of taxId...'});
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
	$('#state_li').load('StateListAjax.action',{'account.country': $('#contractorCountry').val()});
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
					<legend><span>Details</span></legend>
					<ol>
						<li><label>Company Name:</label>
							<s:textfield name="contractor.name" size="35" onchange="checkName(this.value);"/><span class="redMain">*</span>
							<div id="name_status"></div></li>
						<li><label>DBA Name: </label>
							<s:textfield name="contractor.dbaName" size="35" />
						</li>
						<li><label>Country:</label>
							<s:select list="countryList" id="contractorCountry"
							name="contractor.country"
							onchange="changeCountry(this.value);"
							value="locale.country"
							listKey="isoCode" listValue="english"
							/><span class="redMain">*</span></li>
						<li><label id="taxIdLabel">Tax ID:</label> <s:textfield name="contractor.taxId" id="contractorTaxId"
							size="9" maxLength="9" onchange="checkTaxId(this.value);" />
							<span class="redMain">* Only digits 0-9, no dashes</span>
							<span id="taxId_status"></span>
							</li>
						<li><label>Contact:</label>
							<s:textfield name="contractor.contact" size="35" /><span class="redMain">*</span></li>
						<li><label>Web URL:</label> 
							<s:textfield name="contractor.webUrl" size="35" />Example: www.site.com</li>
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
						<li><label>Zip:</label>
							<s:textfield name="contractor.zip" size="35" /><span class="redMain">*</span></li>
						<li><label>Phone:</label>
							<s:textfield name="contractor.phone" size="35" /><span class="redMain">*</span></li>
						<li><label>Phone 2:</label> 
							<s:textfield name="contractor.phone2" size="35" /></li>
						<li><label>Fax:</label> 
							<s:textfield name="contractor.fax" size="35" /></li>
						<li><label>Email:</label> 
							<s:textfield name="contractor.email" size="35" /><span class="redMain">* We send vital account info to this email</span></li>
					</ol>
				</fieldset>
				<fieldset class="form">
					<legend><span>Company Contacts</span></legend>
					<ol>
						<li><label>Second Contact:</label>
							<s:textfield name="contractor.secondContact" size="35" /></li>
						<li><label>Second Phone:</label>
							<s:textfield name="contractor.secondPhone" size="35" /></li>
						<li><label>Second Email:</label>
							<s:textfield name="contractor.secondEmail" size="35" /></li>
						<li><label>Billing Contact:</label> 
							<s:textfield name="contractor.billingContact" size="35" /></li>
						<li><label>Billing Phone:</label>
							<s:textfield name="contractor.billingPhone" size="35" /></li>
						<li><label>Billing Email:</label>
							<s:textfield name="contractor.billingEmail" size="35" /></li>
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
								<s:textarea name="contractor.description" cols="40" rows="15" />
								<br/>Include up to 2000 words to describe your company. 
                        		<br>
                        		<span class="blueMain">Suggestion:</span> copy and paste text from the &quot;about&quot; section 
                      			on your website or company brochure.
							</li>
						</ol>
					</fieldset>
					<fieldset class="form"><legend><span>Login Information</span></legend>
						<ol>
							<li><label>Username:</label>
						 		<s:textfield name="user.username" onblur="checkUsername(this.value);"/>
						 		<span id="username_status"></span><span class="redMain">* Please type in your desired user name</span>
						 	</li>
							<li><label>Password:</label> 
								<s:password name="password"/>
								<span class="redMain">* At least 5 characters long and different from your username</span></li>
							<li><label>Confirm Password:</label> 
								<s:password name="confirmPassword"/>
							</li>
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
