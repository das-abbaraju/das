<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Registration Completion</title>
<link rel="stylesheet" type="text/css" href="css/invoice.css"/>
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
	<div>
		<s:form>
			<s:hidden name="id" value="%{contractor.id}"/>
			<div>
				Based on the information provided your level of risk for the the work your company performs is <strong><s:property value="contractor.riskLevel"/></strong>.	<br/>
				
				Based on the Operators that you have selected:
					<s:iterator value="contractor.operators" status="stat">
						<s:if test="#stat.last">
							and
						</s:if>
						<s:property value="operatorAccount.name"/>,
					</s:iterator>
				the following audits will apply: <br clear="all"/>
				<s:iterator value="auditListMap">
					<div style="float:left; width: <s:property value="100 / auditListMap.size() * 0.9"/>%">
						<ul>
							<s:iterator value="value">
								<li><strong><s:property value="auditType.auditName"/></strong> <s:if test="auditFor != null"> (<s:property value="auditFor"/>) </s:if> - <s:property value="auditType.description"/></li>
							</s:iterator>
						</ul>
					</div>
				</s:iterator>
				<br clear="all"/>
				
				<h3>Invoice Summary</h3>
				<a href="InvoiceDetail.action?invoice.id=<s:property value="invoice.id"/>" target="_BLANK">Invoice #<s:property value="invoice.id"/></a>
				<table class="allborder">
					<tr>
						<th>Item &amp; Description</th>
						<th width="100px">Fee Amount</th>
					</tr>
					<s:iterator value="invoice.items">
						<tr>
							<td>
								<s:property value="invoiceFee.fee" />
								<span style="color: #444; font-style: italic; font-size: 10px;">
								<s:if test="invoiceFee.feeClass == 'Activation'">effective
									<s:if test="paymentExpires == null"><s:date name="invoice.creationDate" format="MMM d, yyyy" /></s:if>
									<s:else><s:date name="paymentExpires" /></s:else>
								</s:if>
								<s:if test="invoiceFee.feeClass == 'Membership' && paymentExpires != null">
									expires <s:date name="paymentExpires" format="MMM d, yyyy"/>
								</s:if>
								</span>
							</td>
							<td class="right">
								$<s:property value="amount" /> USD
							</td>
						</tr>
					</s:iterator>
					<tr>
						<th class="big right">Invoice Total</th>
						<td class="big right">$<s:property value="invoice.totalAmount" /> USD</td>
					</tr>
				</table>

				<s:if test="contractor.paymentMethod.creditCard">
					<input type="submit" class="picsbutton positive" value="Charge Credit Card" name="button"/>
				</s:if>
				<s:else>
					<div id="alert">
						Your payment method is currently set to Check. Your account will be activated as soon as we recieve a check from you for
						<strong>$<s:property value="invoice.totalAmount"/></strong>. If you would like to activate your account now,
						<a href="ContractorPaymentOptions.action">Click Here to Add a Credit Card</a>.
					</div>
				</s:else>
			</div>
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

</body>
</html>
