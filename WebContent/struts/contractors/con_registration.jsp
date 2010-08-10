<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Contractor Registration</title>
<meta name="help" content="User_Manual_for_Contractors">
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />	
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
	if (name.indexOf('^^^') > 0) {
		$('#name_status').html("This contractor will be created as a DEMO account.");
		$('#contractorTaxId').val('000000000');
		$('#contractorPhone').val('949-387-1940');
		return;
	}
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

// Probably a better way to do this
function updateHeader() {
	if ($('input#materialSupplier').is(':checked')) {
		$('#step2').hide();
		$('span.vendor').show();
		$('span.service').hide();
	} else {
		$('#step2').show();
		$('span.vendor').hide();
		$('span.service').show();
	}
}

$(function(){
	changeState($('#contractorCountry').val());
})
</script>
</head>
<body>
<s:include value="registrationHeader.jsp"></s:include>
<span class="redMain required-info">Indicates required information</span>
<s:form method="POST">
<s:hidden name="requestID" />
	<br clear="all" />
	<table>
		<tr>
			<td style="vertical-align: top; width: 50%;">
				<fieldset class="form">
					<h2 class="formLegend">Company Details</h2>
					<ol>
						<li class="required"><label>Company Name:</label>
							<s:textfield name="contractor.name" size="35" onchange="checkName(this.value);"/>
							<div class="fieldhelp">
								<h3>Company Name</h3>
								<p>The name of your company. We'll check this name against our database to see if you or one of your colleagues has already registered your company.</p>
							</div>
							<div id="name_status"></div>
						</li>
						<li><label>DBA Name: </label>
							<s:textfield name="contractor.dbaName" size="35" />
							<div class="fieldhelp">
								<h3>DBA Name</h3>
								<p>An alternative (also known as Doing Business As) name of your company. This is optional but may help your customers search for you if they only know you by your DBA name.</p>
							</div>
						</li>
						<li class="required"><label>Country:</label>
							<s:select list="countryList" id="contractorCountry"
								name="contractor.country.isoCode"
								onchange="changeCountry(this.value);"
								headerKey="" headerValue="- Country -"
								listKey="isoCode" listValue="name" />
							<div class="fieldhelp">
								<h3>Country</h3>
								<p>The global corporate headquarters of your company.</p>
							</div>
						</li>
						<li class="required"><label id="taxIdLabel">Tax ID:</label>
							<s:textfield name="contractor.taxId" id="contractorTaxId"
								size="9" maxLength="9" onchange="checkTaxId(this.value);" />
							<div class="fieldhelp">
								<h3>Tax ID or Business Number</h3>
								<p>The number your government uses to uniquely identify your company. We use this to see if your company has already registered.</p>
								<h5>United States</h5>
								9-digit number with no dashes
								<h5>Canada</h5>
								the first 9-digits of your 15 character Business Number
								
							</div>
							<div id="taxId_status"></div>
						</li>
						<li><label>Web URL:</label> 
							<s:textfield name="contractor.webUrl" size="35" />
							<div class="fieldhelp">
								<h3>Web URL</h3>
								The web site your customers can go to to learn more about your products and services.
								<h5>Example:</h5>
								www.yourcompany.com
							</div>
						</li>
						<li class="required"><label>Company Phone:</label>
							<s:textfield id="contractorPhone" name="contractor.phone" size="20" />
							<div class="fieldhelp">
								<h3>Company Phone</h3>
								Your company's primary telephone line.
							</div>
						</li>
						<li><label>Company Fax:</label>
							<s:textfield name="contractor.fax" size="20" />
							<div class="fieldhelp">
								<h3>Company Fax</h3>
								Your company's primary fax line if any.
							</div>
						</li>
					</ol>
				</fieldset>
				<fieldset class="form">
					<h2 class="formLegend">Primary Address</h2>
					<ol>
						<li class="required"><label>Address:</label>
							<s:textfield name="contractor.address" size="35" />
							<div class="fieldhelp">
								<h3>Primary Address</h3>
								Your company's primary address or headquarters. This should be located in the country you selected in Company Details above.
								<h5>Example:</h5>
								123 Main Street, Suite 100
							</div>
						</li>
						<li class="required"><label>City:</label> 
							<s:textfield name="contractor.city" size="35" />
						</li>
						<li><label>Country:</label>
							<input type="text" disabled="disabled" id="country_display"/>
						</li>
						<s:if test="contractor == null || contractor.state == null"><li id="state_li"></li></s:if>
						<s:else>
							<li class="required"><label>State:</label>
								<s:select list="stateList" id="state_sel"
									name="contractor.state.isoCode"
									headerKey="" headerValue="- State -"
									listKey="isoCode" listValue="name" />
							</li>
						</s:else>
						<li class="required"><label>Zip/Postal Code:</label>
							<s:textfield name="contractor.zip" size="10" />
						</li>
					</ol>
				</fieldset>
				<fieldset class="form">
					<h2 class="formLegend">Industry Details</h2>
					<ol>
						<li><label>Industry:</label>
							<s:select list="industryList" name="contractor.industry" />
							<div class="fieldhelp">
								<h3>Industry</h3>
								The main industry your company works in. If your industry is not listed, then choose General.
							</div>
						</li>
						<li class="required"><label>Main Trade:</label>
							<s:select list="tradeList"
								name="contractor.mainTrade" headerKey="" headerValue="- Choose a trade -" 
								listKey="name" listValue="name" />
							<div class="fieldhelp">
								<h3>Trade</h3>
								This is your company's primary trade. You will have the opportunity to select additional trades later on.
							</div>
						</li>
						<s:iterator value="@com.picsauditing.jpa.entities.ContractorType@values()" id="conType">
							<li class="required"><label><s:property value="#conType.type" />:</label>
								<s:if test="#conType == 'Onsite'">
									<s:checkbox name="contractor.onsiteService" />
								</s:if>
								<s:elseif test="#conType == 'Offsite'">
									<s:checkbox name="contractor.offsiteService" />
								</s:elseif>
								<s:else>
									<s:checkbox name="contractor.materialSupplier" onclick="updateHeader();" id="materialSupplier" />
								</s:else>
								<div class="fieldhelp">
									<h3><s:property value="#conType.type" /></h3>
									<s:property value="#conType.description" />
								</div>
							</li>
						</s:iterator>
					</ol>
				</fieldset>
				<fieldset class="form">
					<h2 class="formLegend">Company Identification</h2>
					<ol>
						<li><label>Description:</label>
							<s:textarea name="contractor.description" cols="60" rows="15" />
							<div class="fieldhelp">
								<h3>Description</h3>
								<p>Include up to 2000 words to describe your company.</p>
								<h5>Suggestion:</h5>
								<p>Copy and paste text from the &quot;About&quot; section on your web site or company brochure.</p>
							</div>
						</li>
					</ol>
				</fieldset>
				<fieldset class="form">
					<h2 class="formLegend">Primary Contact</h2>
					<ol>
						<li class="required"><label>Name:</label>
							<s:textfield name="user.name" size="20" />
							<div class="fieldhelp">
								<h3>Name</h3>
								This your name. We use the information in this section to create the first administrator user.
								You'll have the opportunity to create more users later with varying levels of permissions or change the primary contact to another user.
								<h5>Example</h5>
								John Doe
							</div>
						</li>
						<li class="required"><label>Email:</label>
							<s:textfield name="user.email" size="20" /> 
							<div class="fieldhelp">
								<h3>Email</h3>
								Your email address. We send vital information to this address, so it needs to be correct. Your customers that use PICS will be able to see primary contact(s) and their phone and email address.
							</div>
						</li>
						<li><label>Phone:</label>
							<s:textfield name="user.phone" size="20" />
						</li>
						<li><label>Fax:</label>
							<s:textfield name="user.fax" size="20" />
						</li>
						<li class="required"><label>Username:</label>
					 		<s:textfield name="user.username" onchange="checkUsername(this.value);"/>
					 		<div class="fieldhelp">
					 			<h3>Username</h3>
					 			<p>Please type in your desired user name. We'll let you know if it's available.</p>
					 			<ul>
					 				<li>Must be at least 3 characters long</li>
					 				<li>Don't use spaces in your name</li>
					 				<li>Your email address is recommended because it's easy to remember.</li>
					 			</ul>
					 		</div>
					 		<br />
					 		<div id="username_status"></div>
					 	</li>
						<li class="required"><label>Password:</label> 
							<s:password name="password"/>
							<div class="fieldhelp">
								<h3>Password</h3>
								Must be at least 5 characters long and different from your username
							</div>
						</li>
						<li><label>Confirm Password:</label> 
							<s:password name="confirmPassword"/>
						</li>
						<li><b>
							By clicking <span style="color: #529214; font-family: 'Lucida Grande', Tahoma, Arial">[Create Account]</span> below, I certify that I agree to the 
							terms and conditions of the <a href="#"
								onClick="window.open('contractor_agreement.jsp','name','toolbar=0,scrollbars=1,location=0,statusbar=0,menubar=0,resizable=1,width=700,height=700'); return false;"
								class="ext">PICS Contractor Agreement</a>.</b>
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
