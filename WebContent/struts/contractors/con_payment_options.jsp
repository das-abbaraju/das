<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="../exception_handler.jsp"%>
<html>
<head>
<title><s:property value="contractor.name" /> - Payment Method</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css" />
</head>
<body>

<h1><s:property value="contractor.name" /><span id="sub">Payment Method</span></h1>

<s:include value="../actionMessages.jsp"></s:include>

<s:if test="paymentMethod == 'Credit Card'">
<form method="post" action="https://secure.braintreepaymentgateway.com/api/transact.php">
	<input type="hidden" name="redirect" value="<s:property value="requestURI"/>?id=<s:property value="id"/>"/>
	<s:hidden name="hash"></s:hidden>
	<s:hidden name="key_id"></s:hidden>
	<s:hidden name="orderid"></s:hidden>
	<s:hidden name="amount"></s:hidden>
	<s:hidden name="time"></s:hidden>
	<s:hidden name="customer_vault_id"></s:hidden>
	<s:if test="cc == null'">
		<input type="hidden" name="customer_vault" value="add_customer"/>
	</s:if>
	<s:else>
		<input type="hidden" name="customer_vault" value="update_customer"/>
	</s:else>

	<fieldset class="form">
	<legend><span>Membership Details</span></legend>
	<ol>
		<li><label>Annual Membership Fee:</label>
			$<s:property value="contractor.newBillingAmount" />
		</li>
		<li><label>Payment Expires:</label>
			<s:date name="contractor.paymentExpires" format="MMM d, yyyy" />
		</li>
	</ol>
	<legend><span>Credit Card Details</span></legend>
	<ol>
	<s:if test="contractor.paymentMethodStatusValid">
		<li><label>Existing Card:</label>
			<s:radio theme="pics" list="creditCardTypes" name="ccName"/>
		</li>
	</s:if>
		<li><label>Credit Card Type:</label>
			<s:radio theme="pics" list="creditCardTypes" name="ccName"/>
		</li>
		<li><label>Credit Card Number:</label>
			<s:textfield name="ccnumber" value="4111111111111111" size="20" />
		</li>
		<li><label>Exp Date:</label>
			<s:textfield name="ccexp" value="1010" size="10" />
		</li>
		<li>
		<div class="buttons">
			<button class="positive" name="button" type="submit" value="Submit">Submit</button>
		</div>
		</li>
	</ol>
	</s:if>
	<s:else>
	<legend><span>PICS Credit Details</span></legend>
	<ol>
		<li><label>Contractor Name:</label>
			<s:property value="contractor.name" />
		</li>
		<li><label>Membership Fee:</label>
			$<s:property value="contractor.newBillingAmount" />
		</li>
		<li><label>Membership Date:</label>
			<s:date name="contractor.paymentExpires" format="MMM d, yyyy" />
		</li>
		<li>
		<div class="buttons">
			<button class="positive" name="button" type="submit" value="Submit">Submit</button>
		</div>
		</li>
		<s:if test="response == '1'">
		<li>
			<s:property value="responsetext" />
		</li>
		</s:if>
	</ol>
	</s:else>
	</fieldset>	
	<br clear="all">
</form>

</body>
</html>
