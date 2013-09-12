<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="creditCard == null">
	<li class="method_cc" <s:if test="!method.creditCard">style="display: none"</s:if>>
		This contractor does not have a credit card on file. <a href="ContractorPaymentOptions.action?id=<s:property value="id"/>">Click here to enter a credit card</a>.
	</li>
</s:if>
<s:else>
	<li class="method_cc" <s:if test="!method.creditCard">style="display: none"</s:if>><label>Type:</label>
		<s:property value="creditCard.cardType" />
	</li>
	<li class="method_cc" <s:if test="!method.creditCard">style="display: none"</s:if>><label>Number:</label>
		<s:property value="creditCard.cardNumber" />
	</li>
	<li class="method_cc" <s:if test="!method.creditCard">style="display: none"</s:if>><label>Expiration:</label>
		<s:property value="creditCard.expirationDate" />
	</li>
</s:else>