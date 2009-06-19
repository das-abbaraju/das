<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="contractor.name" /> - Payment <s:property value="invoice.id" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="all" href="css/invoice.css" />
<link rel="stylesheet" type="text/css" media="all" href="css/audit.css" />
<link rel="stylesheet" type="text/css" media="all" href="css/reports.css" />

<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/payment_detail.js"></script>

</head>
<body>
<s:include value="conHeader.jsp"></s:include>

<s:if test="payment == null && method.creditCard">
	<s:iterator value="contractor.payments">
		<s:if test="status.unpaid">
			<div class="alert">This contractor still has an <a
				href="PaymentDetail.action?payment.id=<s:property value="id"/>">open credit</a>. Please apply that credit before
			collecting any new payments.</div>
		</s:if>
	</s:iterator>
</s:if>

<s:form>
	<s:hidden name="id" />
	<div class="auditHeader"><s:if test="payment != null && payment.id > 0">
		<s:hidden name="payment.id" />
		<fieldset>
		<ul>
			<li><label>Payment:</label>#<s:property value="payment.id" /></li>
			<li><label>Date:</label><s:date name="payment.creationDate" format="M/d/yy" /></li>
			<li><label>Total Amount:</label>$<s:property value="payment.totalAmount" /></li>
			<li><label>Applied:</label>$<span id="payment_amountApplied"><s:property value="payment.amountApplied" /></span></li>
			<li><label>Remainder:</label>$<span id="payment_balance"><s:property value="payment.balance" /></span></li>
		</ul>
		</fieldset>
		<fieldset>
		<ul>
			<li><label>Method:</label><s:property value="payment.paymentMethod" /></li>
			<s:if test="payment.paymentMethod.check">
				<li><label>Check Number:</label><s:textfield name="payment.checkNumber" /></li>
			</s:if>
			<s:if test="payment.paymentMethod.creditCard">
				<li><label>Transaction ID:</label><s:property value="payment.transactionID" /></li>
				<li><label>Type:</label><s:property value="payment.ccType" /></li>
				<li><label>Number:</label><s:property value="payment.ccNumber" /></li>
			</s:if>
		</ul>
		</fieldset>
	</s:if><s:else>
		<fieldset>
		<ul>
			<li><label>Payment:</label>NEW</li>
			<li><label>Date:</label><s:date name="new java.util.Date()" format="M/d/yy" /></li>
			<li><label>Total Amount:</label>$<s:textfield id="payment_totalAmount" name="payment.totalAmount" value="0.00" size="7" onchange="changeTotal()"/></li>
			<li><label>Applied:</label>$<span id="payment_amountApplied">0.00</span></li>
			<li><label>Remainder:</label>$<span id="payment_balance">0.00</span>
				<input type="button" value="Update Payment" onclick="calculateTotalFromApplied()">
			</li>
			<li><label>Auto Apply:</label><input id="autoapply" type="checkbox" checked="checked" onchange="autoApply();"></li>
		</ul>
		</fieldset>
		<fieldset>
		<ul>
			<li><label>Method:</label><s:radio name="method" theme="pics"
				list="@com.picsauditing.jpa.entities.PaymentMethod@values()"></s:radio></li>
			<li class="method_check" style="display: none"><label>Check Number:</label><s:textfield
				name="payment.checkNumber" /></li>
			<li class="method_cc" style="display: none"><label>Type:</label><s:property value="creditcard.ccType" /></li>
			<li class="method_cc" style="display: none"><label>Number:</label><s:property value="creditcard.ccNumber" /></li>
			<li class="method_ccNew" style="display: none"><label>Number:</label><s:textfield name="creditcard.ccType" /></li>
			<li class="method_ccNew" style="display: none"><label>Expires:</label><s:textfield name="creditcard.ccNumber" /></li>
		</ul>
		</fieldset>
	</s:else>
	<div class="clear"></div>
	</div>
	<s:if test="payment.invoices.size > 0">
		<h3>Currently Applied Invoices</h3>
		<table class="report">
			<thead>
				<tr>
					<th>UnApply?</th>
					<th>Invoice</th>
					<th>Date</th>
					<th>Invoice Amount</th>
					<th>Balance</th>
					<th>Payment Amount</th>
					<th>Status</th>
				</tr>
			</thead>

			<s:iterator value="payment.invoices" id="i">
				<tr>
					<td><s:checkbox name="unApplyMap[%{id}]" /></td>
					<td>Invoice #<s:property value="invoice.id" /></td>
					<td><s:date name="invoice.creationDate" format="M/d/yy" /></td>
					<td>$<s:property value="invoice.totalAmount" /></td>
					<td>$<s:property value="invoice.balance" /></td>
					<td>$<s:property value="amount" /></td>
					<td><s:property value="invoice.status" /></td>
				</tr>
			</s:iterator>
		</table>
	</s:if>
	<s:if test="hasUnpaidInvoices">
		<h3>Unapplied Invoices</h3>
		<table class="report">
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
				<s:if test="status.unpaid">
					<tr>
						<td class="center"><a href="InvoiceDetail.action?invoice.id=<s:property value="id"/>" target="_BLANK" title="Opens in new window"><span class="invoiceID"><s:property
							value="id" /></span></a></td>
						<td class="center"><s:date name="creationDate" format="M/d/yy" /></td>
						<td class="right">$<s:property value="totalAmount" /></td>
						<td class="right">$<span id="invoice_balance_<s:property value="id"/>"><s:property value="balance" /></span></td>
						<td><input type="button" onclick="setInvoiceApply(<s:property value="id" />);" value="&gt;&gt;"/></td>
						<td class="right">$<s:textfield id="invoice_apply_%{id}" cssClass="amountApply" name="amountApplyMap[%{id}]" size="6" value="0.00" onchange="updateSingleAppliedAmount(%{id});" /></td>
					</tr>
				</s:if>
			</s:iterator>
		</table>
	</s:if>
	<div id="button_div" class="buttons">
		<s:if test="payment == null">
			<input type="submit" class="picsbutton positive" value="Collect Payment" name="button" />
		</s:if>
		<s:else>
			<input type="submit" class="picsbutton positive" value="Save" name="button" />
			<s:if test="payment.qbListID == null">
				<input type="submit" class="picsbutton negative" value="Delete" name="button" />
			</s:if>
		</s:else>
	</div>
</s:form>

</body>
</html>
