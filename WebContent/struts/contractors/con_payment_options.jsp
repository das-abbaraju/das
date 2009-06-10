<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="contractor.name" /> - Payment Method</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<script type="text/javascript" src="js/prototype.js"></script>
<script language="JavaScript">
function updateExpDate() {
	$('ccexpError').hide();
	if ($F('expMonth') != "" && $F('expYear') != "") {
		$('ccexp').value = $F('expMonth') + $F('expYear');
		return true;
	}
	$('ccexpError').innerHTML = "* Please enter your card's expiration date";
	$('ccexpError').show();
	return false;
}

function showPaymentMethodOption(elm) {
	var option =  $F(elm);
	if(option == 'Check') {
		$('creditcard_show').hide();
		$('check_show').show();
	}
	if(option == 'CreditCard') {
		$('check_show').hide();
		$('creditcard_show').show();
	}
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


<s:if test="permissions.contractor">
	<s:if test="contractor.operators.size == 0">
		<div id="alert">
			You have not selected any facilities. No operators will be able to view your account until you do. 
			<a href="ContractorFacilities.action?id=<s:property value="contractor.id"/>">Click to update your operator listings.</a>
		</div>
	</s:if>
	<s:else>
		<div id="info">
			<s:if test="contractor.activeB">
				As an improvement, you may now pay by credit card.  Even though you are providing your credit card information at this time, your card will not be charged until the next billing date.  PICS will email you 7 days prior to renewal before any charges are applied.  If you have questions, contact PICS Accounting any time at (800) 506-7427 x 708.
			</s:if>
			<s:elseif test="contractor.newMembershipLevel.amount == 0">
				<s:property value="contractor.operators.size"/>
				You are currently at the free level and do not owe any membership dues.  
				However, a valid credit card is required to maintain an account with PICS.  
				Only if you upgrade your account will your card be charged, and only after PICS notifies you at 
				least 7 days before the charge.
			</s:elseif>
			<s:else>
				Please enter your credit card information, which will expedite the registration process.  Your membership is valid for 12 months from the charge date.  An upgrade fee will be charged only if you add additional facilities to your account. If you have questions, contact PICS Accounting anytime at (800) 506-7427 x 708.
			</s:else>
		</div>
	</s:else>
</s:if>

<s:form id="save" method="POST">
<fieldset class="form">
<legend><span>Membership Details</span></legend>
<ol>
	<li><label>Company Name:</label>
		<s:property value="contractor.name" />
	</li>
<s:if test="contractor.newMembershipLevel.amount > 0">
	<li><label>Payment Method:</label>
		<s:if test="contractor.newMembershipLevel.amount < 500 && !permissions.admin">
			<s:radio list="#{'Check':'Check','CreditCard':'Credit Card'}" name="contractor.paymentMethod" theme="pics" disabled="true"/>
		</s:if>
		<s:else>
			<s:radio list="paymentMethodList" name="paymentMethod" value="%{contractor.paymentMethod}" theme="pics" onclick="javascript : showPaymentMethodOption(this); return true;"/>
		</s:else>
		</li>
		<li>
			<s:if test="contractor.paymentMethod.creditCard">
				<s:set name="creditcard_show" value="'inline'"/>								
				<s:set name="check_show" value="'none'"/>
			</s:if>
			<s:else>
				<s:set name="creditcard_show" value="'none'"/>								
				<s:set name="check_show" value="'inline'"/>
			</s:else>
			<span id="creditcard_show" style="display: <s:property value="#attr.creditcard_show"/>;"> Credit card payment is required for billing amounts less than $500.<br/></span>
			<span id="check_show" style="display: <s:property value="#attr.check_show"/>;"> Your invoice will be generated on <s:date name="@com.picsauditing.PICS.DateBean@getFirstofMonth(contractor.paymentExpires,-1)" format="MMM d, yyyy"/> and emailed to <s:property value="contractor.email"/> 
			<s:if test="!@com.picsauditing.util.Strings@isEmpty(contractor.billingEmail) && !contractor.email.equals(contractor.billingEmail)">	and <s:property value="contractor.billingEmail"/></s:if> with payment terms of net 30.</span>
	</li>
	<s:if test="contractor.activeB">
		<li><label>Next Billing Date:</label> <s:date
			name="contractor.paymentExpires" format="MMM d, yyyy" /></li>
		<li><label>Next Billing Amount:</label> $<s:property
			value="contractor.newMembershipLevel.amount" /> USD</li>
	</s:if>
	<s:else>
		<li><label>Membership Fee:</label> $<s:property
			value="contractor.newMembershipLevel.amount" /> USD</li>
		<li><label>Activation Fee:</label> $<s:property value="activationFee.amount"/> USD</li>
		<li><label>Total:</label> $<s:property value="activationFee.amount+contractor.newMembershipLevel.amount"/> USD </li>
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
<s:if test="contractor.newMembershipLevel.amount > 500">
<div class="buttons">
	<input type="submit" class="picsbutton" name="button" value="Update"/>
</div>
</s:if>
</ol>
</fieldset>
</s:form>

<s:if test="paymentMethod.creditCard">
	<form method="post" action="https://secure.braintreepaymentgateway.com/api/transact.php" onsubmit="return updateExpDate();">
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
			<li><label>Number:</label>
				<s:textfield name="ccnumber" size="20" />
			</li>
			<li><label>Expiration Date:</label>
				<s:select id="expMonth" list="#{'01':'Jan','02':'Feb','03':'Mar','04':'Apr','05':'May','06':'Jun','07':'Jul','08':'Aug','09':'Sep','10':'Oct','11':'Nov','12':'Dec'}" headerKey="" headerValue="- Month -"></s:select>
				<s:select id="expYear" list="#{'09':2009,10:2010,11:2011,12:2012,13:2013,14:2014,15:2015,16:2016,17:2017,18:2018,19:2019}" headerKey="" headerValue="- Year -"></s:select>
				<s:textfield id="ccexp" name="ccexp" cssStyle="display: none" />
				<span id="ccexpError" class="Red" style="display:none"> </span>
			</li>
			<li>
			<div class="buttons">
				<input type="submit" class="picsbutton positive" name="button" value="Submit"/>
			</div>
			</li>
		</ol>
		</fieldset>	
	</form>
</s:if>
<s:else>
	<fieldset class="form">
	<legend><span>Check</span></legend>
	<ol>
	</ol>
	</fieldset>	
</s:else>
<br clear="all" /><br/><br/>

<s:if test="permissions.contractor && !contractor.activeB && contractor.paymentMethodStatusValid">
	<div class="buttons" style="float: right;">
		<a href="ContractorRegistrationFinish.action" class="positive">Next</a>
	</div>
</s:if>
</body>
</html>
