<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="contractor.name" /> - Payment <s:property value="payment.id" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=20091105" />
<link rel="stylesheet" type="text/css" media="all" href="css/reports.css?v=20091105" />
<link rel="stylesheet" type="text/css" media="all" href="css/notes.css?v=20091105" />

<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/payment_detail.js?v=20091105"></script>
<script type="text/javascript">
	<s:if test="payment == null">
		var payment_amountApplied = 0.00;
		var totalLocked = false;
	</s:if>
	<s:else>
		var payment_amountApplied = <s:property value="payment.amountApplied"/>;
		var totalLocked = true;
	</s:else>
</script>

<style type="text/css">
fieldset.form ol {
	margin-top: 1em;
}
</style>
</head>
<body>
<s:include value="conHeader.jsp"></s:include>

<s:if test="permissions.admin && payment.qbSync">
	<div class="alert" class="noprint">This payment is still waiting to be synced with QuickBooks!</div>
</s:if>

<s:if test="payment == null && method.creditCard">
	<s:iterator value="contractor.payments">
		<s:if test="status.unpaid">
			<div class="alert">This contractor still has an <a
				href="PaymentDetail.action?payment.id=<s:property value="id"/>">open credit</a>. Please apply that credit before
			collecting any new payments.</div>
		</s:if>
	</s:iterator>
</s:if>

<s:if test="payment.status.toString() == 'Void'">
	<div class="alert">This payment was canceled.</div>
</s:if>

<s:form onsubmit="cleanPaymentMethods()">
	<s:hidden name="id" />
	<s:if test="payment != null && payment.id > 0">
		<s:hidden name="payment.id" />
		<fieldset class="form bottom">
		<table width="100%"><tr><td style="vertical-align: top;" valign="top">
		<ol>
			<li><label>Payment #:</label><s:property value="payment.id" /></li>
			<li><label>Date:</label><s:date name="payment.creationDate" format="M/d/yy" /></li>
			<li><label>Amount:</label>$<s:property value="payment.totalAmount" /><s:hidden id="payment_totalAmount" value="%{payment.totalAmount}"/></li>
			<s:if test="payment.status.unpaid">
				<li><label>Applied:</label>$<span id="payment_amountApplied"><s:property value="payment.amountApplied"/></span></li>
				<li><label>Credit:</label>$<span id="payment_balance"><s:property value="payment.balance"/></span>
			</s:if>
		</ol>
		</td><td style="vertical-align: top;" valign="top">
		<ol>
			<li><label>Method:</label><s:property value="payment.paymentMethod.description" /></li>
			<s:if test="payment.paymentMethod.check">
				<li><label>Check Number:</label><s:textfield name="payment.checkNumber" /></li>
			</s:if>
			<s:if test="payment.paymentMethod.creditCard">
				<li><label>Transaction ID:</label><s:property value="payment.transactionID" /></li>
				<li><label>Type:</label><s:property value="payment.ccType" /></li>
				<li><label>Number:</label><s:property value="payment.ccNumber" /></li>
				<li><label>Status:</label><s:property value="transactionCondition" /></li>
				<s:if test="transactionCondition == 'pendingsettlement'">
					<li><label>Void CC:</label><a class="remove" href="PaymentDetail.action?payment.id=<s:property value="payment.id" />&button=voidcc">Void</a></li>
				</s:if>
			</s:if>
		</ol>
		</td></tr></table>
		</fieldset>
	</s:if>
	<s:else>
		<fieldset class="form">
		<table><tr><td style="vertical-align: top;" valign="top">
		<ol>
			<li><label>Payment #:</label>NEW</li>
			<li><label>Date:</label><s:date name="new java.util.Date()" format="M/d/yy" /></li>
			<li><label>Payment Amount:</label>$<s:textfield id="payment_totalAmount" name="payment.totalAmount" value="0.00" size="7" onchange="changeTotal()"/></li>
			<li><label>Applied:</label>$<span id="payment_amountApplied">0.00</span></li>
			<li><label>Credit:</label>$<span id="payment_balance">0.00</span>
				<input type="button" value="Clear" onclick="calculateTotalFromApplied()"></li>
		</ol>
		</td><td style="vertical-align: top;" valign="top">
		<ol id="paymentMethodList">
			<li><label>Method:</label><s:radio name="method" theme="pics" onclick="changePaymentMethod(this.value)"
				list="#{'CreditCard':'Credit Card','Check':'Check'}"></s:radio></li>
			<li class="method_check" <s:if test="!method.check">style="display: none"</s:if>><label>Check Number:</label><s:textfield
				name="payment.checkNumber" /></li>
			<li><div id="braintree"></div></li>
			<li class="method_ccNew" style="display: none"><label>Number:</label><s:textfield name="creditCard.cardNumber" /></li>
			<li class="method_ccNew" style="display: none"><label>Expires:</label><s:textfield name="creditCard.expirationDate" /></li>
		</ol>
		</td></tr></table>
		</fieldset>
	</s:else>
	
	<s:if test="(payment.invoices.size + payment.refunds.size) > 0">
		<table class="report" style="clear:none">
			<thead>
				<tr>
					<th>Type</th>
					<th>#</th>
					<th>Date</th>
					<th>Total</th>
					<th>Applied</th>
					<th>&nbsp;</th>
				</tr>
			</thead>

			<s:iterator value="payment.invoices">
				<tr>
					<td>Invoice</td>
					<td><a href="InvoiceDetail.action?invoice.id=<s:property value="invoice.id" />"><s:property value="invoice.id" /></a></td>
					<td><s:date name="invoice.creationDate" format="M/d/yy" /></td>
					<td>$<s:property value="invoice.totalAmount" /></td>
					<td>$<s:property value="amount" /></td>
					<td><a class="remove" href="PaymentDetail.action?payment.id=<s:property value="payment.id" />&button=unapply&amountApplyMap[<s:property value="invoice.id" />]=0">Remove</a></td>
				</tr>
			</s:iterator>
			<s:iterator value="payment.refunds">
				<tr>
					<td>Refund</td>
					<td><a href=""><s:property value="refund.id" /></a></td>
					<td><s:date name="refund.creationDate" format="M/d/yy" /></td>
					<td>$<s:property value="refund.totalAmount" /></td>
					<td>$<s:property value="amount" /></td>
					<td><a class="remove" href="PaymentDetail.action?payment.id=<s:property value="payment.id" />&button=unapply&amountApplyMap[<s:property value="refund.id" />]=0">Remove</a></td>
				</tr>
			</s:iterator>
		</table>
	</s:if>
	<s:if test="hasUnpaidInvoices">
		<h3>Unapplied Invoices</h3>
		<table class="report" style="clear:none">
			<thead>
				<tr>
					<th>Invoice</th>
					<th>Date</th>
					<th>Total</th>
					<th>Balance</th>
					<th>Apply</th>
					<th>Amt to Apply</th>
				</tr>
			</thead>
			<s:iterator value="contractor.invoices">
				<s:if test="totalAmount > 0 && status.unpaid">
					<tr>
						<td class="center"><a href="InvoiceDetail.action?invoice.id=<s:property value="id"/>"><span class="invoiceID"><s:property value="id" /></span></a></td>
						<td class="center"><s:date name="creationDate" format="M/d/yy" /></td>
						<td class="right">$<s:property value="totalAmount" /></td>
						<td class="right">$<span id="invoice_balance_<s:property value="id"/>"><s:property value="balance" /></span></td>
						<td><input type="button" onclick="setInvoiceApply(<s:property value="id" />);" value="&gt;&gt;"/></td>
						<td class="right">$<s:textfield id="invoice_apply_%{id}" cssClass="amountApply" name="amountApplyMap[%{id}]" size="6"
							onchange="updateSingleAppliedAmount(%{id});" /></td>
					</tr>
				</s:if>
			</s:iterator>
			<tr>
				<td class="right" colspan="6">
					<s:if test="payment == null">
						<input type="button" value="Apply All" onclick="applyAll();return false;"/>
					</s:if>
					<input type="button" value="Clear" onclick="clearAll();return false;"/>
				</td>
			</tr>
		</table>
		<s:if test="payment == null">
			<div><input id="autoapply" type="checkbox" checked="checked" onchange="autoApply();"> <label for="autoapply">Auto Apply</label></div>
		</s:if>
	</s:if>
	
	<div id="buttons" class="buttons">
		<s:if test="payment == null">
			<input type="submit" id="save_button" class="picsbutton positive" value="Collect Payment" name="button"/>
		</s:if>
		<s:else>
			<s:if test="payment.balance > 0">
				<input type="submit" id="save_button" class="picsbutton positive" value="Save" name="button"/>
			</s:if>
			<s:if test="payment.qbListID == null">
				<a href="PaymentDetail.action?payment.id=<s:property value="payment.id"/>&button=Delete" class="picsbutton negative">Delete</a>
			</s:if>
		</s:else>
	</div>
</s:form>

<s:if test="payment != null && payment.id > 0 && payment.balance > 0">
	<s:form>
	<s:hidden name="payment.id" />
	<h3 style="margin-top: 50px">Refund</h3>
	<fieldset class="form bottom">
	<ol>
		<li><label>Refund Method:</label> <s:property value="payment.paymentMethod.description"/></li>
		<li><label>Amount:</label> <s:textfield name="refundAmount" value="%{payment.balance}" /></li>
	</ol>
	<div>
		<input type="submit" class="picsbutton" value="Refund" name="button" />
		<input type="submit" class="picsbutton" value="Refund Without Charge" name="button" />
	</div>
	</fieldset>
	</s:form>
</s:if>

<s:if test="payment == null || payment.id == 0">
	<script>
	<s:if test="contractor.paymentMethod.creditCard && contractor.balance > 0">
		applyAll();
	</s:if>	
	calculateApplied();
	calculateTotalFromApplied();
	<s:if test="method.creditCard">
		findCreditCard(<s:property value="id"/>);
	</s:if>
	</script>
</s:if>
<br clear="all"/>
</body>
</html>
