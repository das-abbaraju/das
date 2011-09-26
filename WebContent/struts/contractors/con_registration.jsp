<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<html>
	<head>
		<title><s:text name="%{scope}.title" /></title>
		
		<meta name="help" content="User_Manual_for_Contractors">
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
			
		<s:include value="../jquery.jsp"/>
		
		<script type="text/javascript" src="js/detect_timezone.js?v=<s:property value="version"/>"></script>
		
		<script type="text/javascript">
			$(function() {
				changeCountry($("#contractorCountry").val());
			});
			
			function checkUsername(username) {
				$('#username_status').text(translate('JS.Registration.CheckingUsername'));
				var data = {userID: 0, username: username};
				$('#username_status').load('user_ajax.jsp', data);
			}
			
			function checkName(name) {
				if (name.indexOf('^^^') > 0) {
					$('#name_status').html("This contractor will be created as a DEMO account.");
					$('#contractorTaxId').val('000000000');
					$('#contractorPhone').val('949-387-1940');
					return;
				}
				startThinking({div:'name_status', message: translate('JS.Registration.CheckingName')});
				var data = {companyName: name, button: 'name'};
				$('#name_status').load('ContractorValidateAjax.action', data);
			}
			
			function changeCountry(country) {
				if (country == 'AE') {
					$('#contractorTaxId').val('');
					$('#taxIdItem').hide();
					$('#ContractorRegistration_contractor_zip').val('');
					$('#zipItem').hide();
				} else {
					if($('#taxIdItem').is(':hidden'))
						$('#taxIdItem').show();
					if($('#zipItem').is(':hidden'))
						$('#zipItem').show();
			
					if (country == 'US'){
						$('#taxIdLabel').text(translate('JS.ContractorAccount.taxId.US')+':');
						$('#taxIdLabelHelp').html(translate('JS.ContractorAccount.taxId.US.help'));
					} else if (country == 'CA') {
						$('#taxIdLabel').text(translate('JS.ContractorAccount.taxId.CA')+':');
						$('#taxIdLabelHelp').html(translate('JS.ContractorAccount.taxId.CA.help'));
					} else {
						$('#taxIdLabel').text(translate('JS.ContractorAccount.taxId.Other')+':');
						$('#taxIdLabelHelp').html(translate('JS.ContractorAccount.taxId.Other.help'));
					}
				}
				
				changeState(country);
			}
			
			function changeState(country) {
				$('#state_li').load('StateListAjax.action',{countryString: $('#contractorCountry').val(), prefix: 'contractor.'});
				$('#country_display').val($('#contractorCountry option:selected').text());
			
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
			
				$('#timezone').val(jzTimezoneDetector.determine_timezone().timezone.olson_tz);
			});
		
		</script>
	</head>
	<body>
		<s:if test="configEnvironment"><!-- We can remove this once we've release i18n -->
			<ul class="locales">
				<li>
					<a href="?request_locale=en">English</a>
				</li>
				<li>
					<a href="?request_locale=fr">Français</a>
				</li>
				<li>
					<a href="?request_locale=es">Español</a>
				</li>
			</ul>
		</s:if>
		
		<s:include value="registrationHeader.jsp"></s:include>
		
		<span class="redMain required-info"><s:text name="%{scope}.IndicatesRequiredInfo" /></span>
		
		<s:form method="POST" cssClass="form">
			<s:hidden name="requestID" />
			
			<fieldset class="form">
				<h2 class="formLegend"><s:text name="%{scope}.CompanyDetails.heading" /></h2>
				
				<ol>
					<li class="required">
						<s:textfield name="contractor.name" size="35" theme="formhelp" onchange="checkName(this.value);"/>
						<div id="name_status"></div>
					</li>
					<li>
						<s:textfield name="contractor.dbaName"  theme="formhelp" size="35" />
					</li>
					<li class="required">
						<s:select list="countryList" id="contractorCountry"
							theme="formhelp"
							name="contractor.country.isoCode"
							onchange="changeCountry(this.value);"
							listKey="isoCode" listValue="name" />
					</li>
					<li class="required" id="taxIdItem" <s:if test="contractor.country.isoCode =='AE'">style="display: none;"</s:if>>
						<label><s:div id="taxIdLabel" /></label>
						<s:textfield name="contractor.taxId" id="contractorTaxId" size="15" maxLength="15" />
						<s:div cssClass="fieldhelp" id="taxIdLabelHelp" />
						<div id="taxId_status"></div>
					</li>
					<li>
						<s:textfield name="contractor.webUrl" theme="formhelp" size="35" />
					</li>
					<li class="required">
						<s:textfield id="contractorPhone" name="contractor.phone" theme="formhelp" size="20" />
					</li>
					<li>
						<s:textfield name="contractor.fax" theme="formhelp" size="20" />
					</li>
				</ol>
			</fieldset>
			
			<fieldset class="form">
				<h2 class="formLegend"><s:text name="global.PrimaryAddress" /></h2>
				
				<ol>
					<li class="required">
						<s:textfield name="contractor.address" theme="formhelp" size="35" /><br />
						<s:textfield name="contractor.address2" size="35" cssClass="multifield" />
					</li>
					<li class="required">
						<s:textfield name="contractor.city" theme="formhelp" size="35" />
					</li>
					<li>
						<label><s:text name="Country" />:</label>
						<input type="text" disabled="disabled" id="country_display"/>
					</li>
					
					<s:if test="contractor == null || contractor.state == null">
						<li id="state_li"></li>
					</s:if>
					<s:else>
						<li class="required">
							<s:select list="stateList" id="state_sel" name="contractor.state.isoCode" theme="formhelp" listKey="isoCode" listValue="name" />
						</li>
					</s:else>
					
					<li class="required" id="zipItem" <s:if test="contractor.country.isoCode == 'AE'">style="display: none;"</s:if>>
						<s:textfield name="contractor.zip" theme="formhelp" size="10" />
					</li>
					<li class="required">
						<s:select name="contractor.timezone" id="timezone" value="contractor.timezone.iD" theme="form" list="@com.picsauditing.util.TimeZoneUtil@TIME_ZONES" />
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
						<label><s:text name="ContractorAccount.soleProprietor" /></label>
						<s:radio list="#{'true':getText('YesNo.Yes'),'false':getText('YesNo.No')}" id="SoleProprietor" name="contractor.soleProprietor" />
						<div class="fieldhelp">
							<h3><s:text name="ContractorAccount.soleProprietor.fieldhelptitle" /></h3>
							<s:text name="ContractorAccount.soleProprietor.fieldhelp" />
						</div>
					</li>
					<li>
						<label><s:text name="ContractorAccount.competitorMembership" /></label>
						<s:radio list="#{'true':getText('YesNo.Yes'),'false':getText('YesNo.No')}" id="CompetitorMembership" name="contractor.competitorMembership" />
						<div class="fieldhelp">
							<h3><s:text name="ContractorAccount.competitorMembership.fieldhelptitle" /></h3>
							<s:text name="ContractorAccount.competitorMembership.fieldhelp" />
						</div>
					</li>
					<li>
						<label><s:text name="ContractorAccount.description.alttitle" />:</label>
						<s:textarea name="contractor.description" cols="60" rows="15" />
						<div class="fieldhelp">
							<h3><s:text name="ContractorAccount.description" /></h3>
							<s:text name="ContractorAccount.description.fieldhelp" />
						</div>
					</li>
				</ol>
			</fieldset>
			
			<fieldset class="form">
				<h2 class="formLegend"><s:text name="global.ContactPrimary" /></h2>
				
				<ol>
					<li class="required">
						<s:textfield name="user.name" size="20" theme="formhelp" />
					</li>
					<li class="required">
						<s:textfield name="user.email" size="20" theme="formhelp" /> 
					</li>
					<li>
						<s:textfield name="user.phone" size="20" theme="formhelp" />
					</li>
					<li>
						<s:textfield name="user.fax" size="20" theme="formhelp" />
					</li>
					<li class="required">
				 		<s:textfield name="user.username" onchange="checkUsername(this.value);"  theme="formhelp"/>
				 		<br />
				 		<div id="username_status"></div>
				 	</li>
					<li class="required">
						<s:password name="password" theme="formhelp" label="global.Password"/>
					</li>
					<li>
						<s:password name="confirmPassword" theme="formhelp" label="global.ConfirmPassword"/>
					</li>
					<li>
						<b><s:text name="ContractorRegistration.TermsAndConditions" /></b>
					</li>
				</ol>
			</fieldset>
			
			<fieldset class="form submit">
				<div>
					<s:submit method="createAccount" value="%{getText('ContractorRegistration.button.CreateAccount')}" cssClass="picsbutton positive"></s:submit>
				</div>
			</fieldset>
		</s:form>
		
		<s:include value="registrationFooter.jsp" />
	</body>
</html>