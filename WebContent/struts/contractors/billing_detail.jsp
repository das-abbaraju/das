<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:property value="contractor.name" /> Billing Detail</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />

<script src="js/prototype.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css" />
<script src="js/notes.js" type="text/javascript"></script>

</head>
<body>
<s:include value="conHeader.jsp"></s:include>

<table width="100%">
	<tr>
		<td style="vertical-align: top; width: 48%;">
		<fieldset class="form"><legend><span>Info</span></legend>
		<ol>
			<li><label>Active:</label> <s:if test="contractor.active == 'Y'">
							Yes
						</s:if> <s:else>
							No
						</s:else></li>
			<li><label title="The Date the Account was Created.">Registration Date:</label> <s:date
				name="contractor.creationDate" format="MMM d, yyyy" /></li>
			<li><label title="The Date the Activation/Reactivation Fee was Paid.">Activation Date:</label> <s:date
				name="contractor.membershipDate" format="MMM d, yyyy" /></li>
			<li><label>Will be Renewed:</label>
				<s:if test="contractor.renew">Yes</s:if>
				<s:else>No</s:else>
			</li>
			<li><label>Renewal Date:</label> <s:date name="contractor.paymentExpires" format="MMM d, yyyy" /></li>
			<li><label>Payment Method:</label> <s:property value="contractor.paymentMethod.description" /></li>
			<li><label>Credit Card on File?</label> <s:if test="contractor.ccOnFile">Yes</s:if> <s:else>No</s:else></li>
		</ol>
		</fieldset>

		<fieldset class="form"><legend><span>Facilities</span></legend>
		<ol>
			<li><label>Requested By:</label> <s:property value="requestedBy.name" /></li>
			<li><label>Risk Level:</label> <s:property value="contractor.riskLevel" /></li>
			<li><label>Facilities:</label> <s:property value="contractor.operators.size()" /> operator(s)<br />
			<br />
			<ul style="float: right; list-style-type: disc;">
				<s:iterator value="contractor.operators">
					<s:if test="permissions.admin">
						<li><a href="AuditOperator.action?oID=<s:property value="operatorAccount.id" />"><s:property
							value="operatorAccount.name" /></a></li>
					</s:if>
					<s:else>
						<li><s:property value="operatorAccount.name" /></li>
					</s:else>
				</s:iterator>
			</ul>
			</li>
			<li><label>View Operators:</label> <a href="ContractorFacilities.action?id=<s:property value="id" />">Facilities</a>
			</li>
			<li><label>Last Upgrade Date:</label> <s:date name="contractor.lastUpgradeDate" format="MMM d, yyyy" /></li>
		</ol>
		</fieldset>
		</td>
		<td style="width: 5px;"></td>
		<td style="vertical-align: top; width: 48%;">
		<fieldset class="form"><legend><span>Invoicing</span></legend>
		<ol>
			<li><label>Current Balance:</label> $<s:property value="contractor.balance" /> USD</li>
			<li><label>Billing Status:</label> <s:property value="contractor.billingStatus" /></li>
			<li><label>Must Pay:</label> <s:property value="contractor.mustPay" /></li>
			<li><label>New Level:</label> $<s:property value="contractor.newMembershipLevel.amount" /> USD <br>
			<s:property value="contractor.newMembershipLevel.fee" /></li>
			<li><label>Current Level:</label> $<s:property value="contractor.membershipLevel.amount" /> USD <br>
			<s:property value="contractor.membershipLevel.fee" /></li>

		</ol>
		</fieldset>

		<s:if test="permissions.admin">
			<s:form id="save" method="POST" enctype="multipart/form-data">
				<s:hidden name="id" />
				<fieldset class="form"><legend><span>Create Invoice</span></legend>
				<ol>
					<s:iterator value="invoiceItems">
						<s:if test="invoiceFee != null">
							<li><label><s:property value="invoiceFee.fee" />:</label> $<s:property value="amount" /> USD</li>
						</s:if>
						<s:else>
							<li><label><s:property value="description" />:</label> $<s:property value="amount" /> USD</li>
						</s:else>
					</s:iterator>
					<li><label>Total:</label> $<s:property value="invoiceTotal" /> USD</li>
				</ol>
				<div class="buttons"><input type="submit" class="picsbutton positive" name="button" value="Create" /></div>
				</fieldset>
			</s:form>
		</s:if> <s:if test="permissions.admin">
			<div class="clear"></div>
			<h3 style="margin-top: 50px">Past Invoices</h3>
			<table class="report">
				<thead>
					<tr>
						<th>Invoice #</a></th>
						<th>Date Created</th>
						<th>Amount</th>
						<th>Due Date</th>
						<th>Date Paid</th>
						<th>Paid</th>
					</tr>
				</thead>
				<tbody>
					<s:iterator value="contractor.sortedInvoices">
						<tr style="cursor: pointer;"
							onclick="window.location = 'InvoiceDetail.action?invoice.id=<s:property value="id"/>'">
							<td class="center"><a href="InvoiceDetail.action?invoice.id=<s:property value="id"/>"><s:property
								value="id" /></a></td>
							<td class="right"><s:date name="creationDate" format="M/d/yy" /></td>
							<td class="right">$<s:property value="totalAmount" /></td>
							<td class="right"><s:date name="dueDate" format="M/d/yy" /></td>
							<td class="right"><s:date name="paidDate" format="M/d/yy" /></td>
							<td class="right"><s:if test="paid">Yes</s:if><s:else>No</s:else></td>
						</tr>
					</s:iterator>
				</tbody>
			</table>
		</s:if></td>
	</tr>
</table>

<br />
<br />
<div id="notesList"><s:include value="con_notes_embed.jsp"></s:include></div>

</body>
</html>
