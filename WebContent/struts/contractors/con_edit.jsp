<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@page import="com.picsauditing.util.URLUtils"%>
<html>
<head>
<title><s:property value="contractor.name" /></title>
<meta name="help" content="User_Manual_for_Contractors">
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=20091105" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
$(function() {
	$('.datepicker').datepicker();
});
</script>
</head>
<body>
<s:if test="permissions.contractor && !contractor.activeB">
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
					<li><label>Username:</label>
						<s:textfield name="user.username" size="20" />
							<pics:permission perm="SwitchUser">
								<a href="Login.action?button=login&switchToUser=<s:property value="user.id"/>">Switch User</a>
							</pics:permission>
					</li>
					<li><label>Change Password:</label>
						<s:password name="password1" size="15" />
					</li>
					<li><label>Confirm Password:</label>
						<s:password name="password2" size="15" />
					</li>
					<li><label>Date Created:</label>
						<s:date name="contractor.creationDate" format="MMM d, yyyy" />
					</li>
					<li><label>Last Login:</label>
						<s:date name="contractor.lastLogin" format="MMM d, yyyy" />
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
						<s:select list="@com.picsauditing.PICS.Inputs@COUNTRY_ARRAY" 
						name="contractor.country"
						onchange="(this.value == 'USA' || this.value == 'Canada') ? $('#state_li').show() : $('#state_li').hide();"
						/></li>
						
					<li id="state_li" 
						<s:if test="contractor.country != 'USA' && contractor.country != 'Canada'">style="display: none"</s:if>
						><label>State/Province:</label>
						<s:select list="StateList" name="contractor.state"/></li>
					<li><label>Zip:</label>
						<s:textfield name="contractor.zip" size="7" />
					</li>
					<li><label>Phone:</label>
						<s:textfield name="contractor.phone" size="15" />
					</li>
					<li><label>Phone 2:</label>
						<s:textfield name="contractor.phone2" size="15" />
					</li>
					<li><label>Fax:</label>
						<s:textfield name="contractor.fax" size="15" />
					</li>
					<li><label>Email:</label>
						<s:textfield name="contractor.email" size="30" />
					</li>
					<li><label>Email Confirmed Date:</label>
						<s:date name="user.emailConfirmedDate" format="MMM d, yyyy" />
					</li>
					<li><label>Contact:</label>
						<s:textfield name="contractor.contact" size="20" />
					</li>
				</ol>
				</fieldset>
				<fieldset class="form">
				<legend><span>Company Contacts</span></legend>
				<ol>		
					<li><label>Second Contact:</label>
						<s:textfield name="contractor.secondContact" size="20" />
					</li>
					<li><label>Second Phone:</label>
						<s:textfield name="contractor.secondPhone" size="15" />
					</li>
					<li><label>Second Email:</label>
						<s:textfield name="contractor.secondEmail" size="30" />
					</li>
					<li><label>Web URL:</label>
						<s:textfield name="contractor.webUrl" size="30" />
					</li>
				</ol>
				</fieldset>
				<fieldset class="form">
				<legend><span>Billing Contact</span></legend>
				<ol>		
					<li><label>Billing Contact:</label>
						<s:textfield name="contractor.billingContact" size="20" />
					</li>
					<li><label></label><a href="?id=<s:property value="id"/>&button=copyPrimary">Same As Primary Address</a></li>
					<li><label>Billing Address:</label> 
						<s:textfield name="contractor.billingAddress" size="35" /></li>
					<li><label>Billing City:</label>
						<s:textfield name="contractor.billingCity" size="35" /></li>
					<li><label>Billing State:</label>
						<s:select list="StateList" name="contractor.billingState"/></li>
					<li><label>Billing Zip:</label>
						<s:textfield name="contractor.billingZip" size="35" /></li>
					<li><label>Billing Phone:</label>
						<s:textfield name="contractor.billingPhone" size="15" />
					</li>
					<li><label>Billing Email:</label>
						<s:textfield name="contractor.billingEmail" size="30" />
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
						<pics:permission perm="RiskRank">
							<s:select name="contractor.riskLevel" list="@com.picsauditing.jpa.entities.LowMedHigh@values()"/>
						</pics:permission>
						<pics:permission perm="RiskRank" negativeCheck="true">
							<s:property value="contractor.riskLevel"/>
						</pics:permission>
					</li>
					<li><label>Requested By:</label>
						<s:property value="contractor.requestedBy.name"/>
					</li>
				</ol>
				</fieldset>
				<fieldset class="form">
				<legend><span>Company Identification</span></legend>
				<ol>
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
					<li><label>Active (Visible):</label>
						<s:if test="contractor.activeB">Yes</s:if>
						<s:else>No</s:else>
					</li>
					<li><label>Will Renew:</label>
						<s:if test="contractor.renew">Yes - <a href="?id=<s:property value="id"/>&button=Cancel">Cancel Account</a></s:if>
						<s:else>No - <a href="?id=<s:property value="id"/>&button=Reactivate">Reactivate</a></s:else>
					</li>
					<li><label>Bid Only Account:</label>
						<s:checkbox name="contractor.acceptsBids"/></li>	
					<li><label>Reason:</label>
						<s:select list="deactivationReasons" name="contractor.reason" headerKey=" " headerValue="- Deactivation Reason -"/>
					</li>
					<li><label>Risk Level:</label>
						<s:radio list="riskLevelList" name="contractor.riskLevel" theme="pics" />
					</li>
					<li><label>Password:</label>
						<a href="?id=<s:property value="id"/>&button=PasswordReminder">Password Reminder</a>
					</li>
					<li><label>Tax ID:</label>
						<s:textfield name="contractor.taxId" size="9" maxLength="9" />*(only digits 0-9, no dashes)
					</li>
					<li><label>Welcome Email:</label>
						<a target="_blank"
							href="send_welcome_email.jsp?id=<s:property value="id"/>"
							onClick="return confirm('Are you sure you want to send a welcome email to <s:property value="contractor.name"/>?');">Send
							Welcome Email</a>
					</li>
					<li><label>Must Pay?</label>
						<s:radio list="#{'Yes':'Yes','No':'No'}" name="contractor.mustPay"
							value="contractor.mustPay" theme="pics" />
					</li>
					<li><label>Upgrade Date:</label>
						<input name="contractor.lastUpgradeDate" type="text" class="forms datepicker" size="10" 
							value="<s:date name="contractor.membershipDate" format="MM/dd/yyyy" />" />
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
