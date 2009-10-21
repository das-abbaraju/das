<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="contractor.name" /> - Payment Method</title>
<meta name="help" content="User_Manual_for_Contractors">
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/invoice.css" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
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
<s:if test="permissions.contractor && !contractor.activeB">
	<s:include value="registrationHeader.jsp"></s:include>
</s:if>
<s:else>
<s:include value="conHeader.jsp"></s:include>
</s:else>

<s:include value="../actionMessages.jsp"></s:include>

<s:if test="contractor.operators.size == 0">
	<div id="alert">
		You have not selected any facilities. No operators will be able to view your account until you do. 
		<a href="ContractorFacilities.action?id=<s:property value="contractor.id"/>">Click to update your operator listings.</a>
	</div>
</s:if>
<s:elseif test="contractor.newMembershipLevel.amount == 0">
	<div id="alert">
		You are currently at the free level and do not owe any membership dues.  
		However, a valid credit card is required to maintain an account with PICS.  
		Only if you upgrade your account will your card be charged, and only after PICS notifies you at 
		least 7 days before the charge.
	</div>
</s:elseif>
<s:elseif test="contractor.activeB && !contractor.paymentMethodStatusValid">
		<div id="info">
			As an improvement, you may now pay by credit card.  Even though you are providing your credit card information at this time, your card will not be charged until the next billing date.  PICS will email you 7 days prior to renewal before any charges are applied.  If you have questions, contact PICS Accounting any time at (800) 506-7427 x 708.
		</div>
</s:elseif>
<s:if test="!contractor.paymentMethod.creditCard">
	<div id="info">PICS will email each invoice. Please make sure your contact information is updated.</div>
</s:if>
<s:if test="contractor.balance > 0">
	<div id="alert">
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
<legend><span>Membership Details</span></legend>
<ol>
<s:if test="contractor.newMembershipLevel.amount > 0">
	<li><label>Payment Method:</label>
		<s:property value="contractor.paymentMethod.description"/><br/>
		<s:if test="contractor.paymentMethod.creditCard && contractor.newMembershipLevel.amount < 500">
			<i>Please Note: Credit Card payment is required for memberships under $500.</i>
		</s:if>
	</li>

	<s:if test="contractor.activeB">
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
<li><label>&nbsp;</label>
	<a href="#" onClick="window.open('privacy_policy.jsp','name','toolbar=0,scrollbars=1,location=0,statusbar=0,menubar=0,resizable=1,width=500,height=500'); return false;">
	Privacy Policy </a> |
	<a href="#" onClick="window.open('refund_policy.jsp','name','toolbar=0,scrollbars=1,location=0,statusbar=0,menubar=0,resizable=1,width=500,height=500'); return false;">
	Refund Policy </a> 							
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

<s:if test="contractor.paymentMethod.creditCard">
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
		<legend><span>Existing Card</span></legend>
		<ol>
			<li><label>Type:</label>
				<s:property value="cc.cardType"/>
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
		<legend><span><s:if test="cc == null">Add</s:if><s:else>Replace</s:else>
		 Credit Card</span></legend>
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
			<li>
			<div>
				<input type="submit" class="picsbutton positive" name="button" value="Submit"/>
				<br clear="all">
			</div>
			</li>
		</ol>
		</fieldset>
	</form>
</s:if>

<br clear="all" /><br/><br/>
<s:if test="permissions.contractor && !contractor.activeB && contractor.paymentMethodStatusValid">
	<div class="buttons" style="float: right;">
		<a href="ContractorRegistrationFinish.action" class="picsbutton positive">Next &gt;&gt;</a>
	</div>
</s:if>
</body>
</html>
