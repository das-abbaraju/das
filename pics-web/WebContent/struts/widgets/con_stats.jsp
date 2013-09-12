<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:if test="!permissions.insuranceOnlyContractorUser">
	<div style="
		border: 1px #A84D10 solid; 
		background-color: #F6F6F6;
		padding: 5px;
		margin: 3px; 
		width: 200px; 
		text-align: center;
	">
	<a href="ContractorView.action"><s:text name="ContractorStats.link.ShowDetailsPage" /></a>
	</div>
</s:if>
<pics:permission perm="ContractorAdmin">
	<a href="ContractorEdit.action" class="edit right"><s:text name="ContractorStats.link.EditAccountInfo" /></a>
</pics:permission>
<p><label><s:text name="ContractorAccount.name" />:</label> <s:property value="contractor.name" /></p>
<p><label><s:text name="ContractorAccount.address" />:</label> <s:property value="contractor.address" /><br />
<s:property value="contractor.city" />, <s:property value="contractor.countrySubdivision.isoCode" /> <s:property value="contractor.zip" /></p>
<p><label><s:text name="ContractorAccount.created" />:</label> <s:date name="contractor.creationDate" format="%{@com.picsauditing.util.PicsDateFormat@IsoLongMonth}" /></p>
<p><label><s:text name="global.ContactPrimary" />:</label> <s:property value="contractor.primaryContact.name" /></p>
<p><label><s:text name="User.phone" />:</label> <s:property value="contractor.primaryContact.phone" /></p>
<p><label><s:text name="User.email" />:</label> <s:property value="contractor.primaryContact.email" /></p>
<s:if test="contractor.auditor.name != null">
	<p><label><s:text name="ContractorStats.auditor.name" />:</label> <s:property value="contractor.auditor.name" /></p>
	<p><label><s:text name="ContractorStats.auditor.email" />:</label> <s:property value="contractor.auditor.email" /></p>
</s:if>
<s:if test="!permissions.insuranceOnlyContractorUser">
	<p>
		<label><s:text name="ContractorStats.label.CurrentMembershipLevel" />:</label>
		<s:if test="contractor.mustPayB">
			<table>
			<s:iterator value="contractor.fees.keySet()" var="feeClass">
				<s:if test="!contractor.fees.get(#feeClass).currentLevel.free && #feeClass.membership">
					<tr><td colspan="2"><s:property value="contractor.fees.get(#feeClass).currentLevel.fee" />:&nbsp;</td>
					<td class="right"><s:property value="contractor.country.currency.symbol" /><s:property value="contractor.fees.get(#feeClass).currentAmount" /></td>
					<td>&nbsp;<s:property value="contractor.currency"/></td></tr>
				</s:if>
			</s:iterator>
			</table>
		</s:if>
		<s:else>
			<s:text name="ContractorStatsAjax.Free" />
		</s:else>
	</p>
	<s:if test="contractor.mustPayB || (contractor.ccOnFile && creditCard != null)">
		<pics:permission perm="ContractorBilling">
			<a href="ContractorPaymentOptions.action?id=<s:property value="contractor.id" />" class="edit right"><s:text name="ContractorStats.link.EditCreditCardInfo" /></a>
		</pics:permission>
	</s:if>
	<p>
		<label><s:text name="global.CreditCard" />:</label>
		<s:if test="contractor.ccOnFile && creditCard != null">
			<s:property value="creditCard.cardType"/> <s:property value="creditCard.cardNumber.substring(creditCard.cardNumber.length()-4)"/> <s:text name="global.CreditCard.shortExpiration" /> <s:property value="creditCard.expirationDateFormatted"/><br/>
			<s:if test="contractor.renew && contractor.mustPayB && contractor.paymentMethod.creditCard && contractor.ccValid">
				<s:if test="hasUnpaidInvoices">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<s:text name="ContractorStats.message.WillBeChargedOn" ><s:param value="%{chargedOn}" /></s:text></s:if>
				<s:else>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<s:text name="ContractorStats.message.WillRenewOn" ><s:param value="%{contractor.paymentExpires}" /></s:text></s:else>
			</s:if>
		</s:if>
		<s:else>
			<s:text name="ContractorStats.message.NoCreditCard" />
		</s:else>
	</p>

	<s:if test="contractor.renew == false">
		<div class="alert">
			<s:text name="ContractorStats.message.WillExpireOn" ><s:param value="%{contractor.paymentExpires}" /></s:text>
		</div>
	</s:if>
</s:if>

