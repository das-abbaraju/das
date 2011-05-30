<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
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
#locales a, #locales a:visited, #locales a:hover, #locales a:active {
	margin: 15px;
	padding: 4px;
	text-decoration: none;
	font-weight: bold;
	border: 1px solid white;
	color: gray;
}
#locales a:hover, #locales a:active {
	border: 1px solid gray;
	background-color: #F0F0F0;
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
	$('#country_display').val($('#contractorCountry option:selected').text());
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
<s:form method="POST" cssClass="form">
	<s:hidden name="requestID" />	
	<fieldset class="form">
		<h2 class="formLegend"><s:text name="%{scope}.CompanyDetails.heading" /></h2>
		<ol>
			<li class="required"><label><s:text name="%{scope}.CompanyDetails.LegalCompanyName"/>:</label>
				<s:textfield name="contractor.name" size="35" onchange="checkName(this.value);"/>
				<pics:fieldhelp title="Company Name">
					<p>The name of your company as listed on legal documents, trade certificates and licenses.</p>
					<p>We'll search our database to see if your company has already been registered.</p>
				</pics:fieldhelp>
				<div id="name_status"></div>
			</li>
			<li><label><s:text name="%{scope}.CompanyDetails.ShortNameorDBA" />:</label>
				<s:textfield name="contractor.dbaName" size="35" />
				<pics:fieldhelp title="Short Name">
					<p>An alternative (also known as Doing Business As) name of your company. This is optional but may help your customers search for you if they only know you by an acronym, shortened company name, or alternative DBA name.</p>
				</pics:fieldhelp>
			</li>
			<li class="required"><label><s:text name="%{scope}.CompanyDetails.Country" />:</label>
				<s:select list="countryList" id="contractorCountry"
					name="contractor.country.isoCode"
					onchange="changeCountry(this.value);"
					headerKey="" headerValue="- Country -"
					listKey="isoCode" listValue="name" />
				<pics:fieldhelp title="Country">
					<p>The headquarters of your company. This will affect the currency in which your PICS membership will be listed.</p>
				</pics:fieldhelp>
			</li>
			<li class="required" id="taxIdItem" <s:if test="contractor.country.isoCode =='AE'">style="display: none;"</s:if>><label><s:text name="%{scope}.CompanyDetails.TaxID" />:</label>
				<s:textfield name="contractor.taxId" id="contractorTaxId"
					size="9" maxLength="9" onchange="checkTaxId(this.value);" />
				<pics:fieldhelp title="Tax ID or Business Number">
					<p>The number your government uses to uniquely identify your company. We use this to see if your company has already registered.</p>
					<h5>United States</h5>
					9-digit number with no dashes
					<h5>Canada</h5>
					the first 9-digits of your 15 character Business Number
				</pics:fieldhelp>
				<div id="taxId_status"></div>
			</li>
			<li><label><s:text name="%{scope}.CompanyDetails.WebURL" />:</label>
				<s:textfield name="contractor.webUrl" size="35" />
				<pics:fieldhelp title="Web URL">
					The web site your customers can go to to learn more about your products and services.
					<h5>Example:</h5>
					www.yourcompany.com
				</pics:fieldhelp>
			</li>
			<li class="required"><label><s:text name="%{scope}.CompanyDetails.CompanyPhone" />:</label>
				<s:textfield id="contractorPhone" name="contractor.phone" size="20" />
				<pics:fieldhelp>
					<h3><s:text name="%{scope}.CompanyDetails.CompanyPhone" />:</h3>
					Your company's primary telephone line. If you are outside the United States or Canada, please include your country code.
				</pics:fieldhelp>
			</li>
			<li><label><s:text name="%{scope}.CompanyDetails.CompanyFax" />:</label>
				<s:textfield name="contractor.fax" size="20" />
				<pics:fieldhelp title="Company Fax">
					Your company's primary fax line if any.
				</pics:fieldhelp>
			</li>
		</ol>
	</fieldset>
	<fieldset class="form">
		<h2 class="formLegend"><s:text name="global.PrimaryAddress" /></h2>
		<ol>
			<li class="required"><label><s:text name="%{scope}.PrimaryAddress.Address" />:</label>
				<s:textfield name="contractor.address" size="35" /><br />
				<s:textfield name="contractor.address2" size="35" cssClass="multifield" />
				<pics:fieldhelp title="Primary Address">
					Your company's primary address or headquarters. This should be located in the country you selected in Company Details above.
					<h5>Example:</h5>
					123 Main Street, Suite 100
				</pics:fieldhelp>
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
			<li>
				<s:checkbox id="Onsite" name="contractor.onsiteServices" theme="formhelp" onclick="updateRequiredField();"/>
			</li>
			<li>
				<s:checkbox id="Offsite" name="contractor.offsiteServices" theme="formhelp" onclick="updateRequiredField();"/>
			</li>
			<li>
				<s:checkbox id="Supplier" name="contractor.materialSupplier" theme="formhelp" onclick="updateRequiredField();"/>
			</li>
		</ol>
	</fieldset>
	<fieldset class="form">
		<h2 class="formLegend"><s:text name="%{scope}.CompanyIdentification.heading" /></h2>
		<ol>
			<li>
				<s:checkbox id="SoleProprietor" name="contractor.soleProprietor" theme="formhelp" />
			</li>
			<li>
				<s:checkbox id="CompetitorMembership" name="contractor.competitorMembership" theme="formhelp" />
			</li>
			<li><label><s:text name="%{scope}.CompanyIdentification.Description" />:</label>
				<s:textarea name="contractor.description" cols="60" rows="15" />
				<pics:fieldhelp title="Description">
					<p>Include up to 2000 words to describe your company.</p>
					<h5>Suggestion:</h5>
					<p>Copy and paste text from the &quot;About&quot; section on your web site or company brochure.</p>
				</pics:fieldhelp>
			</li>
		</ol>
	</fieldset>
	<fieldset class="form">
		<h2 class="formLegend"><s:text name="global.ContactPrimary" /></h2>
		<ol>
			<li class="required"><label><s:text name="%{scope}.PrimaryContact.Name" />:</label>
				<s:textfield name="user.name" size="20" />
				<pics:fieldhelp title="Name">
					This your name. We use the information in this section to create the first administrator user.
					You'll have the opportunity to create more users later with varying levels of permissions or change the primary contact to another user.
					<h5>Example</h5>
					John Doe
				</pics:fieldhelp>
			</li>
			<li class="required"><label><s:text name="%{scope}.PrimaryContact.Email" />:</label>
				<s:textfield name="user.email" size="20" /> 
				<pics:fieldhelp title="Email">
					Your email address. We send vital information to this address, so it needs to be correct. Your customers that use PICS will be able to see primary contact(s) and their phone and email address.
				</pics:fieldhelp>
			</li>
			<li><label><s:text name="%{scope}.PrimaryContact.Phone" />:</label>
				<s:textfield name="user.phone" size="20" />
				<pics:fieldhelp title="Phone">
					This is the direct phone number and extension if applicable for the primary contact. This may be used by CSRs or auditors to contact you directly.
				</pics:fieldhelp>
			</li>
			<li><label><s:text name="%{scope}.PrimaryContact.Fax" />:</label>
				<s:textfield name="user.fax" size="20" />
			</li>
			<li class="required"><label><s:text name="%{scope}.PrimaryContact.Username" />:</label>
		 		<s:textfield name="user.username" onchange="checkUsername(this.value);"/>
		 		<pics:fieldhelp title="Username">
		 			<p>Please type in your desired user name. We'll let you know if it's available.</p>
		 			<ul>
		 				<li>Must be at least 3 characters long</li>
		 				<li>Don't use spaces in your name</li>
		 				<li>Your email address is recommended because it's easy to remember.</li>
		 			</ul>
		 		</pics:fieldhelp>
		 		<br />
		 		<div id="username_status"></div>
		 	</li>
			<li class="required"><label><s:text name="%{scope}.PrimaryContact.Password" />:</label> 
				<s:password name="password"/>
				<pics:fieldhelp title="Password">
					Must be at least 5 characters long and different from your username
				</pics:fieldhelp>
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
</s:form>
</body>
</html>
