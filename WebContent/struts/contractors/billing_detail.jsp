<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="contractor.name" /> <s:text name="%{scope}.title" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />

<s:include value="../jquery.jsp"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css" />

</head>
<body>
<s:include value="conHeader.jsp"></s:include>

<s:if test="contractor.qbListID.startsWith('NOLOAD')">
	<div class="alert">This contractor is NOT set to sync with QuickBooks</div>
</s:if>
<s:if test="contractor.acceptsBids">
	<div class="alert">This is a BID-ONLY Contractor Account.</div>
</s:if>

<table width="100%">
	<tr>
		<td style="vertical-align: top; width: 48%;">
		<fieldset class="form">
		<h2 class="formLegend"><s:text name="%{scope}.Info.heading" /></h2>
		<ol>
			<li><label><s:text name="%{scope}.Info.Active" />:</label> <s:property value="contractor.status"/></li>
			<li><label title="The Date the Account was Created."><s:text name="%{scope}.Info.RegistrationDate" />:</label> <s:date
				name="contractor.creationDate" format="MMM d, yyyy" /></li>
			<li><label title="The Date the Activation/Reactivation Fee was Paid."><s:text name="%{scope}.Info.ActivationDate" />:</label> <s:date
				name="contractor.membershipDate" format="MMM d, yyyy" /></li>
			<li><label><s:text name="%{scope}.Info.WillBeRenewed" />:</label> <s:if test="contractor.renew">Yes</s:if> <s:else>No</s:else></li>
			<li><label><s:text name="%{scope}.Info.RenewalDate" />:</label> <s:date name="contractor.paymentExpires" format="MMM d, yyyy" /></li>
			<li><label><s:text name="%{scope}.Info.PaymentMethod" />:</label> <s:property value="contractor.paymentMethod.description" /></li>
			<li><label><s:text name="%{scope}.Info.CreditCardOnFile" />?</label> <s:if test="contractor.ccOnFile">Yes</s:if><s:elseif test="!contractor.ccOnFile && contractor.ccExpiration != null"><span style="color:red;" >Invalid</span></s:elseif><s:else>No</s:else></li>
		</ol>
		</fieldset>

		<fieldset class="form bottom">
		<h2 class="formLegend"><s:text name="%{scope}.Facilities.heading" /></h2>
		<ol>
			<li><label><s:text name="%{scope}.Facilities.RequestedBy" />:</label> <s:property value="requestedBy.name" /></li>
			<li><label><s:text name="%{scope}.Facilities.RiskLevel" />:</label> <s:property value="contractor.riskLevel" /></li>
			<li><label><s:text name="%{scope}.Facilities.Facilities" />:</label> <s:property value="contractor.payingFacilities" /> paying operator(s)<br />
			<br />
			<ul style="position: relative; left: 1em; list-style-type: disc;">
				<s:iterator value="contractor.nonCorporateOperators">
					<s:if test="operatorAccount.status.activeDemo">
						<li>
							<s:if test="permissions.admin">
								<a href="AuditOperator.action?oID=<s:property value="operatorAccount.id" />">
									<s:property value="operatorAccount.name" />
								</a>
							</s:if>
							<s:else>
								<a href="ContractorFlag.action?opID=<s:property value="operatorAccount.id" />">
									<s:property value="operatorAccount.name" />
								</a>
							</s:else>
						</li>
					</s:if>
				</s:iterator>
			</ul>
			</li>
			<li><label><s:text name="%{scope}.Facilities.ViewOperators" />:</label> <a href="ContractorFacilities.action?id=<s:property value="id" />"><s:text name="%{scope}.Facilities.Facilities" /></a>
			</li>
			<li><label><s:text name="%{scope}.Facilities.LastUpgradeDate" />:</label> <s:date name="contractor.lastUpgradeDate" format="MMM d, yyyy" /></li>
		</ol>
		</fieldset>
		</td>
		<td style="width: 5px;"></td>
		<td style="vertical-align: top; width: 48%;">
		<fieldset class="form">
		<h2 class="formLegend"><s:text name="%{scope}.Invoicing.heading" /></h2>
		<ol>
			<li><label><s:text name="%{scope}.Invoicing.CurrentBalance" />:</label> <s:property value="contractor.balance" /> <s:property value="contractor.currencyCode"/> <s:if
				test="contractor.balance > 0">
				<pics:permission perm="Billing" type="Edit">
					<a href="PaymentDetail.action?id=<s:property value="id" />" class="add">Make a Payment</a>
				</pics:permission>
			</s:if></li>
			<li><label><s:text name="%{scope}.Invoicing.BillingStatus" />:</label> <s:property value="contractor.billingStatus" /></li>
			<li><label><s:text name="%{scope}.Invoicing.MustPay" />:</label> <s:property value="contractor.mustPay" /></li>
			<li><label><s:text name="%{scope}.Invoicing.CurrentLevel" />:</label> <s:property value="contractor.membershipLevel.amount" /> <s:property value="contractor.currencyCode"/><br>
			<s:property value="contractor.membershipLevel.fee" /></li>
			<s:if test="contractor.newMembershipLevel != contractor.membershipLevel">
				<li><label>New Level:</label> <s:property value="contractor.newMembershipLevel.amount" /> <s:property value="contractor.currencyCode"/> <br>
				<s:property value="contractor.newMembershipLevel.fee" /></li>
			</s:if>

		</ol>
		</fieldset>

		<s:if test="permissions.admin">
			<fieldset class="form">
			<h2 class="formLegend">Create Invoice</h2> <s:form id="save" method="POST"
				enctype="multipart/form-data">
				<s:hidden name="id" />
				<ol>
					<s:iterator value="invoiceItems">
						<s:if test="invoiceFee != null">
							<li><label><s:property value="invoiceFee.fee" />:</label> <s:property value="amount" /> <s:property value="contractor.currencyCode"/></li>
						</s:if>
						<s:else>
							<li><label><s:property value="description" />:</label> <s:property value="amount" /> <s:property value="contractor.currencyCode"/></li>
						</s:else>
					</s:iterator>
					<li><label>Total:</label> <s:property value="invoiceTotal" /> <s:property value="contractor.currencyCode"/></li>
					<pics:permission perm="Billing">
						<li>
							<div><input type="submit" class="picsbutton positive" name="button" value="Create" /></div>
						</li>
					</pics:permission>
				</ol>
			</s:form> <s:if test="contractor.billingStatus == 'Current' && !contractor.status.activeDemo">
				<s:form>
					<s:hidden name="id" />
					<div><input type="submit" class="picsbutton positive" name="button" value="Activate" /></div>
				</s:form>
			</s:if></fieldset>
		</s:if>
		<fieldset class="form bottom">
		<h2 class="formLegend"><s:text name="%{scope}.TransactionHistory.heading" /></h2>
		<ol>
			<li>
			<table class="report">
				<thead>
					<tr>
						<th>Transaction</th>
						<th>#</th>
						<th>Date</th>
						<th>Amount</th>
						<th>Outstanding</th>
						<s:if test="permissions.admin">
							<th>Status</th>
						</s:if>	
					</tr>
				</thead>
				<tbody>
					<s:iterator value="transactions">
						<s:set name="url" value="" />
						<s:if test="class.simpleName == 'Invoice'">
							<s:set name="url" value="'InvoiceDetail.action?invoice.id='+id" />
						</s:if>
						<s:elseif test="class.simpleName == 'Payment'">
							<pics:permission perm="Billing">
								<s:set name="url" value="'PaymentDetail.action?payment.id='+id" />
							</pics:permission>
						</s:elseif>
						<tr>
							<td><s:property value="class.simpleName" /></td>
							<td class="right">
								<s:if test="#url.length() > 0">
									<a href="<s:property value="#url" />"><s:property value="id" /></a>
								</s:if>
								<s:else>
									<s:property value="id" />
								</s:else>
							</td>
							<td class="right"><s:date name="creationDate" format="M/d/yy" /></td>
							<td class="right"><s:property value="totalAmount" /> <s:property value="currency"/></td>
							<td class="right">
								<s:if test="class.simpleName.equals('Payment') && status.toString() == 'Unpaid' && balance > 0">-</s:if><s:property value="balance" /> <s:property value="contractor.currencyCode"/>
							</td>
							<s:if test="permissions.admin">
								<td><s:property value="status"/></td>
							</s:if>
						</tr>
					</s:iterator>
				</tbody>
			</table>
			</li>
		</ol>
		</fieldset>
		</td>
	</tr>
</table>
 
<div id="notesList"><s:include value="../notes/account_notes_embed.jsp"></s:include></div>

</body>
</html>