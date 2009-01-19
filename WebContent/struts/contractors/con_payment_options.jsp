<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="../exception_handler.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><s:property value="contractor.name" /> - Payment Method</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
</head>
<body>
<div id="main">
<div id="bodyholder">
<div id="content">

<div align="center"><a href="javascript: window.close();">Close Window</a></div>
<s:include value="../actionMessages.jsp"></s:include>

<fieldset class="form">
<legend><span>Membership Details</span></legend>
<ol>
	<li><label>Company Name:</label>
		<s:property value="contractor.name" />
	</li>
	<li><label>Annual Fee:</label>
		$<s:property value="contractor.newBillingAmount" />
	</li>
	<li><label>Membership Expires:</label>
		<s:date name="contractor.paymentExpires" format="MMM d, yyyy" />
	</li>
</ol>
</fieldset>

<s:if test="paymentMethod == 'Credit Card'">

<form method="post" action="https://secure.braintreepaymentgateway.com/api/transact.php">
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
	</fieldset>	
</form>
</s:if>
<s:else>
	<fieldset class="form">
	<legend><span>Check</span></legend>
	<ol>
		<li><label>Membership Fee:</label>
			$<s:property value="contractor.newBillingAmount" />
		</li>
	</ol>
	</fieldset>	
</s:else>
<br clear="all" /><br/><br/>
</div>
</div>
</div>
</body>
</html>
