<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="contractor.name" /> - Payment Method</title>
<meta name="help" content="User_Manual_for_Contractors">
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/invoice.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
$(function() {
	$('a[rel*=facebox]').facebox({
 		loading_image : 'loading.gif',
 		close_image : 'closelabel.gif'
 	});
 });
function validate() {
	return updateExpDate();
}
function updateExpDate() {
	$('#ccexpError').hide();
	if (!$('#expMonth').blank() && !$('#expYear').blank()) {
		$('#ccexp').val($('#expMonth').val() + $('#expYear').val());
		return true;
	}
	$('#ccexpError').text("* Please enter your card's expiration date").show();
	return false;
}
</script>
</head>
<body>
<s:if test="permissions.contractor && !contractor.status.activeDemo">
	<s:include value="registrationHeader.jsp"></s:include>
</s:if>
<s:else>
<s:include value="conHeader.jsp"></s:include>
</s:else>

<s:include value="../actionMessages.jsp"></s:include>

<%-- All criteria are satisfied after contractor has entered CC info --%>
<s:if test="contractor.paymentMethodStatusValid && contractor.paymentMethod.creditCard && contractor.mustPayB">
	<div class="info">Your credit card will be kept on file and used for any upgrades or renewals. We will notify the primary user via email 30 days before any charges occur for renewals and 7 days before any charge occurs for upgrades. If you choose to deactivate your account, please call us at 800-506-7427.</div>
</s:if>

<s:if test="contractor.operators.size == 0">
	<div class="alert">
		You have not selected any facilities. No operators will be able to view your account until you do. 
		<a href="ContractorFacilities.action?id=<s:property value="contractor.id"/>">Click to update your operator listings.</a>
	</div>
</s:if>
<s:elseif test="contractor.newMembershipLevel.amount == 0">
	<div class="alert">
		You are currently at the free level and do not owe any membership dues.  
		However, a valid credit card is required to maintain an account with PICS.  
		Only if you upgrade your account will your card be charged, and only after PICS notifies you at 
		least 7 days before the charge.
	</div>
</s:elseif>
<s:elseif test="contractor.status.active && !contractor.paymentMethodStatusValid && contractor.mustPayB">
		<div class="info">
			As an improvement, you may now pay by credit card.  Even though you are providing your credit card information at this time, your card will not be charged until the next billing date.  PICS will email you 7 days prior to renewal before any charges are applied.  If you have questions, contact PICS Accounting any time at (800) 506-7427 x 708.
		</div>
</s:elseif>
<s:if test="!contractor.paymentMethod.creditCard && contractor.mustPayB">
	<div class="info">PICS will email each invoice. Please make sure your contact information is updated.</div>
</s:if>
<s:if test="contractor.balance > 0">
	<div class="alert">
		<s:iterator value="contractor.invoices">
			<s:if test="status.unpaid">
			You have an <a href="InvoiceDetail.action?invoice.id=<s:property value="id"/>">unpaid invoice</a> for $<s:property value="balance"/> due <s:property value="@com.picsauditing.PICS.DateBean@toShowFormat(dueDate)"/><br/> 	
			</s:if>
		</s:iterator>
	</div>
</s:if>
<s:form id="save" method="POST">
	<s:hidden name="id" />
<fieldset class="form">
<h2 class="formLegend">Membership Details</h2>
<ol>
<s:if test="contractor.newMembershipLevel.amount > 0">
	<li>
		<s:if test="contractor.paymentMethod.creditCard && contractor.newMembershipLevel.amount < 500">
			<i>Please Note: Credit Card payment is required for memberships under $500.</i>
		</s:if>
	</li>

	<s:if test="contractor.status.activeDemo">
		<li><label>Next Billing Date:</label> <s:date
			name="contractor.paymentExpires" format="MMM d, yyyy" /></li>
		<li><label>Next Billing Amount:</label> $<s:property
			value="contractor.newMembershipLevel.amount" /> USD
			<a onClick="window.open('con_pricing.jsp','name','toolbar=0,scrollbars=0,location=0,statusbar=0,menubar=0,resizable=1,width=300,height=420'); return false;"
				href="#" title="opens in new window">Click here to view pricing</a>
		</li>
	</s:if>
	<s:else>
		<s:if test="contractor.balance > 0">
			<li>
				<s:iterator value="contractor.invoices" id="i">
					<s:if test="status.unpaid">
						<s:include value="con_invoice_embed.jsp"/>
						<br clear="all"/>
					</s:if>
				</s:iterator>
			</li>
		</s:if>
		<s:elseif test="contractor.acceptsBids">
			<li><label>Total:</label> $<s:property value="contractor.newMembershipLevel.amount"/> USD </li>
		</s:elseif>
		<s:else>
			<li><label>Annual Membership:</label> $<s:property
				value="contractor.newMembershipLevel.amount" /> USD
				<a onClick="window.open('con_pricing.jsp','name','toolbar=0,scrollbars=0,location=0,statusbar=0,menubar=0,resizable=1,width=300,height=420'); return false;"
					href="#" title="opens in new window">Click here to view pricing</a>
				</li>
			<li><label><s:property value="activationFee.fee"/>:</label> $<s:property value="activationFee.amount"/> USD</li>
			<li><label>Total:</label> $<s:property value="activationFee.amount+contractor.newMembershipLevel.amount"/> USD </li>
		</s:else>
	</s:else>
</s:if>
<s:else>
	<li><label>Status:</label>no payment required</li>
</s:else>
<li><label>Payment Method:</label>
	<s:property value="contractor.paymentMethod.description"/><br/>
</li>
<s:if test="contractor.status.active || permissions.admin">
	<li><label>
			Contractor Agreement:
		</label><s:checkbox name="contractor.agreed" disabled="true" />
			<s:if test="contractor.agreementDate != null">
			On <s:date name="contractor.agreementDate" format="MM/dd/yy" />, <s:property value="contractor.agreedBy.name" /> agreed to the terms of the PICS Contractor Agreement.
		</s:if>
	</li>
	<s:if test="contractor.agreementDate != null && !contractor.agreementInEffect">
		<li><label>&nbsp;</label>
			<span style="color:grey;">We periodically update our Contractor User Agreement. Please review the <a title="Click here to view the latest PICS Contractor Agreement" href="ContractorAgreementAjax.action?id=<s:property value="contractor.id"/>" rel="facebox">latest terms</a> 
			and 
			<s:if test="!permissions.admin &&
						(permissions.hasPermission(@com.picsauditing.access.OpPerms@ContractorBilling) 
							|| permissions.hasPermission(@com.picsauditing.access.OpPerms@ContractorAdmin)
							|| permissions.hasPermission(@com.picsauditing.access.OpPerms@ContractorSafety)) ">
				<form>
					click <input type="submit" name="button" class="picsbutton positive" value="I Agree" />
				</form>
			</s:if>
			<s:else>
				have one of the account administrators login
			</s:else>
			to accept.</span>
		</li>
	</s:if>
</s:if>
<li><label>&nbsp;</label>
	<a onClick="window.open('privacy_policy.jsp','name','toolbar=0,scrollbars=1,location=0,statusbar=0,menubar=0,resizable=1,width=500,height=500'); return false;"
		 href="#" class="ext">Privacy Policy</a> |
	<a href="#" onClick="window.open('refund_policy.jsp','name','toolbar=0,scrollbars=1,location=0,statusbar=0,menubar=0,resizable=1,width=500,height=500'); return false;"
		 href="#" class="ext">Refund Policy</a> |
	<a title="Click here to view the PICS Contractor Agreement" href="ContractorAgreementAjax.action?id=<s:property value="contractor.id"/>" rel="facebox">
	Contractor Agreement </a>
</li>
<s:if test="contractor.newMembershipLevel.amount > 500 || permissions.admin">
	<li>
		<div>
			<s:if test="contractor.paymentMethod.creditCard">
				<input type="submit" class="picsbutton" name="button" value="Change Payment Method to Check"/>
			</s:if>
			<s:else>
				<input type="submit" class="picsbutton" name="button" value="Change Payment Method to Credit Card"/>
			</s:else>
		</div>
		
	</li>
	
</s:if>
<pics:permission perm="Billing">
<s:if test="contractor.ccOnFile">	
	<li><div>
			<input type="submit" class="picsbutton negative" name="button" value="Mark this Credit Card Invalid"/>
		</div>
	</li>
</s:if>		
</pics:permission>
</ol>
</fieldset>
</s:form>

<s:if test="contractor.paymentMethod.creditCard && !braintreeCommunicationError">
	<form method="post" action="https://secure.braintreepaymentgateway.com/api/transact.php" onsubmit="return validate();">
		<input type="hidden" name="redirect" value="<s:property value="requestString"/>?id=<s:property value="id"/>"/>
		<s:hidden name="hash"></s:hidden>
		<s:hidden name="key_id"></s:hidden>
		<s:hidden name="orderid"></s:hidden>
		<s:hidden name="amount"></s:hidden>
		<s:hidden name="time"></s:hidden>
		<s:hidden name="company"></s:hidden>
		<s:hidden name="customer_vault_id"></s:hidden>
		<s:if test="cc == null">
			<input type="hidden" name="customer_vault" value="add_customer"/>
		</s:if>
		<s:else>
			<input type="hidden" name="customer_vault" value="update_customer"/>
		</s:else>
	
		<s:if test="cc != null">
		<fieldset class="form">
		<h2 class="formLegend">Existing Card</h2>
		<ol>
			<li><label>Type:</label>
				<s:property value="cc.cardType"/><s:if test="!contractor.ccOnFile && contractor.ccExpiration != null"><span style="color:red;" >&nbsp;&nbsp;( Invalid )</span></s:if>
			</li>
			<li><label>Number:</label>
				<s:property value="cc.cardNumber"/>
			</li>
			<li><label>Expires:</label>
				<s:property value="cc.expirationDateFormatted"/>
			</li>
			<li><a href="?id=<s:property value="id"/>&button=delete" class="remove">Remove Card</a></li>
		</ol>
		</fieldset>
		</s:if>
	
		<fieldset class="form">
		<h2 class="formLegend"><span><s:if test="cc == null">Add</s:if><s:else>Replace</s:else>
		 Credit Card</span></h2>
		<ol>
			<li><label>Type:</label>
				<s:radio theme="pics" list="creditCardTypes" name="ccName"/>
			</li>
			<li><label>Card Number:</label>
				<s:textfield name="ccnumber" size="20" />
			</li>
			<li><label>Expiration Date:</label>
				<s:select id="expMonth" list="#{'01':'Jan','02':'Feb','03':'Mar','04':'Apr','05':'May','06':'Jun','07':'Jul','08':'Aug','09':'Sep','10':'Oct','11':'Nov','12':'Dec'}" headerKey="" headerValue="- Month -"></s:select>
				<s:select id="expYear" list="#{'09':2009,10:2010,11:2011,12:2012,13:2013,14:2014,15:2015,16:2016,17:2017,18:2018,19:2019}" headerKey="" headerValue="- Year -"></s:select>
				<s:textfield id="ccexp" name="ccexp" cssStyle="display: none" />
				<span id="ccexpError" class="Red" style="display:none"> </span>
			</li>
		</ol>
		</fieldset>
		<fieldset class="form submit">
			<input type="submit" class="picsbutton positive" name="button" value="Submit"/>
			<br clear="all">
		</fieldset>
	</form>
</s:if>
<br clear="all" /><br/><br/>
<s:if test="permissions.contractor && contractor.status.pendingDeactivated && (contractor.paymentMethodStatusValid || !contractor.mustPayB)">
	<div class="buttons" style="float: right;">
		<a id="next_link" href="ContractorRegistrationFinish.action" class="picsbutton positive">Next &gt;&gt;</a>
	</div>
</s:if>
</body>
</html>
