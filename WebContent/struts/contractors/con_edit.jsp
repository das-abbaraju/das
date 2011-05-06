<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@page import="com.picsauditing.util.URLUtils"%>
<html>
<head>
<title><s:property value="contractor.name" /></title>
<meta name="help" content="User_Manual_for_Contractors">
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
function changeState(country) {
	$('#state_li').load('StateListAjax.action',{countryString: $('#contractorCountry').val(), stateString: '<s:property value="contractor.state.isoCode"/>'});
}

function countryChanged(country) {
	// hide taxID and zip code
	if (country == 'AE') {
		$('#tax_li').hide();
		$('#zip_li').hide();
	} else {
		$('#tax_li').show();
		$('#zip_li').show();
	}
	changeState(country);
}

$(function() {
	changeState($("#contractorCountry").val());
	$('.datepicker').datepicker();
	$('.cluetip').cluetip({
		closeText: "<img src='images/cross.png' width='16' height='16'>",
		arrows: true,
		cluetipClass: 'jtip',
		local: true,
		clickThrough: false
	});
});

</script>
</head>
<body>
<s:if test="permissions.contractor && !contractor.status.activeDemo">
	<s:include value="registrationHeader.jsp"></s:include>
</s:if>
<s:else>
	<s:include value="conHeader.jsp"></s:include>
</s:else>
<s:if test="permissions.admin && unpaidInvoices.size() > 0">
	<div class="info">Invoices open for this contractor
	<ol>
	<s:iterator value="unpaidInvoices">
		<li><a href="InvoiceDetail.action?invoice.id=<s:property value="id"/>"><s:property value="id"/></a></li>
	</s:iterator>
	</ol>
	</div>
</s:if>
<s:if test="permissions.admin && contractor.qbSync">
	<div class="alert" class="noprint">This contractor is still waiting to be synced with QuickBooks!</div>
</s:if>
<s:if test="contractor.acceptsBids">
	<div class="alert">This is a BID-ONLY Contractor Account.</div>
</s:if>

<s:form id="save" method="POST" enctype="multipart/form-data">
<div>
	<input type="submit" class="picsbutton positive" name="button" value="<s:text name="button.Save" />"/>
</div>
<br clear="all" />
<s:hidden name="id" />
	<table width="100%">
		<tr>
			<td style="vertical-align: top; width: 50%;">
				<fieldset class="form">
				<h2 class="formLegend"><s:text name="%{scope}.Details.heading"/></h2>
				<ol>
					<li><label><s:text name="%{scope}.Details.Name"/>:</label>
						<s:textfield name="contractor.name" size="35" />
					</li>
					<li><label><s:text name="%{scope}.Details.DBAName"/>: </label>
						<s:textfield name="contractor.dbaName" size="35" />
					</li>
					<li><label><s:text name="%{scope}.Details.DateCreated"/>:</label>
						<s:date name="contractor.creationDate" format="MMM d, yyyy" />
					</li>
				</ol>
				</fieldset>
				<fieldset class="form">
				<h2 class="formLegend"><s:text name="global.primaryAddress"/></h2>
				<ol>
					<li><label><s:text name="%{scope}.PrimaryAddress.Address"/>:</label>
						<s:textfield name="contractor.address" size="35" /><br />
						<s:textfield name="contractor.address2" size="35" />
					</li>
					<li><label><s:text name="%{scope}.PrimaryAddress.City"/>:</label>
						<s:textfield name="contractor.city" size="20" />
					</li>
					<li><label><s:text name="%{scope}.PrimaryAddress.Country"/>:</label>
						<s:select list="countryList"
						name="country.isoCode" id="contractorCountry"
						listKey="isoCode" listValue="name"
						value="contractor.country.isoCode"
						onchange="countryChanged(this.value)"
						/></li>
					<li id="state_li"></li>
					<s:if test="contractor.country.isoCode != 'AE'">
						<li id="zip_li"><label><s:text name="%{scope}.PrimaryAddress.Zip"/>:</label>
							<s:textfield name="contractor.zip" size="7" />
						</li>
					</s:if>
					<s:if test="contractor.demo">
					<li><label><s:text name="%{scope}.PrimaryAddress.DefaultLanguage"/>:</label>
						<s:select name="contractor.locale" listValue="displayName"
							list="@com.picsauditing.jpa.entities.AppTranslation@getLocales()" />
					</li>
					</s:if>
					<li><label><s:text name="%{scope}.PrimaryAddress.CompanyPhone"/>:</label><s:textfield name="contractor.phone" /></li>
					<li><label><s:text name="%{scope}.PrimaryAddress.CompanyFax"/>:</label><s:textfield name="contractor.fax" /></li>
					
					<li><label><s:text name="%{scope}.PrimaryAddress.PrimaryContact"/>:</label> <s:select
						list="userList"
						name="contactID"
						listKey="id"
						listValue="name"
						value="%{contractor.primaryContact.id}"
						/>
					<s:if test="permissions.admin">
						<a href="UsersManage.action?button=newUser&accountId=<s:property value="contractor.id"/>&user.isGroup=No&user.isActive=Yes">Add User</a>
					</s:if>
					<s:else>
					<pics:permission perm="ContractorAdmin">
						<a href="UsersManage.action?button=newUser&accountId=<s:property value="contractor.id"/>&user.isGroup=No&user.isActive=Yes">Add User</a>
					</pics:permission>
					</s:else>
					</li>
				</ol>
				</fieldset>
				<fieldset class="form">
				<h2 class="formLegend"><s:text name="%{scope}.IndustryDetails.heading"/></h2>
				<ol>
					<s:if test="contractor.country.isoCode != 'AE'">
						<li id="tax_li"><label><s:text name="%{scope}.IndustryDetails.TaxID"/>:</label>
							<s:property value="contractor.taxId"/>
						</li>
					</s:if>
					<li><label><s:text name="%{scope}.IndustryDetails.NAICSPrimary"/>:</label>
						<s:property value="contractor.naics.code"/>
					</li>
					<li><label><s:text name="%{scope}.IndustryDetails.RiskLevel"/>:</label>
						<s:property value="contractor.riskLevel"/>
					</li>
					<li><label><s:text name="%{scope}.IndustryDetails.RequestedBy"/>:</label>
						<s:property value="contractor.requestedBy.name"/>
					</li>
				</ol>
				</fieldset>
				<fieldset class="form">
				<h2 class="formLegend"><s:text name="%{scope}.CompanyIdentification.heading"/></h2>
				<ol>
					<li><label><s:text name="%{scope}.CompanyIdentification.WebURL"/>:</label> 
						<s:textfield name="contractor.webUrl" size="35" /></li>
					<li><label><s:text name="%{scope}.CompanyIdentification.CompanyLogo"/>:</label>
						<s:file name="logo" size="35" />
					</li>
					<li><label>&nbsp</label>
						(Allowed formats: jpg, gif, png)
					</li>
					<li><label><s:text name="%{scope}.CompanyIdentification.CompanyBrochure"/>:</label>
						<s:file name="brochure" size="35" />
					</li>
					<li><label>&nbsp</label>
						(Allowed formats: pdf, doc, jpg, gif, png)
					</li>
					<li><label><s:text name="%{scope}.CompanyIdentification.Description"/>:</label>
						<s:textarea name="contractor.description" cols="40"	rows="15" />
					</li>	
				</ol>
				</fieldset>
				<fieldset class="form submit">
					<s:if test="permissions.contractor">
						<input type="submit" class="picsbutton positive" name="button" value="<s:text name="button.Save" />"/>
					</s:if>
					<s:else>
						<pics:permission perm="ContractorAccounts" type="Edit">
							<input type="submit" class="picsbutton positive" name="button" value="Save"/>
						</pics:permission>
					</s:else>
					<pics:permission perm="RemoveContractors">
						<input type="submit" class="picsbutton negative" name="button" value="Delete" 
							onClick="return confirm('Are you sure you want to delete this account?');"/>
					</pics:permission>
				</fieldset>				
			</td>
		<s:if test="permissions.admin">
			<td style="vertical-align: top; width: 50%; padding-left: 10px;">
				<fieldset class="form">
				<h2 class="formLegend">PICS Admin Fields</h2>
				<ol>
					<li><label>Status:</label>
						<s:select list="statusList" name="contractor.status" value="%{contractor.status}" />
					</li>
					<li><label>Will Renew:</label>
						<s:if test="contractor.renew">
							Yes - <s:submit action="ContractorEdit!deactivate" value="Cancel Account" />
						<pics:fieldhelp></pics:fieldhelp>
						</s:if>
						<s:else>
							No - <s:submit action="ContractorEdit!reactivate" value="Reactivate" /> 
						</s:else>
					</li>
					<li><label>Bid Only Account:</label>
						<s:checkbox name="contractor.acceptsBids"/></li>	
					<li><label>Reason:</label>
						<s:select list="deactivationReasons" name="contractor.reason" headerKey="" headerValue="- Deactivation Reason -"/>
					</li>
					<s:if test="canEditRiskLevel">
						<li><label>Risk Level:</label>
							<s:radio list="riskLevelList" name="riskLevel" theme="pics" />
						</li>
					</s:if>
					<s:if test="contractor.country.isoCode != 'AE'">
						<li id="taxIdItem"><label>Tax ID:</label>
							<s:textfield name="contractor.taxId" size="9" maxLength="9" />*(only digits 0-9, no dashes)
						</li>
					</s:if>
					<li><label>Must Pay?</label>
						<s:radio list="#{'Yes':'Yes','No':'No'}" name="contractor.mustPay"
							value="contractor.mustPay" theme="pics" />
					</li>
					<li><label>Upgrade Date:</label>
						<input name="contractor.lastUpgradeDate" type="text" class="forms datepicker" size="10" 
							value="<s:date name="contractor.lastUpgradeDate" format="MM/dd/yyyy" />" />
					</li>
					<li><label>Contractor Type:</label>
						<s:iterator value="@com.picsauditing.jpa.entities.ContractorType@values()" id="conType">
							<s:if test="#conType.toString() == 'Onsite'">
								<s:checkbox name="contractor.onsiteServices" /><s:property value="#conType.type" />
							</s:if>
							<s:elseif test="#conType.toString() == 'Offsite'">
								<s:checkbox name="contractor.offsiteServices" /><s:property value="#conType.type" />
							</s:elseif>
							<s:else>
								<s:checkbox name="contractor.materialSupplier" /><s:property value="#conType.type" />
							</s:else>
						</s:iterator>
						<pics:fieldhelp title="Contractor Type">
							<s:iterator value="@com.picsauditing.jpa.entities.ContractorType@values()" id="conType">
								<h5><s:property value="#conType.type" /></h5>
								<s:property value="#conType.description" escape="false" /><br />
							</s:iterator>
						</pics:fieldhelp>
					</li>
				</ol>
				</fieldset>
				<pics:permission perm="EmailOperators">
					<fieldset class="form bottom">
					<h2 class="formLegend">De-activation Email</h2>
					<ol>
						<li>
							<input type="submit" class="picsbutton positive" name="button" value="SendDeactivationEmail"/>
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
