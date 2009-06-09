<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Registration Completion</title>
</head>
<body>

<s:include value="registrationHeader.jsp"></s:include>

<s:if test="contractor.operators.size == 0">
	<div id="error">You haven't selected any facilities! We cannot process your account until do. <a
		href="ContractorFacilities.action">Click to Add Facilities</a></div>
</s:if>
<s:elseif test="!contractor.paymentMethodStatusValid">
	<div id="error">You didn't add a credit card to your account. You must enter a valid payment method before we can
	process your account. <a href="ContractorPaymentOptions.action">Click to Add a Credit Card</a></div>
</s:elseif>
<s:else>
</s:else>

<s:if test="!contractor.activeB">
	<div style="color: red">
		<s:form><fieldset class="form">
			Based on the information provided your level of risk for the the work your company performs is <s:property value="contractor.riskLevel"/>.	
			
			Based on the Operators that you have selected:
			<h3>Operators</h3>
			<ul>
				<s:iterator value="contractor.operators">
					<li><s:property value="operatorAccount.name"/></li>
				</s:iterator>
			</ul>
			
			the following audits will apply:
			<h3>Audits</h3>
			<ul>
				<s:iterator value="contractor.audits">
					<li><s:property value="auditType.auditName"/> <s:if test="auditFor != null"> (<s:property value="auditFor"/>) </s:if> - <s:property value="auditType.description"/></li>
				</s:iterator>
			</ul>
			
			need to provide a brief description of each audit
			
			display invoice summary <br/>
			<h3>Invoice</h3>
			<s:iterator value="contractor.invoices">
				<s:property value="dueDate"/> - <s:property value="totalAmount"/>
			</s:iterator>
			<br/>
			<br/>
			
			<input type="submit" class="picsbutton positive" value="Charge Credit Card"/>
			</fieldset>
		</s:form>
	</div>
</s:if>
<s:else>
	<div id="info">
		TODO!!!!
		Show Confirmation page here
		TODO!!!!
	</div>
</s:else>

<!-- 
<div>
<ol>
	<li>We will review your account <s:if test="contractor.newMembershipLevel.amount > 0">and <s:if
			test="contractor.paymentMethod.creditCard">charge your credit card</s:if>
		<s:else>send you an invoice</s:else>
	</s:if> within one business day.</li>
	<li>You must also confirm the email address that you provided us before your account will be activated. (If you
	have a SPAM filter, we suggest you add <b>picsauditing.com</b> to the safe sender domain list. Otherwise, you may not
	receive our automatically generated emails or notices from your operators.)</li>
	<li>After your account is active, you will have access to update your Pre-Qualification Form (PQF) and any other
	information your operators may require.</li>
	<li>If you have any questions, please call us at (800) 506-PICS (7427) or chat with us by click the Chat link
	above</li>
</ol>
</div>-->

<div class="buttons"><a class="positive" href="Login.action?button=logout">Logout</a></div>

</body>
</html>
