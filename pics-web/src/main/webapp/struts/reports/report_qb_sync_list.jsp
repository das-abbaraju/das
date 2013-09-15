<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>QuickBooks Sync ${currency}</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css?v=<s:property value="version"/>" />
</head>
<body>
<h1>QuickBooks Sync ${currency}</h1>
<s:if test="currency.display == 'USD'">
    <a href="JavaScript:;" class="picsbutton positive disabled">Switch to United States</a>
</s:if>
<s:else>
    <a href="QBSyncList.action?currency=USD" class="picsbutton positive">Switch to United States</a>
</s:else>

<s:if test="currency.display == 'CAD'">
    <a href="JavaScript:;" class="picsbutton positive disabled">Switch to Canada</a>
</s:if>
<s:else>
    <a href="QBSyncList.action?currency=CAD" class="picsbutton positive">Switch to Canada</a>
</s:else>

<s:if test="currency.display == 'GBP'">
    <a href="JavaScript:;" class="picsbutton positive disabled">Switch to UK</a>
</s:if>
<s:else>
    <a href="QBSyncList.action?currency=GBP" class="picsbutton positive">Switch to UK</a>
</s:else>

<s:if test="currency.display == 'EUR'">
    <a href="JavaScript:;" class="picsbutton positive disabled">Switch to Euro</a>
</s:if>
<s:else>
    <a href="QBSyncList.action?currency=EUR" class="picsbutton positive">Switch to Euro</a>
</s:else>

<div>
Last Error: <s:date name="lastError.creationDate" nice="true" /><br />

<pre style="width: 100%; overflow: scroll; background-color: infobackground;">
<s:property value="lastError.body" />
</pre>

</div>

<s:if test="contractorInsert.size > 0" >
	<h3>Contractors to Insert</h3>
	<table class="report">
		<thead>
			<tr>
				<th></th>
				<th>Con ID</th>
				<th>Contractor Name</th>
				<th>Created</th>
			</tr>
		</thead>
		<s:iterator value="contractorInsert">
			<tr>
				<td><a href="QBSyncList.action?currency=${currency}&type=C&id=${id}" class="remove">Skip</a></td>
				<td><s:property value="id" /></td>
				<td><a href="ContractorEdit.action?id=<s:property value="id" />"><s:property
					value="name" /></a></td>
				<td><s:date name="creationDate" nice="true" /></td>
			</tr>
		</s:iterator>
	</table>
</s:if>
<s:else>
	<h5>No Contractors to Insert!</h5>
</s:else>

<s:if test="contractorUpdate.size > 0" >
	<h3>Contractors to Update</h3>
	<table class="report">
		<thead>
			<tr>
				<th></th>
				<th>Con ID</th>
				<th>Contractor Name</th>
				<th>Updated</th>
				<th>qbID</th>
			</tr>
		</thead>
		<s:iterator value="contractorUpdate">
			<tr>
				<td><a href="QBSyncList.action?currency=${currency}&type=C&id=${id}" class="remove">Skip</a></td>
				<td><s:property value="id" /></td>
				<td><a href="ContractorEdit.action?id=<s:property value="id" />"><s:property
					value="name" /></a></td>
				<td><s:date name="updateDate" nice="true" /></td>
				<td><s:property value="qbListID" /></td>
			</tr>
		</s:iterator>
	</table>
</s:if>
<s:else>
	<h5>No Contractors to Update!</h5>
</s:else>

<s:if test="invoiceInsert.size > 0" >
	<h3>Invoices to Insert</h3>
	<table class="report">
		<thead>
			<tr>
				<th></th>
				<th>Con ID</th>
				<th>Contractor Name</th>
				<th>Invoice</th>
				<th>Amount</th>
				<th>Created</th>
			</tr>
		</thead>
		<s:iterator value="invoiceInsert">
			<tr>
				<td><a href="QBSyncList.action?currency=${currency}&type=I&id=${id}" class="remove">Skip</a></td>
				<td><s:property value="account.id"/></td>
				<td><a href="ContractorEdit.action?id=<s:property value="account.id" />"><s:property value="account.name" /></a></td>
				<td><a href="InvoiceDetail.action?invoice.id=<s:property value="id" />"><s:property
					value="id" /></a></td>
				<td class="right">$<s:property value="totalAmount" /></td>
				<td><s:date name="creationDate" nice="true" /></td>
			</tr>
		</s:iterator>
	</table>
</s:if>
<s:else>
	<h5>No Invoices to Insert!</h5>
</s:else>

<s:if test="invoiceUpdate.size > 0" >
	<h3>Invoices to Update</h3>
	<table class="report">
		<thead>
			<tr>
				<th></th>
				<th>Con ID</th>
				<th>Contractor Name</th>
				<th>Invoice</th>
				<th>Amount</th>
				<th>Updated</th>
				<th>qbID</th>
			</tr>
		</thead>
		<s:iterator value="invoiceUpdate">
			<tr>
				<td><a href="QBSyncList.action?currency=${currency}&type=I&id=${id}" class="remove">Skip</a></td>
				<td><s:property value="account.id"/></td>
				<td><a href="ContractorEdit.action?id=<s:property value="account.id" />"><s:property value="account.name" /></a></td>
				<td><a href="InvoiceDetail.action?invoice.id=<s:property value="id" />"><s:property
					value="id" /></a></td>
				<td class="right">$<s:property value="totalAmount" /></td>
				<td><s:date name="updateDate" nice="true" /></td>
				<td><s:property value="qbListID" /></td>
			</tr>
		</s:iterator>
	</table>
</s:if>
<s:else>
	<h5>No Invoices to Update!</h5>
</s:else>

<s:if test="paymentInsert.size > 0" >
	<h3>Payments to Insert</h3>
	<table class="report">
		<thead>
			<tr>
				<th></th>
				<th>Con ID</th>
				<th>Contractor Name</th>
				<th>Payment</th>
				<th>Amount</th>
				<th>Created</th>
			</tr>
		</thead>
		<s:iterator value="paymentInsert">
			<tr>
				<td><a href="QBSyncList.action?currency=${currency}&type=P&id=${id}" class="remove">Skip</a></td>
				<td><s:property value="account.id"/></td>
				<td><a href="ContractorEdit.action?id=<s:property value="account.id" />"><s:property value="account.name" /></a></td>
				<td><a href="PaymentDetail.action?payment.id=<s:property value="id" />"><s:property
					value="id" /></a></td>
				<td class="right">$<s:property value="totalAmount" /></td>
				<td><s:date name="creationDate" nice="true" /></td>
			</tr>
		</s:iterator>
	</table>
</s:if>
<s:else>
	<h5>No Payments to Insert!</h5>
</s:else>

<s:if test="paymentUpdate.size > 0" >
	<h3>Payments to Update</h3>
	<table class="report">
		<thead>
			<tr>
				<th></th>
				<th>Con ID</th>
				<th>Contractor Name</th>
				<th>Payment</th>
				<th>Amount</th>
				<th>Updated</th>
				<th>qbID</th>
			</tr>
		</thead>
		<s:iterator value="paymentUpdate">
			<tr>
				<td><a href="QBSyncList.action?currency=${currency}&type=P&id=${id}" class="remove">Skip</a></td>
				<td><s:property value="account.id"/></td>
				<td><a href="ContractorEdit.action?id=<s:property value="account.id" />"><s:property value="account.name" /></a></td>
				<td><a href="PaymentDetail.action?payment.id=<s:property value="id" />"><s:property
					value="id" /></a></td>
				<td class="right">$<s:property value="totalAmount" /></td>
				<td>?? <s:date name="updateDate" nice="true" /></td>
				<td><s:property value="qbListID" /></td>
			</tr>
		</s:iterator>
	</table>
</s:if>
<s:else>
	<h5>No Payments to Update!</h5>
</s:else>

</body>
</html>