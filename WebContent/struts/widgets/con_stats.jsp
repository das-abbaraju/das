<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<div style="
	border: 1px #A84D10 solid; 
	background-color: #F6F6F6;
	padding: 5px;
	margin: 3px; 
	width: 200px; 
	text-align: center;
"><a href="ContractorView.action">Show Details Page</a></div>
<pics:permission perm="ContractorAdmin">
	<a href="ContractorEdit.action" class="edit right">Edit Account Info</a>
</pics:permission>
<p><label>Account Name:</label> <s:property value="contractor.name" /></p>
<p><label>Address:</label> <s:property value="contractor.address" /><br />
<s:property value="contractor.city" />, <s:property value="contractor.state" /> <s:property value="contractor.zip" /></p>
<p><label>Account Since:</label> <s:date name="contractor.creationDate" format="MMM d, yyyy" /></p>
<p><label>Primary Contact:</label> <s:property value="contractor.primaryContact.name" /></p>
<p><label>Primary Phone:</label> <s:property value="contractor.primaryContact.phone" /></p>
<p><label>Primary Email:</label> <s:property value="contractor.primaryContact.email" /></p>
<s:if test="contractor.auditor.name != null">
	<p><label>Assigned Representative:</label> <s:property value="contractor.auditor.name" /></p>
	<p><label>Representative's Email:</label> <s:property value="contractor.auditor.email" /></p>
</s:if>
<p>
	<label>Current Level:</label>
	<s:if test="contractor.mustPayB">$<s:property value="contractor.membershipLevel.amount" /> USD <br />
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</s:if><s:property value="contractor.membershipLevel.fee" />
</p>
<s:if test="contractor.mustPayB || (contractor.ccOnFile && creditCard != null)">
	<pics:permission perm="ContractorBilling">
		<a href="ContractorPaymentOptions.action?id=<s:property value="contractor.id" />" class="edit right">Edit Credit Card Info</a>
	</pics:permission>
</s:if>
<p>
	<label>Credit Card:</label>
	<s:if test="contractor.ccOnFile && creditCard != null">
		<s:property value="creditCard.cardType"/> <s:property value="creditCard.cardNumber.substring(creditCard.cardNumber.length()-4)"/> exp <s:property value="creditCard.expirationDateFormatted"/><br/>
		<s:if test="contractor.renew && contractor.mustPayB && contractor.paymentMethod.creditCard && contractor.ccValid">
			<s:if test="hasUnpaidInvoices">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Will be charged on <s:property value="chargedOn" /></s:if>
			<s:else>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Your account will renew on <s:property value="contractor.paymentExpires"/></s:else>
		</s:if>
	</s:if>
	<s:else>
		No Credit Card on File
	</s:else>
</p>

<s:if test="contractor.renew == false">
	<div class="alert">
		Your account is not set to renew and will expire on 
		<s:property value="contractor.paymentExpires"/>.
	</div>
</s:if>

