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
<h1><s:property value="contractor.name" /> <span class="sub">Payment Method</span></h1>

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
		<li><label>Annual Fee:</label>
			$<s:property value="contractor.newBillingAmount" />
		</li>
		<li><label>Membership Expires:</label>
			<s:date name="contractor.paymentExpires" format="MMM d, yyyy" />
		</li>
	</ol>
	</fieldset>
	
	<s:if test="contractor.paymentMethodStatusValid">
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
	<legend><span>Add/Update Credit Card</span></legend>
	<ol>
		<li><label>Type:</label>
			<s:radio theme="pics" list="creditCardTypes" name="ccName"/>
		</li>
		<li><label>Number:</label>
			<s:textfield name="ccnumber" value="4111111111111111" size="20" />
		</li>
		<li><label>Expiration Date:</label>
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
			<s:select list="#{1:1,2:2,3:3}"></s:select>
			<s:date name="contractor.paymentExpires" format="MMM d, yyyy" />
		</li>
		<li>
		<div class="buttons">
			<button class="positive" name="button" type="submit" value="Submit">Submit</button>
		</div>
		</li>
	</ol>
	</s:else>
	</fieldset>	
	<br clear="all">
</form>

</body>
</html>
