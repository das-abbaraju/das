<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp" pageEncoding="UTF-8"%>
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
	changeState(country);
}

$(function() {
	changeState($("#contractorCountry").val());
	$('.datepicker').datepicker();
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
	<input type="submit" class="picsbutton positive" name="button" value="Save"/>
</div>
<br clear="all" />
<s:hidden name="id" />
	<table>
		<tr>
			<td style="vertical-align: top; width: 50%;">
				<fieldset class="form">
				<legend><span>Details</span></legend>
				<ol>
					<li><label>Name:</label>
						<s:textfield name="contractor.name" size="35" />
					</li>
					<li><label>DBA Name: </label>
						<s:textfield name="contractor.dbaName" size="35" />
					</li>
					<li><label>Date Created:</label>
						<s:date name="contractor.creationDate" format="MMM d, yyyy" />
					</li>
				</ol>
				</fieldset>
				<fieldset class="form">
				<legend><span>Primary Address</span></legend>
				<ol>
					<li><label>Address:</label>
						<s:textfield name="contractor.address" size="35" />
					</li>
					<li><label>City:</label>
						<s:textfield name="contractor.city" size="20" />
					</li>
					<li><label>Country:</label>
						<s:select list="countryList"
						name="country.isoCode" id="contractorCountry"
						listKey="isoCode" listValue="name"
						value="contractor.country.isoCode"
						onchange="countryChanged(this.value)"
						/></li>
					<li id="state_li"></li>
					<li><label>Zip:</label>
						<s:textfield name="contractor.zip" size="7" />
					</li>
					<li><label>Company Phone:</label><s:textfield name="contractor.phone" /></li>
					<li><label>Company Fax:</label><s:textfield name="contractor.fax" /></li>
					
					<li><label>Primary Contact:</label> <s:select
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
				<legend><span>Industry Details</span></legend>
				<ol>
					<li><label>Tax ID:</label>
						<s:property value="contractor.taxId"/>
					</li>
					<li><label>Industry:</label>
						<s:select list="industryList" name="contractor.industry"/>
					</li>
					<li><label>NAICS (Primary):</label>
						<s:property value="contractor.naics.code"/>
					</li>
					<li><label>Main Trade:</label>
						<s:select cssStyle="font-size: 12px;" list="tradeList" name="contractor.mainTrade" headerKey="" headerValue="- Choose a trade -" listKey="question" listValue="question"/>
					</li>
					<li><label>Risk Level:</label>
						<s:property value="contractor.riskLevel"/>
					</li>
					<li><label>Requested By:</label>
						<s:property value="contractor.requestedBy.name"/>
					</li>
				</ol>
				</fieldset>
				<fieldset class="form">
				<legend><span>Company Identification</span></legend>
				<ol>
					<li><label>Web URL:</label> 
						<s:textfield name="contractor.webUrl" size="35" /></li>
					<li><label>Company Logo:</label>
						<s:file name="logo" size="35" />
					</li>
					<li><label>&nbsp</label>
						(Allowed formats: jpg, gif, png)
					</li>
					<li><label>Company Brochure:</label>
						<s:file name="brochure" size="35" />
					</li>
					<li><label>&nbsp</label>
						(Allowed formats: pdf, doc, jpg, gif, png)
					</li>
					<li><label>Description:</label>
						<s:textarea name="contractor.description" cols="40"	rows="15" />
					</li>	
				</ol>
				</fieldset>
			</td>
		<s:if test="permissions.admin">
			<td style="vertical-align: top; width: 50%; padding-left: 10px;">
				<fieldset class="form">
				<legend><span>PICS Admin Fields</span></legend>
				<ol>
					<li><label>Status:</label>
						<s:select list="statusList" name="contractor.status" value="%{contractor.status}" />
					</li>
					<li><label>Will Renew:</label>
						<s:if test="contractor.renew">Yes - <a href="?id=<s:property value="id"/>&button=Cancel">Cancel Account</a></s:if>
						<s:else>No - <a href="?id=<s:property value="id"/>&button=Reactivate">Reactivate</a></s:else>
					</li>
					<li><label>Bid Only Account:</label>
						<s:checkbox name="contractor.acceptsBids"/></li>	
					<li><label>Reason:</label>
						<s:select list="deactivationReasons" name="contractor.reason" headerKey="" headerValue="- Deactivation Reason -"/>
					</li>
					<pics:permission perm="RiskRank">
						<li><label>Risk Level:</label>
							<s:radio list="riskLevelList" name="riskLevel" theme="pics" />
						</li>
					</pics:permission>
					<li><label>Tax ID:</label>
						<s:textfield name="contractor.taxId" size="9" maxLength="9" />*(only digits 0-9, no dashes)
					</li>
					<li><label>Must Pay?</label>
						<s:radio list="#{'Yes':'Yes','No':'No'}" name="contractor.mustPay"
							value="contractor.mustPay" theme="pics" />
					</li>
					<li><label>Upgrade Date:</label>
						<input name="contractor.lastUpgradeDate" type="text" class="forms datepicker" size="10" 
							value="<s:date name="contractor.lastUpgradeDate" format="MM/dd/yyyy" />" />
					</li>
				</ol>
				</fieldset>
				<pics:permission perm="EmailOperators">
					<fieldset class="form">
					<legend><span>De-activation Email</span></legend>
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
<br clear="all">
	<div>
		<s:if test="permissions.contractor">
			<input type="submit" class="picsbutton positive" name="button" value="Save"/>
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
	</div>
</s:form>
</body>
</html>
