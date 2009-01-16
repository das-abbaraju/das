<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="../exception_handler.jsp"%>
<html>
<head>
<title>Payment Options</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css" />
</head>
<body>

<s:include value="../actionMessages.jsp"></s:include>

<form method="post" action="https://secure.braintreepaymentgateway.com/api/transact.php">
	<s:hidden name="hash"></s:hidden>
	<s:hidden name="key_id"></s:hidden>
	<s:hidden name="orderid"></s:hidden>
	<s:hidden name="amount"></s:hidden>
	<s:hidden name="time"></s:hidden>
	<s:hidden name="customer_vault_id"></s:hidden>
	<input type="radio" name="customer_vault" value="add_customer"/> Add
	<input type="radio" name="customer_vault" value="update_customer"/> Update
	<input type="radio" name="customer_vault" value="delete_customer"/> Delete
	Report Type <input type="text" name="report_type" value="customer_vault"/>
	<input type="hidden" name="redirect" value="http://localhost:8080/picsWeb2/ContractorPaymentOptions.action?id=<s:property value="id"/>"/>
<br clear="all" />

<s:hidden name="id" />
	<fieldset class="form">
	<s:if test="paymentMethod == 'Credit Card'">
	<legend><span>Credit Card Details</span></legend>
	<ol>
		<li><label>Contractor Name:</label>
			<s:property value="contractor.name" />
		</li>
		<li><label>Annual Membership Fee:</label>
			$<s:property value="contractor.newBillingAmount" />
		</li>
		<li><label>Renewal Date:</label>
			<s:date name="contractor.paymentExpires" format="MMM d, yyyy" />
		</li>
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
