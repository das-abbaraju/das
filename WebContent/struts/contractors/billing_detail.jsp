<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="contractor.name" /> Billing Detail</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=20091105" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=20091105" />

<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
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
			<li><label>Will be Renewed:</label> <s:if test="contractor.renew">Yes</s:if> <s:else>No</s:else></li>
			<li><label>Renewal Date:</label> <s:date name="contractor.paymentExpires" format="MMM d, yyyy" /></li>
			<li><label>Payment Method:</label> <s:property value="contractor.paymentMethod.description" /></li>
			<li><label>Credit Card on File?</label> <s:if test="contractor.ccOnFile">Yes</s:if> <s:else>No</s:else></li>
		</ol>
		</fieldset>

		<fieldset class="form"><legend><span>Facilities</span></legend>
		<ol>
			<li><label>Requested By:</label> <s:property value="requestedBy.name" /></li>
			<li><label>Risk Level:</label> <s:property value="contractor.riskLevel" /></li>
			<li><label>Facilities:</label> <s:property value="contractor.payingFacilities" /> paying operator(s)<br />
			<br />
			<ul style="position: relative; left: 11em; list-style-type: disc;">
				<s:iterator value="contractor.operators">
					<s:if test="operatorAccount.activeB">
						<li><s:if test="permissions.admin">
							<a href="AuditOperator.action?oID=<s:property value="operatorAccount.id" />"><s:property
								value="operatorAccount.name" /></a>
						</s:if> <s:else>
							<a href="ContractorFlag.action?opID=<s:property value="operatorAccount.id" />"><s:property
								value="operatorAccount.name" /></a>
						</s:else></li>
					</s:if>
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
			<li><label>Current Balance:</label> $<s:property value="contractor.balance" /> USD <s:if
				test="contractor.balance > 0">
				<pics:permission perm="Billing" type="Edit">
					<a href="PaymentDetail.action?id=<s:property value="id" />" class="add">Make a Payment</a>
				</pics:permission>
			</s:if></li>
			<li><label>Billing Status:</label> <s:property value="contractor.billingStatus" /></li>
			<li><label>Must Pay:</label> <s:property value="contractor.mustPay" /></li>
			<li><label>Current Level:</label> $<s:property value="contractor.membershipLevel.amount" /> USD <br>
			<s:property value="contractor.membershipLevel.fee" /></li>
			<s:if test="contractor.newMembershipLevel != contractor.membershipLevel">
				<li><label>New Level:</label> $<s:property value="contractor.newMembershipLevel.amount" /> USD <br>
				<s:property value="contractor.newMembershipLevel.fee" /></li>
			</s:if>

		</ol>
		</fieldset>

		<s:if test="permissions.admin">
			<fieldset class="form"><legend><span>Create Invoice</span></legend> <s:form id="save" method="POST"
				enctype="multipart/form-data">
				<s:hidden name="id" />
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
					<li>
					<div><input type="submit" class="picsbutton positive" name="button" value="Create" /></div>
					</li>
				</ol>
			</s:form> <s:if test="contractor.billingStatus == 'Current' && !contractor.activeB">
				<s:form>
					<s:hidden name="id" />
					<div><input type="submit" class="picsbutton positive" name="button" value="Activate" /></div>
				</s:form>
			</s:if></fieldset>
		</s:if>
		<fieldset class="form bottom"><legend><span>Transaction History</span></legend>
		<ol>
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
						<tr
							<s:if test="#url.length() > 0">
								class="clickable <s:if test="status.void"> inactive</s:if> " 
								onclick="window.location = '<s:property value="#url"/>'"
							</s:if>
							>
							<td><s:property value="class.simpleName" /></td>
							<td class="right"><s:if test="#url.length() > 0">
								<a href="<s:property value="#url" />"><s:property value="id" /></a>
							</s:if><s:else>
								<s:property value="id" />
							</s:else></td>
							<td class="right"><s:date name="creationDate" format="M/d/yy" /></td>
							<td class="right">$<s:property value="totalAmount" /></td>
							<td class="right">$ <s:if
								test="class.simpleName.equals('Payment') && status.toString() == 'Unpaid' && balance > 0">
								-</s:if> <s:property value="balance" /></td>
							<s:if test="permissions.admin">
								<td><s:property value="status"/></td>
							</s:if>
						</tr>
					</s:iterator>
				</tbody>
			</table>
		</ol>
		</fieldset>
		</td>
	</tr>
</table>
 
<div id="notesList"><s:include value="../notes/account_notes_embed.jsp"></s:include></div>

</body>
</html>
