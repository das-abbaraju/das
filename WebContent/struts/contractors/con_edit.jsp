<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<%@ page import="com.picsauditing.util.URLUtils" %>

<html>
	<head>
		<title><s:property value="contractor.name" /></title>
		
		<meta name="help" content="User_Manual_for_Contractors">
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
		
		<s:include value="../jquery.jsp"/>
		
		<script type="text/javascript">
			function changeState(country) {
				$('#state_li').load('StateListAjax.action',{countryString: $('#contractorCountry').val(), prefix: "contractor.", stateString: '<s:property value="contractor.state.isoCode"/>'});
			}
			
			function changeBillingState(country) {
				$('#billing_state_li').load('StateListAjax.action',{countryString: $('#contractorCountry').val(), prefix: 'contractor.billingState', stateString: '<s:property value="contractor.billingState.isoCode"/>'});
				$('#country_display').val($('#contractorCountry option:selected').text());
			}
			
			function countryChanged(country) {
				// hide taxID and zip code
				if (country == 'AE') {
					$('#tax_li').hide();
					$('#zip_li').hide();
				} else {
					$('#tax_li').show();
					$('#zip_li').show();
			
					if (country == 'US'){
						$('.taxIdLabel').text(translate('JS.ContractorAccount.taxId.US')+':');
						$('#taxIdLabelHelp').html(translate('JS.ContractorAccount.taxId.US.help'));
					} else if (country == 'CA') {
						$('.taxIdLabel').text(translate('JS.ContractorAccount.taxId.CA')+':');
						$('#taxIdLabelHelp').html(translate('JS.ContractorAccount.taxId.CA.help'));
					} else {
						$('.taxIdLabel').text(translate('JS.ContractorAccount.taxId.Other')+':');
						$('#taxIdLabelHelp').html(translate('JS.ContractorAccount.taxId.Other.help'));
					}
				}
				changeState(country);
				changeBillingState(country);
			}
			
			$(function() {
				countryChanged($("#contractorCountry").val());
				changeState($("#contractorCountry").val());
				changeBillingState($("#contractorBillingCountry").val());
				$('.datepicker').datepicker();
				$('.cluetip').cluetip({
					closeText: "<img src='images/cross.png' width='16' height='16'>",
					arrows: true,
					cluetipClass: 'jtip',
					local: true,
					clickThrough: false
				});
				
				$('#save').delegate('#removeImportPQFButton', 'click', function(e) {
					return confirm('Are you sure you want to remove this audit?');
				});
			});
		</script>
	</head>
	<body>
		<s:include value="conHeader.jsp" />
		
		<s:if test="permissions.admin && unpaidInvoices.size() > 0">
			<div class="info">
				Invoices open for this contractor
				
				<ol>
					<s:iterator value="unpaidInvoices">
						<li>
							<a href="InvoiceDetail.action?invoice.id=<s:property value="id"/>"><s:property value="id"/></a>
						</li>
					</s:iterator>
				</ol>
			</div>
		</s:if>
		
		<s:if test="permissions.admin && contractor.qbSync">
			<div class="alert" class="noprint">
				This contractor is still waiting to be synced with QuickBooks!
			</div>
		</s:if>
		
		<s:if test="contractor.acceptsBids">
			<div class="alert">
				This is a Bid Only Contractor Account.
			</div>
		</s:if>
		
		<s:form id="save" method="POST" enctype="multipart/form-data">
			<br clear="all" />
			
			<s:hidden name="id" />
			
			<table width="100%">
				<tr>
					<td style="vertical-align: top; width: 50%;">
						<fieldset class="form">
							<h2 class="formLegend"><s:text name="ContractorEdit.Details.heading"/></h2>
							
							<ol>
								<li>
									<label><s:text name="ContractorEdit.Details.Name"/>:</label>
									<s:textfield name="contractor.name" size="35" />
								</li>
								<li>
									<label><s:text name="ContractorEdit.Details.DBAName"/>: </label>
									<s:textfield name="contractor.dbaName" size="35" />
								</li>
								<li>
									<label><s:text name="ContractorEdit.Details.DateCreated"/>:</label>
									<s:date name="contractor.creationDate" format="MMM d, yyyy" />
								</li>
							</ol>
						</fieldset>
						
						<fieldset class="form">
							<h2 class="formLegend"><s:text name="global.primaryAddress"/></h2>
							
							<ol>
								<li>
									<label><s:text name="global.Address"/>:</label>
									<s:textfield name="contractor.address" size="35" />
									<br />
									<s:textfield name="contractor.address2" size="35" />
								</li>
								<li>
									<label><s:text name="ContractorEdit.PrimaryAddress.City"/>:</label>
									<s:textfield name="contractor.city" size="20" />
								</li>
								<li>
									<label><s:text name="Country" />:</label>
									<s:select
										list="countryList"
										name="contractor.country.isoCode" id="contractorCountry"
										listKey="isoCode" listValue="name"
										value="contractor.country.isoCode"
										onchange="countryChanged(this.value)"
									/>
								</li>
								<li id="state_li"></li>
								
								<s:if test="contractor.country.isoCode != 'AE'">
									<li id="zip_li">
										<label><s:text name="ContractorEdit.PrimaryAddress.Zip"/>:</label>
										<s:textfield name="contractor.zip" size="7" />
									</li>
								</s:if>
								
								<li>
									<s:select name="contractor.timezone" value="contractor.timezone.iD" theme="form" list="@com.picsauditing.util.TimeZoneUtil@TIME_ZONES" />
								</li>
								
								<s:if test="contractor.demo || configEnvironment || i18nReady">
									<li>
										<label><s:text name="ContractorEdit.PrimaryAddress.DefaultLanguage"/>:</label>
										<s:select name="contractor.locale" listValue="@org.apache.commons.lang.StringUtils@capitalize(getDisplayName(language))" list="@com.picsauditing.jpa.entities.AppTranslation@getLocales()" />
									</li>
								</s:if>
								
								<li>
									<label><s:text name="ContractorEdit.PrimaryAddress.CompanyPhone"/>:</label>
									<s:textfield name="contractor.phone" />
								</li>
								<li>
									<label><s:text name="ContractorEdit.PrimaryAddress.CompanyFax"/>:</label>
									<s:textfield name="contractor.fax" />
								</li>
								<li>
									<label><s:text name="ContractorEdit.PrimaryAddress.PrimaryContact"/>:</label>
									<s:select
										list="userList"
										name="contactID"
										listKey="id"
										listValue="name"
										value="%{contractor.primaryContact.id}"
									/>
										
									<s:if test="permissions.admin">
										<a href="UsersManage!add.action?account=<s:property value="contractor.id"/>&isActive=Yes&isGroup=&userIsGroup=No">Add User</a>
									</s:if>
									<s:else>
										<pics:permission perm="ContractorAdmin">
											<a href="UsersManage!add.action?account=<s:property value="contractor.id"/>&isActive=Yes&isGroup=&userIsGroup=No">Add User</a>
										</pics:permission>
									</s:else>
								</li>
							</ol>
						</fieldset>
						
						<fieldset class="form">
							<h2 class="formLegend"><s:text name="ContractorEdit.BillingDetails.heading"/></h2>
							
							<ol>
								<li>
									<label><s:text name="ContractorEdit.billingAddress"/>:</label>
									<s:textfield name="contractor.billingAddress" size="35" /><br />
								</li>
								<li>
									<label><s:text name="ContractorEdit.billingCity"/>:</label>
									<s:textfield name="contractor.billingCity" size="20" />
								</li>
								<li>
									<label><s:text name="ContractorEdit.billingCountry" />:</label>
									<input type="text" disabled="disabled" id="country_display"/>
								</li>
								<li id="billing_state_li"></li>
								<li id="billing_zip_li">
									<label><s:text name="ContractorEdit.billingZip"/>:</label>
									<s:textfield name="contractor.billingZip" size="7" />
								</li>
							</ol>
						</fieldset>
						
						<fieldset class="form">
							<h2 class="formLegend"><s:text name="ContractorEdit.IndustryDetails.heading"/></h2>
							
							<ol>
								<s:if test="contractor.country.isoCode != 'AE'">
									<li id="tax_li">
										<label><s:div cssClass="taxIdLabel" /></label>
										<s:property value="contractor.taxId"/>
									</li>
								</s:if>
								
								<li>
									<label><s:text name="ContractorEdit.IndustryDetails.NAICSPrimary"/>:</label>
									<s:property value="contractor.naics.code"/>
								</li>
								<li>
									<label><s:text name="ContractorEdit.SoleProprietor.heading"/>:</label>
									
									<s:if test="contractor.soleProprietor">
										<s:text name="YesNo.Yes" />
									</s:if>
									<s:else>
										<s:text name="YesNo.No" />
									</s:else>
								</li>
								
								<s:if test="contractor.safetyRisk != null">
									<li>
										<label><s:text name="global.SafetyRisk"/>:</label>
										<s:text name="%{contractor.safetyRisk.i18nKey}"/>
									</li>
								</s:if>
								
								<s:if test="contractor.materialSupplier && contractor.productRisk != null">
									<li>
										<label><s:text name="global.ProductRisk"/>:</label>
										<s:text name="%{contractor.productRisk.i18nKey}"/>
									</li>
								</s:if>
								
								<li>
									<label><s:text name="ContractorEdit.IndustryDetails.RequestedBy"/>:</label>
									<s:property value="contractor.requestedBy.name"/>
								</li>
							</ol>
						</fieldset>
						
						<fieldset class="form">
							<h2 class="formLegend"><s:text name="ContractorEdit.CompanyIdentification.heading"/></h2>
							
							<ol>
								<li>
									<label><s:text name="ContractorEdit.CompanyIdentification.WebURL"/>:</label> 
									<s:textfield name="contractor.webUrl" size="35" /></li>
								<li>
									<label><s:text name="ContractorEdit.CompanyIdentification.CompanyLogo"/>:</label>
									<s:file name="logo" size="35" />
								</li>
								<li>
									<label>&nbsp</label>
									(Allowed formats: jpg, gif, png)
								</li>
								<li>
									<label><s:text name="ContractorEdit.CompanyIdentification.CompanyBrochure"/>:</label>
									<s:file name="brochure" size="35" />
								</li>
								<li>
									<label>&nbsp</label>
									(Allowed formats: pdf, doc, jpg, gif, png)
								</li>
								<li>
									<label><s:text name="ContractorEdit.CompanyIdentification.Description"/>:</label>
									<s:textarea name="contractor.description" cols="40"	rows="15" />
								</li>	
							</ol>
						</fieldset>
						
						<fieldset class="form submit">
							<s:if test="permissions.contractor">
								<s:submit cssClass="picsbutton positive" method="save" value="%{getText('button.Save')}" />
							</s:if>
							<s:else>
								<pics:permission perm="ContractorAccounts" type="Edit">
									<s:submit cssClass="picsbutton positive" method="save" value="%{getText('button.Save')}" />
								</pics:permission>
							</s:else>
							
							<pics:permission perm="RemoveContractors">
								<s:submit cssClass="picsbutton negative" method="delete" value="%{getText('button.Delete')}" 
									onclick="return confirm('Are you sure you want to delete this account?');"/>
							</pics:permission>
						</fieldset>				
					</td>
					
					<s:if test="permissions.admin">
					
					<td style="vertical-align: top; width: 50%; padding-left: 10px;">
						<fieldset class="form">
							<h2 class="formLegend">PICS Admin Fields</h2>
							
							<ol>
								<li>
									<label>Status:</label>
									<s:select list="statusList" name="contractor.status" value="%{contractor.status}" />
								</li>
								<li>
									<label>Will Renew:</label>
									
									<s:if test="contractor.renew">
										Yes - <s:submit method="deactivate" value="Cancel Account" />
										<pics:fieldhelp></pics:fieldhelp>
									</s:if>
									<s:else>
										No - <s:submit method="reactivate" value="Reactivate" /> 
									</s:else>
								</li>
								<li>
									<label><s:text name="ContractorEdit.SoleProprietor.heading"/></label>
									<s:checkbox name="contractor.soleProprietor" />
								</li>
								<li>
									<label>Account Level:</label>
									<s:select list="@com.picsauditing.jpa.entities.AccountLevel@values()" name="contractor.accountLevel"/></li>	
								<li>
									<label>Reason:</label>
									<s:select list="deactivationReasons" name="contractor.reason" headerKey="" headerValue="- %{getText('Filters.header.DeactivationReason')} -"
										listKey="key" listValue="value" />
								</li>
								
								<s:if test="canEditRiskLevel">
									<li>
										<label>Risk Levels:</label>
										<a href="ContractorEditRiskLevel.action?id=<s:property value="contractor.id" />" class="edit">Edit Risk Levels</a>
									</li>
								</s:if>
								
								<s:if test="contractor.country.isoCode != 'AE'">
									<li id="taxIdItem">
										<label><s:div cssClass="taxIdLabel" /></label>
										<s:textfield id="contractorTaxId" name="contractor.taxId" size="15" maxLength="15" />
										<s:div cssClass="fieldhelp" id="taxIdLabelHelp" />
									</li>
								</s:if>
								
								<li>
									<label>Must Pay?</label>
									<s:radio 
										list="#{'Yes':'Yes','No':'No'}" 
										name="contractor.mustPay" 
										value="contractor.mustPay"
										theme="pics" 
										cssClass="inline"
									/>
								</li>
								<li>
									<label>Upgrade Date:</label>
									<input name="contractor.lastUpgradeDate" type="text" class="forms datepicker" size="10" value="<s:date name="contractor.lastUpgradeDate" format="MM/dd/yyyy" />" />
								</li>
								<li>
									<label>Contractor Type:</label>
									
									<s:iterator value="@com.picsauditing.jpa.entities.ContractorType@values()" id="conType">
										<s:if test="#conType.toString() == 'Onsite'">
											<s:checkbox name="contractor.onsiteServices" /><s:property value="#conType.type" />
										</s:if>
										<s:elseif test="#conType.toString() == 'Offsite'">
											<s:checkbox name="contractor.offsiteServices" /><s:property value="#conType.type" />
										</s:elseif>
										<s:elseif test="#conType.toString() == 'Supplier'">
											<s:checkbox name="contractor.materialSupplier" /><s:property value="#conType.type" />
										</s:elseif>
										<s:else>
											<s:checkbox name="contractor.transportationServices" /><s:property value="#conType.type" />
										</s:else>
									</s:iterator>
									
									<pics:fieldhelp title="Contractor Type">
										<s:iterator value="@com.picsauditing.jpa.entities.ContractorType@values()" id="conType">
											<h5><s:property value="#conType.type" /></h5>
											
											<s:property value="#conType.description" escape="false" />
											<br />
										</s:iterator>
									</pics:fieldhelp>
								</li>
								<li>
									<label for="conCompetitorMembership">Has Competitor Membership:</label>
									<s:radio
										id="conCompetitorMembership" 
										list="#{'true':getText('YesNo.Yes'),'false':getText('YesNo.No')}" 
										name="contractor.competitorMembership"
										theme="pics" 
										cssClass="inline" 
									/>
									<br />
									
									<s:if test="hasImportPQFAudit">
										<s:submit method="expireImportPQF" cssClass="picsbutton negative" id="removeImportPQFButton" value="Remove Import PQF" />
									</s:if>
									<s:elseif test="contractor.competitorMembership.equals(true)">
										<s:submit method="createImportPQF" cssClass="picsbutton positive" value="Create Import PQF" />
									</s:elseif>
									
									<div class="fieldhelp">
										<h3>Competitor Membership</h3>
										
										<p>
											Clicking on "Remove Import PQF" will only expire any existing Import PQF. This will NOT remove the invoice/fee. Voiding the invoice/fee will have to be done manually.
										</p>
									</div>
								</li>
								<li>
									<label for="conCanadianCompetitor">Has Canadian Competitor:</label>
									<s:radio
										id="conCanadianCompetitor" 
										list="#{'true':getText('YesNo.Yes'),'false':getText('YesNo.No'),'':getText('YesNo.NA')}" 
										name="contractor.hasCanadianCompetitor"
										theme="pics"
										cssClass="inline" 
									/>
								</li>
								<li>
									<s:submit cssClass="picsbutton positive" method="save" value="%{getText('button.Save')}" />
								</li>
							</ol>
						</fieldset>
						
						<pics:permission perm="EmailOperators">
							<fieldset class="form bottom">
								<h2 class="formLegend">De-activation Email</h2>
								
								<ol>
									<li>
										<s:submit cssClass="picsbutton positive" method="sendDeactivationEmail" value="%{getText(scope + '.button.SendDeactivationEmail')}" />
									</li>
									<li>
										<s:select cssStyle="font-size: 12px;" list="operatorList" name="operatorIds" listKey="id" listValue="name" multiple="true" size="10"/>
									</li>
								</ol>
							</fieldset>
						</pics:permission>
					</td>
					
					</s:if>
				</tr>
			</table>
		</s:form>
	</body>
</html>