<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Payment Options</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css" />
</head>
<body>

<s:include value="conHeader.jsp"></s:include>
<form method="post" action="https://secure.braintreepaymentgateway.com/api/transact.php">
	<input id="key_id" type="hidden" value="776320" name="key_id"/>
	<input id="orderid" type="hidden" value="12345" name="orderid"/>
	<input id="ccnumber" type="hidden" value="4111111111111111" name="ccnumber"/>
	<input id="ccexp" type="hidden" value="1010" name="ccexp"/>
	<input id="amount" type="hidden" value="10.00" name="amount"/>
	<input id="time" type="hidden" value=20090116173111 name="time"/>
	<input id="hash" type="hidden" value="b55e7e5d73fabf16525896c760f8d115" name="hash"/>	
	<input id="customer_vault" type="hidden" value="add_customer" name="customer_vault"/>
	<input id="customer_vault_id" type="hidden" value="3256" name="customer_vault_id"/>
	<input id="redirect" type="hidden" value="http://localhost:8080/picsWeb2/ContractorPaymentOptions.action?id=<s:property value="id"/>" name="redirect"/>
<br clear="all" />

<s:hidden name="id" />
	<fieldset class="form">
	<legend><span>Primary Address</span></legend>
	<ol>
		<li><label>Address:</label>
			<s:textfield name="contractor.address" size="35" />
		</li>
		<li>
		<div class="buttons">
			<!-- <button class="positive" name="button" type="submit" value="Save">Save</button>  -->
			<input type="submit" class="positive" name="button" value="Submit"/>
		</div>
		</li>
		<s:if test="response_code == '100'">
		<li>
			Successful Transaction!!
		</li>
		</s:if>
	</ol>
	</fieldset>	
	<br clear="all">
</form>

</body>
</html>
