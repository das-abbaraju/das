<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="contractor.name" /> - Payment Method</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<script type="text/javascript" src="js/prototype.js"></script>
<script language="JavaScript">
function updateExpDate() {
	$('ccexp').value = $F('expMonth') + $F('expYear');
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
	<div id="info">
		<s:if test="contractor.activeB">
			As an improvement, you may now pay by credit card.  Even though you are providing your credit card information at this time, your card will not be charged until the next billing date.  PICS will email you 7 days prior to renewal before any charges are applied.  If you have questions, contact PICS Accounting any time at (800) 506-7427 x 708.
		</s:if>
		<s:else>
			Please enter your credit card information, which will expedite the registration process.  Your membership is valid for 12 months from the charge date.  An upgrade fee will be charged only if you add additional facilities to your account.  PICS will email you 7 days prior to any charge. If you have questions, contact PICS Accounting anytime at (800) 506-7427 x 708.
		</s:else>
	</div>
</s:if>

<fieldset class="form">
<legend><span>Membership Details</span></legend>
<ol>
	<li><label>Company Name:</label>
		<s:property value="contractor.name" />
	</li>
<s:if test="contractor.newMembershipLevel.amount > 0">
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

</ol>
</fieldset>


<s:if test="paymentMethod.creditCard">
	<form method="post" action="https://secure.braintreepaymentgateway.com/api/transact.php" onsubmit="updateExpDate();">
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
				<s:select id="expMonth" list="#{'01':'Jan','02':'Feb','03':'Mar','04':'Apr','05':'May','06':'Jun','07':'Jul','08':'Aug','09':'Sep','10':'Oct','11':'Nov','12':'Dec'}"></s:select>
				<s:select id="expYear" list="#{'09':2009,10:2010,11:2011,12:2012,13:2013,14:2014,15:2015,16:2016,17:2017,18:2018,19:2019}"></s:select>
				<s:textfield id="ccexp" name="ccexp" cssStyle="display: none" />
			</li>
			<li>
			<div class="buttons">
				<button class="positive" name="button" type="submit" value="Submit">Submit</button>
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
