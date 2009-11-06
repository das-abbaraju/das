<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>QuickBooks Sync</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css?v=20091105" />
</head>
<body>
<h1>QuickBooks Sync</h1>

<div>
Last Error: <s:date name="lastError.creationDate" nice="true" /><br />

<pre style="width: 100%; overflow: scroll; background-color: infobackground;">
<s:property value="lastError.body" />
</pre>

</div>

<h3>Contractors to Insert</h3>
<table class="report">
	<thead>
		<tr>
			<th></th>
			<th>ID</th>
			<th>Contractor Name</th>
			<th>Created</th>
		</tr>
	</thead>
	<s:iterator value="contractorInsert">
		<tr>
			<td><a href="QBSyncList.action?type=C&id=<s:property value="id" />" class="remove">Skip</a></td>
			<td><s:property value="id" /></td>
			<td><a href="ContractorEdit.action?id=<s:property value="id" />"><s:property
				value="name" /></a></td>
			<td><s:date name="creationDate" nice="true" /></td>
		</tr>
	</s:iterator>
</table>

<h3>Contractors to Update</h3>
<table class="report">
	<thead>
		<tr>
			<th></th>
			<th>ID</th>
			<th>Contractor Name</th>
			<th>Updated</th>
			<th>qbID</th>
		</tr>
	</thead>
	<s:iterator value="contractorUpdate">
		<tr>
			<td><a href="QBSyncList.action?type=C&id=<s:property value="id" />" class="remove">Skip</a></td>
			<td><s:property value="id" /></td>
			<td><a href="ContractorEdit.action?id=<s:property value="id" />"><s:property
				value="name" /></a></td>
			<td><s:date name="updateDate" nice="true" /></td>
			<td><s:property value="qbListID" /></td>
		</tr>
	</s:iterator>
</table>

<h3>Invoices to Insert</h3>
<table class="report">
	<thead>
		<tr>
			<th></th>
			<th>Contractor Name</th>
			<th>Invoice</th>
			<th>Amount</th>
			<th>Created</th>
		</tr>
	</thead>
	<s:iterator value="invoiceInsert">
		<tr>
			<td><a href="QBSyncList.action?type=I&id=<s:property value="id" />" class="remove">Skip</a></td>
			<td><s:property value="account.name" /></td>
			<td><a href="InvoiceDetail.action?invoice.id=<s:property value="id" />"><s:property
				value="id" /></a></td>
			<td class="right">$<s:property value="totalAmount" /></td>
			<td><s:date name="creationDate" nice="true" /></td>
		</tr>
	</s:iterator>
</table>

<h3>Invoices to Update</h3>
<table class="report">
	<thead>
		<tr>
			<th></th>
			<th>Contractor Name</th>
			<th>Invoice</th>
			<th>Amount</th>
			<th>Updated</th>
			<th>qbID</th>
		</tr>
	</thead>
	<s:iterator value="invoiceUpdate">
		<tr>
			<td><a href="QBSyncList.action?type=I&id=<s:property value="id" />" class="remove">Skip</a></td>
			<td><s:property value="account.name" /></td>
			<td><a href="InvoiceDetail.action?invoice.id=<s:property value="id" />"><s:property
				value="id" /></a></td>
			<td class="right">$<s:property value="totalAmount" /></td>
			<td><s:date name="updateDate" nice="true" /></td>
			<td><s:property value="qbListID" /></td>
		</tr>
	</s:iterator>
</table>

<h3>Payments to Insert</h3>
<table class="report">
	<thead>
		<tr>
			<th></th>
			<th>Contractor Name</th>
			<th>Payment</th>
			<th>Amount</th>
			<th>Created</th>
		</tr>
	</thead>
	<s:iterator value="paymentInsert">
		<tr>
			<td><a href="QBSyncList.action?type=P&id=<s:property value="id" />" class="remove">Skip</a></td>
			<td><s:property value="account.name" /></td>
			<td><a href="PaymentDetail.action?payment.id=<s:property value="id" />"><s:property
				value="id" /></a></td>
			<td class="right">$<s:property value="totalAmount" /></td>
			<td><s:date name="creationDate" nice="true" /></td>
		</tr>
	</s:iterator>
</table>

<h3>Payments to Update</h3>
<table class="report">
	<thead>
		<tr>
			<th></th>
			<th>Contractor Name</th>
			<th>Payment</th>
			<th>Amount</th>
			<th>Updated</th>
			<th>qbID</th>
		</tr>
	</thead>
	<s:iterator value="paymentUpdate">
		<tr>
			<td><a href="QBSyncList.action?type=P&id=<s:property value="id" />" class="remove">Skip</a></td>
			<td><s:property value="account.name" /></td>
			<td><a href="PaymentDetail.action?payment.id=<s:property value="id" />"><s:property
				value="id" /></a></td>
			<td class="right">$<s:property value="totalAmount" /></td>
			<td>?? <s:date name="updateDate" nice="true" /></td>
			<td><s:property value="qbListID" /></td>
		</tr>
	</s:iterator>
</table>

</body>
</html>
