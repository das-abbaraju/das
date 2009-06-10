<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="invoice.account.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
</head>
<body>
<s:include value="../contractors/conHeader.jsp" />

<div><a href="InvoiceDetail.action?invoice.id=<s:property value="invoiceId"/>">&lt;&lt; Return to Invoice</a></div>

<s:form action="ConInvoiceMaintain" cssStyle="width: 400px">
	<s:hidden name="invoiceId" />
	<s:hidden name="id"/>
<fieldset class="form">
	<legend><span>Invoice Edit</span></legend>
	<ol>
		<li><label>Amount:</label>
			<s:textfield name="invoice.totalAmount" value="%{invoice.totalAmount}"/></li>
		<li><label>Payment Method</label>
			<s:radio list="#{'Check':'Check','CreditCard':'Credit Card'}" name="contractor.paymentMethod" theme="pics"/></li>
		<li><label>Notes:</label>
			<s:textfield name="invoice.notes"/></li>
		<li><label>Due Date:</label>
			<s:textfield name="invoice.dueDate" value="%{invoice.dueDate && getText('dates', {invoice.dueDate})}"/></li>
		<li><label>Paid:</label>
			<s:checkbox name="invoice.paid" value="%{invoice.paid}"/></li>
		<li><label>Paid Date:</label>
			<s:textfield name="invoice.paidDate" value="%{invoice.paidDate && getText('dates', {invoice.paidDate})}"/></li>
	</ol>
</fieldset>
<fieldset class="form">
	<legend><span>Quick Books</span></legend>
	<ol>
		<li><label>Check Number:</label>
			<s:textfield name="invoice.checkNumber"/></li>
		<li><label>TransactionID:</label>
			<s:textfield name="invoice.transactionID"/></li>
		<li><label>PO Number:</label>
			<s:textfield name="invoice.poNumber"/></li>
		<li><label>CC Number:</label>
			<s:textfield name="invoice.ccNumber"/></li>
		<li><label>Quick Books Sync:</label>
			<s:checkbox name="invoice.qbSync"/></li>
		<li><label>QB ListID:</label>
			<s:textfield name="invoice.qbListID"/></li>
	</ol>
</fieldset>
<s:iterator value="invList" status="item">
<fieldset class="form">
	<legend><span>Item For <s:property value="invoiceFee.fee"/></span></legend>
	<ol>
		<li><label>Invoice Fee</label>
		<s:select list="feeList" name="invList[%{#item.index}].invoiceFee.id" headerKey="0"
							headerValue="- Select a New Fee to Add -" listKey="id" listValue="fee" value="%{invoiceFee.id}" /></li>
		<li><label>Amount:</label>
			<s:textfield name="invList[%{#item.index}].amount" value="%{amount}"/></li>
		<li><label>Payment Expires:</label>
			<s:textfield name="invList[%{#item.index}].paymentExpires" value="%{paymentExpires && getText('dates', {paymentExpires})}"/></li>
		<li><label>Description:</label>
			<s:textfield name="invList[%{#item.index}].description" value="%{description}"/></li>
	</ol>
</fieldset>
</s:iterator>
<fieldset class="form submit">
	<div class="buttons">
		<pics:permission perm="InvoiceEdit" type="Edit">
			<input type="submit" class="picsbutton positive" name="button" value="Save"/>
		</pics:permission>
		<pics:permission perm="InvoiceEdit" type="Delete">
			<input type="submit" class="picsbutton negative" name="button" value="Delete" onclick="return confirm('Are you sure you want to permanently remove this invoice?');"/>
		</pics:permission>
	</div>
</fieldset>
</s:form>

</body>
</html>
