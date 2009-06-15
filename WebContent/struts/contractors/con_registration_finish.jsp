<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Registration Completion</title>
<meta name="help" content="User_Manual_for_Contractors">
<link rel="stylesheet" type="text/css" href="css/invoice.css"/>
</head>
<body>

<s:if test="contractor.activeB">
	<s:include value="conHeader.jsp"></s:include>
</s:if>
<s:else>
	<s:include value="registrationHeader.jsp"></s:include>
</s:else>

<s:if test="contractor.operators.size == 0">
	<div id="error">You haven't selected any facilities! We cannot process your account until do. <a
		href="ContractorFacilities.action">Click to Add Facilities</a></div>
</s:if>
<s:elseif test="!contractor.paymentMethodStatusValid">
	<div id="error">You didn't add a credit card to your account. You must enter a valid payment method before we can
	process your account. <a href="ContractorPaymentOptions.action">Click to Add a Credit Card</a></div>
</s:elseif>
<s:else>
	<s:if test="complete">
		<div id="info">
			Your account has been registered successfully. A copy of the invoice has been emailed to you.
			<s:if test="contractor.activeB">
				<div class="buttons">
					<a href="Home.action" class="picsbutton positive">Click Here to go to your Home Page</a>
				</div>
			</s:if>
			<s:else>
				<strong>You will have full access to your account once your payment has been received.</strong>
			</s:else>
			<div class="clear"></div>
		</div>
	</s:if>
	<s:else>
		<s:if test="contractor.paymentMethod.check">
			<div id="alert">
				Your payment method is currently set to Check. Your account will be activated as soon as we receive a check from you for
				<strong>$<s:property value="invoice.totalAmount"/></strong>. If you would like to activate your account now,
				<a href="ContractorPaymentOptions.action">Click Here to Add a Credit Card</a>.
			</div>
		</s:if>
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
						the following audits will apply: 

						<br clear="all"/>

						<s:iterator value="auditMenu">
							<s:if test="children.size() > 0">
								<div style="float:left;width: <s:property value="100 / auditMenu.size() * 0.9"/>%">
									<strong style="font-size:16px"><s:property value="name" escape="false"/></strong>
									<ul>
										<s:iterator value="children">
											<li><s:property value="name" escape="false"/></li>
										</s:iterator>
									</ul>
								</div>
							</s:if>
						</s:iterator>

						<br clear="all"/>
						
						<h3>Invoice Summary</h3>
						<br clear="all"/>
						<s:set name="i" value="invoice"/>
						<s:include value="con_invoice_embed.jsp"/>
						<br clear="all"/>
						<input type="submit" class="picsbutton positive" value="Complete My Registration" name="button"/>
						<s:if test="contractor.paymentMethod.creditCard">
							<div id="info">
								Please only click the button once. Your card will be charged $<s:property value="invoice.totalAmount" /> immediately and a receipt will be mailed to you.
							</div>
						</s:if>
						
					</div>
				</s:form>
			</div>
		</s:if>
	</s:else>
</s:else>

</body>
</html>
