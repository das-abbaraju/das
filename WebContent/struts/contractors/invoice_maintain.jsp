<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Edit Invoice <s:property value="invoice.id" /> for <s:property value="invoice.account.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
</head>
<body>
<s:include value="../contractors/conHeader.jsp" />

<div class="buttons"><a href="InvoiceDetail.action?invoice.id=<s:property value="invoiceId"/>" class="picsbutton">&lt;&lt; Return to Invoice</a></div>

<s:form action="ConInvoiceMaintain" cssStyle="width: 500px">
	<s:hidden name="invoiceId" />
	<s:hidden name="id"/>
<fieldset class="form">
	<h2 class="formLegend">Invoice Edit</h2>
	<ol>
		<li><label>Amount:</label>
			<s:textfield name="invoice.totalAmount" value="%{invoice.totalAmount}"/></li>
		<li><label>Notes:</label>
			<s:textarea name="invoice.notes" cols="30" rows="3"/></li>
		<li><label>Creation Date:</label>
			<s:textfield name="invoice.creationDate" /></li>
		<li><label>Due Date:</label>
			<s:textfield name="invoice.dueDate" /></li>
		<li><label>Status:</label>
			<s:radio list="@com.picsauditing.jpa.entities.TransactionStatus@values()" name="invoice.status" theme="pics"></s:radio>
	</ol>
</fieldset>
<fieldset class="form">
	<h2 class="formLegend">Quick Books</h2>
	<ol>
		<li><label>PO Number:</label>
			<s:textfield name="invoice.poNumber"/></li>
		<li><label>Quick Books Sync:</label>
			<s:checkbox name="invoice.qbSync"/></li>
		<li><label>QB ListID:</label>
			<s:property value="invoice.qbListID"/></li>
	</ol>
</fieldset>
<s:iterator value="invoice.items" status="item">
<fieldset class="form">
	<legend><span>Item For <s:property value="invoiceFee.fee"/></span></legend>
	<ol>
		<li><label>Fee:</label>
		<s:select list="feeList" name="feeMap[%{id}]" headerKey="0"
							headerValue="- Select a New Fee to Add -" listKey="id" listValue="fee" value="%{invoiceFee.id}" /></li>
		<li><label>Amount:</label>
			<s:textfield name="invoice.items[%{#item.index}].amount" value="%{amount}"/></li>
		<li><label>Payment Expires:</label>
			<s:textfield name="invoice.items[%{#item.index}].paymentExpires"/></li>
		<li><label>Description:</label>
			<s:textfield name="invoice.items[%{#item.index}].description" value="%{description}"/></li>
		<li><label></label><a href="ConInvoiceMaintain.action?id=<s:property value="contractor.id"/>&invoiceId=<s:property value="invoice.id"/>&itemID=<s:property value="id"/>&button=Remove" class="remove">Remove Line Item</a></li>
	</ol>
</fieldset>
</s:iterator>
<fieldset class="form submit">
	<div>
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
