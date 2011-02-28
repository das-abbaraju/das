<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title><s:text name="%{scope}.title" /></title>
<meta name="help" content="User_Manual_for_Contractors">
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />	
<style type="text/css">
#locales {
	position: relative;
	top: -30px
}
#locales a, #locales a:VISITED, #locales a:HOVER, #locales a:ACTIVE {
	margin: 15px;
	padding: 5px;
	text-decoration: none;
	font-weight: bold;
	border: 1px solid white;
}
#locales a:HOVER, #locales a:ACTIVE {
	border: 1px solid #012142;
}
</style>
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
	if (country == 'AE') {
		$('#taxIdItem').hide();
		$('#zipItem').hide();
	} else {
		checkTaxId($('#contractorTaxId').val());
		if($('#taxIdItem').is(':hidden'))
			$('#taxIdItem').show();
		if($('#zipItem').is(':hidden'))
			$('#zipItem').show();
	}
	
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
	if ($('input#materialSupplier').is(':checked') && !$('input#onsiteService').is(':checked') && !$('input#offsiteService').is(':checked')) {
		$('#step2').hide();
		$('span.vendor').show();
		$('span.service').hide();
	} else {
		$('#step2').show();
		$('span.vendor').hide();
		$('span.service').show();
	}
}

function updateRequiredField() {
	if($('#Onsite').is(':checked') || $('#Offsite').is(':checked') || $('#Supplier').is(':checked')) {
		$('#Onsite').parent().addClass('hasdata');
		$('#Offsite').parent().addClass('hasdata');
		$('#Supplier').parent().addClass('hasdata');
	}
	if(!$('#Onsite').is(':checked') && !$('#Offsite').is(':checked') && !$('#Supplier').is(':checked')) {
		$('#Onsite').parent().removeClass('hasdata');
		$('#Offsite').parent().removeClass('hasdata');
		$('#Supplier').parent().removeClass('hasdata');
	}
		
}

$(function(){
	changeState($('#contractorCountry').val());
})
</script>
</head>
<body>
<s:if test="debugging">
<div id="locales">
<a href="?request_locale=en">English</a>
<a href="?request_locale=fr">Français</a>
<a href="?request_locale=es">Español</a>
</div>
</s:if>

<s:include value="registrationHeader.jsp"></s:include>
<span class="redMain required-info"><s:text name="%{scope}.IndicatesRequiredInfo" /></span>
<s:form method="POST">
<s:hidden name="requestID" />
	<br clear="all" />
	<table>
		<tr>
			<td style="vertical-align: top; width: 50%;">
				<fieldset class="form">
					<h2 class="formLegend"><s:text name="%{scope}.CompanyDetails.heading" /></h2>
					<ol>
						<li class="required"><label><s:text name="%{scope}.CompanyDetails.LegalCompanyName"/>:</label>
							<s:textfield name="contractor.name" size="35" onchange="checkName(this.value);"/>
							<div class="fieldhelp">
								<h3>Company Name</h3>
								<p>The name of your company as listed on legal documents, trade certificates and licenses.</p>
								<p>We'll search our database to see if your company has already been registered.</p>
							</div>
							<div id="name_status"></div>
						</li>
						<li><label><s:text name="%{scope}.CompanyDetails.ShortNameorDBA" />:</label>
							<s:textfield name="contractor.dbaName" size="35" />
							<div class="fieldhelp">
								<h3>Short Name</h3>
								<p>An alternative (also known as Doing Business As) name of your company. This is optional but may help your customers search for you if they only know you by an acronym, shortened company name, or alternative DBA name.</p>
							</div>
						</li>
						<li class="required"><label><s:text name="%{scope}.CompanyDetails.Country" />:</label>
							<s:select list="countryList" id="contractorCountry"
								name="contractor.country.isoCode"
								onchange="changeCountry(this.value);"
								headerKey="" headerValue="- Country -"
								listKey="isoCode" listValue="name" />
							<div class="fieldhelp">
								<h3>Country</h3>
								<p>The headquarters of your company. This will affect the currency in which your PICS membership will be listed.</p>
							</div>
						</li>
						<li class="required" id="taxIdItem" <s:if test="contractor.country.isoCode =='AE'">style="display: none;"</s:if>><label><s:text name="%{scope}.CompanyDetails.TaxID" />:</label>
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
						<li><label><s:text name="%{scope}.CompanyDetails.WebURL" />:</label>
							<s:textfield name="contractor.webUrl" size="35" />
							<div class="fieldhelp">
								<h3>Web URL</h3>
								The web site your customers can go to to learn more about your products and services.
								<h5>Example:</h5>
								www.yourcompany.com
							</div>
						</li>
						<li class="required"><label><s:text name="%{scope}.CompanyDetails.CompanyPhone" />:</label>
							<s:textfield id="contractorPhone" name="contractor.phone" size="20" />
							<div class="fieldhelp">
								<h3><s:text name="%{scope}.CompanyDetails.CompanyPhone" />:</h3>
								Your company's primary telephone line. If you are outside the United States or Canada, please include your country code.
							</div>
						</li>
						<li><label><s:text name="%{scope}.CompanyDetails.CompanyFax" />:</label>
							<s:textfield name="contractor.fax" size="20" />
							<div class="fieldhelp">
								<h3>Company Fax</h3>
								Your company's primary fax line if any.
							</div>
						</li>
					</ol>
				</fieldset>
				<fieldset class="form">
					<h2 class="formLegend"><s:text name="%{scope}.PrimaryAddress.heading" /></h2>
					<ol>
						<li class="required"><label><s:text name="%{scope}.PrimaryAddress.Address" />:</label>
							<s:textfield name="contractor.address" size="35" /><br />
							<s:textfield name="contractor.address2" size="35" cssClass="multifield" />
							<div class="fieldhelp">
								<h3>Primary Address</h3>
								Your company's primary address or headquarters. This should be located in the country you selected in Company Details above.
								<h5>Example:</h5>
								123 Main Street, Suite 100
							</div>
						</li>
						<li class="required"><label><s:text name="%{scope}.PrimaryAddress.City" />:</label> 
							<s:textfield name="contractor.city" size="35" />
						</li>
						<li><label><s:text name="%{scope}.PrimaryAddress.Country" />:</label>
							<input type="text" disabled="disabled" id="country_display"/>
						</li>
						<s:if test="contractor == null || contractor.state == null"><li id="state_li"></li></s:if>
						<s:else>
							<li class="required"><label><s:text name="%{scope}.PrimaryAddress.State" />:</label>
								<s:select list="stateList" id="state_sel"
									name="contractor.state.isoCode"
									headerKey="" headerValue="- State -"
									listKey="isoCode" listValue="name" />
							</li>
						</s:else>
						<li class="required" id="zipItem" <s:if test="contractor.country.isoCode == 'AE'">style="display: none;"</s:if>><label><s:text name="%{scope}.PrimaryAddress.ZipCode" />:</label>
							<s:textfield name="contractor.zip" size="10" />
						</li>
					</ol>
				</fieldset>
				<fieldset class="form">
					<h2 class="formLegend"><s:text name="%{scope}.IndustryDetails.heading" /></h2>
					<ol>
						<li class="required"><label><s:text name="%{scope}.IndustryDetails.MainTrade" />:</label>
							<s:select list="tradeList"
								name="contractor.mainTrade" headerKey="" headerValue="- Choose a trade -" 
								listKey="name" listValue="name" />
							<div class="fieldhelp">
								<h3>Trade</h3>
								This is your company's primary trade. You will have the opportunity to select additional trades later on.
							</div>
						</li>
						<s:iterator value="@com.picsauditing.jpa.entities.ContractorType@values()" id="conType">
							<li class="required">
								<label><s:property value="type" />:</label>
								<s:if test="#conType.toString() == 'Onsite'"><s:checkbox id="Onsite" name="contractor.onsiteServices" onclick="updateRequiredField();"/></s:if>
								<s:if test="#conType.toString() == 'Offsite'"><s:checkbox id="Offsite" name="contractor.offsiteServices" onclick="updateRequiredField();"/></s:if>
								<s:if test="#conType.toString() == 'Supplier'"><s:checkbox id="Supplier" name="contractor.materialSupplier" onclick="updateRequiredField();"/></s:if>
								<div class="fieldhelp">
									<h3><s:property value="type" /></h3>
									<s:property value="description" escape="false" />
								</div>
							</li>
						</s:iterator>
					</ol>
				</fieldset>
				<fieldset class="form">
					<h2 class="formLegend"><s:text name="%{scope}.CompanyIdentification.heading" /></h2>
					<ol>
						<li><label><s:text name="%{scope}.CompanyIdentification.Description" />:</label>
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
					<h2 class="formLegend"><s:text name="%{scope}.PrimaryContact.heading" /></h2>
					<ol>
						<li class="required"><label><s:text name="%{scope}.PrimaryContact.Name" />:</label>
							<s:textfield name="user.name" size="20" />
							<div class="fieldhelp">
								<h3>Name</h3>
								This your name. We use the information in this section to create the first administrator user.
								You'll have the opportunity to create more users later with varying levels of permissions or change the primary contact to another user.
								<h5>Example</h5>
								John Doe
							</div>
						</li>
						<li class="required"><label><s:text name="%{scope}.PrimaryContact.Email" />:</label>
							<s:textfield name="user.email" size="20" /> 
							<div class="fieldhelp">
								<h3>Email</h3>
								Your email address. We send vital information to this address, so it needs to be correct. Your customers that use PICS will be able to see primary contact(s) and their phone and email address.
							</div>
						</li>
						<li><label><s:text name="%{scope}.PrimaryContact.Phone" />:</label>
							<s:textfield name="user.phone" size="20" />
							<div class="fieldhelp">
								<h3>Phone</h3>
								This is the direct phone number and extension if applicable for the primary contact. This may be used by CSRs or auditors to contact you directly.
							</div>
						</li>
						<li><label><s:text name="%{scope}.PrimaryContact.Fax" />:</label>
							<s:textfield name="user.fax" size="20" />
						</li>
						<li class="required"><label><s:text name="%{scope}.PrimaryContact.Username" />:</label>
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
						<li class="required"><label><s:text name="%{scope}.PrimaryContact.Password" />:</label> 
							<s:password name="password"/>
							<div class="fieldhelp">
								<h3>Password</h3>
								Must be at least 5 characters long and different from your username
							</div>
						</li>
						<li><label><s:text name="%{scope}.PrimaryContact.ConfirmPassword" />:</label> 
							<s:password name="confirmPassword"/>
						</li>
						<li>
							<b><s:text name="%{scope}.TermsAndConditions" /></b>
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
