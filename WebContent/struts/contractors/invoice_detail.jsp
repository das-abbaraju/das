<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="contractor.name" /> - Invoice <s:property value="invoice.id" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="all" href="css/invoice.css?v=<s:property value="version"/>" />

<style type="text/css" media="print">
h1 {
	display: none;
}

input[type=submit] {
	display: none;
}
</style>
</head>
<body>
<s:if test="!permissions.contractor || contractor.status.activeDemo">
	<s:include value="conHeader.jsp"></s:include>
</s:if>

<s:if test="invoice.status.void">
	<div class="alert" class="noprint">This invoice has been CANCELED.</div>
</s:if>
<s:elseif test="invoice.status.paid">
	<div class="info" class="noprint">This invoice has been PAID in full.</div>
</s:elseif>
<s:elseif test="invoice.overdue && contractor.status.active">
	<div class="alert" class="noprint">This invoice is currently OVERDUE!</div>
</s:elseif>
<s:if test="invoice.status.unpaid && !contractor.paymentMethodStatusValid && contractor.mustPayB">
	<div class="alert" class="noprint">Our records show that you do not have a valid method of payment on file, 
	or your credit card has expired. Please <a href="ContractorPaymentOptions.action?id=<s:property value="contractor.id"/>"> update your payment information.</a></div>
</s:if>

<s:if test="permissions.admin">
	<s:if test="invoice.status.unpaid && invoice.totalAmount == 0">
	<div class="alert" class="noprint">Please post a note after you have modified the invoice!</div>
</s:if>
<s:if test="invoice.qbSync">
	<div class="alert" class="noprint">This invoice is still waiting to be synced with QuickBooks!</div>
</s:if>
</s:if>

<s:form id="save" name="save" method="POST">
	<s:hidden name="id"></s:hidden>
	<s:hidden name="invoice.id"></s:hidden>
	<s:hidden name="button" value="Save"></s:hidden>

	<table width="100%">
		<tr>
			<td>
			<table width="100%">
				<tr>
					<td width="100"><img src="images/logo_sm.png" alt="image" width="100" height="31" /></td>
					<td style="padding: 10px;">PICS <br>
					P.O. Box 51387 <br>
					Irvine, CA 92619-1387</td>
					<td width="400">
					<table width="100%" border="0" cellspacing="0" cellpadding="4" class="allborder">
						<tr>
							<th>Date</th>
							<th class="big" style="white-space: nowrap;">Invoice #</th>
						</tr>
						<tr>
							<td class="center"><nobr> <s:date name="invoice.creationDate" format="MMM d, yyyy" /> <s:set
								name="o" value="invoice"></s:set> <s:include value="../who.jsp"></s:include> </nobr></td>
							<td class="center"><s:property value="invoice.id" /></td>
						</tr>
					</table>
						<s:set name="urlBase" value="InvoiceDetail.action?invoice.id={%invoice.id}" />
						<s:property value="#urlBase"/>

					<div id="toolbox" class="noprint">
					<ul>
						<pics:permission perm="Billing" type="Edit">
							<s:if test="edit">
								<li><a class="save" href="#" onclick="document.forms['save'].submit(); return false;">Save</a></li>
								<li><a class="exit" href="InvoiceDetail.action?invoice.id=<s:property value="invoice.id"/>">Return</a></li>
							</s:if>
							<s:else>
								<s:if test="invoice.status.unpaid">
									<li><a class="pay" href="PaymentDetail.action?id=<s:property value="id"/>&amountApplyMap[<s:property value="invoice.id"/>]=<s:property value="invoice.balance"/>">Pay</a></li>
								</s:if>
								<li><a class="edit" href="InvoiceDetail.action?invoice.id=<s:property value="invoice.id"/>&edit=true">Edit</a></li>
								<pics:permission perm="Billing" type="Delete">
									<li><a class="void"
										href="InvoiceDetail.action?invoice.id=<s:property value="invoice.id"/>&button=Cancel" onclick="return confirm('Are you sure you want to cancel this invoice?');">Void</a></li>
								</pics:permission>
								<pics:permission perm="InvoiceEdit">
									<li><a class="system_edit" href="ConInvoiceMaintain.action?id=<s:property value="id"/>&invoiceId=<s:property value="invoice.id"/>">Sys Edit</a></li>
								</pics:permission>
							</s:else>
						</pics:permission>
						<s:if test="!edit">
							<s:if test="permissions.contractor && invoice.status.unpaid && contractor.ccValid">
								<li><a class="pay" href="InvoiceDetail.action?invoice.id=<s:property value="invoice.id"/>&button=pay" onclick="return confirm('The credit card on file (<s:property value="ccNumber" />) will be charged. Do you wish to continue?'); this.disable()">Pay</a></li>
							</s:if>
							<li><a class="email"
								href="InvoiceDetail.action?invoice.id=<s:property value="invoice.id"/>&button=Email">Email</a></li>
							<li><a class="print" href="javascript: window.print();">Print</a></li>
						</s:if>
					</ul>
					</div>
					</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td style="padding-top: 15px;">
			<table width="100%" class="allborder">
				<tr>
					<th>Bill To</th>
					<th width="16%">PO #</th>
					<th width="16%">Due Date</th>
				</tr>
				<tr>
					<td><s:property value="contractor.name" /><br />
						c/o <s:property value="billingUser.name" />
						<br />
					<s:if test="contractor.billingAddress.length() > 0">
						<s:property value="contractor.billingAddress" /><br />
						<s:property value="contractor.billingCity" />, <s:property value="contractor.billingState.isoCode" />
						<s:property	value="contractor.billingZip" />
					</s:if>
					<s:else>
						<s:property value="contractor.address" /><br />
						<s:property value="contractor.city" />, <s:property value="contractor.state.isoCode" />
						<s:property	value="contractor.zip" />
					</s:else>
					</td>
					<td><s:if test="edit">
						<s:textfield name="invoice.poNumber" size="20" />
					</s:if> <s:else>
						<s:property value="invoice.poNumber" />
					</s:else></td>
					<td class="center"><s:if test="edit">
						<s:textfield name="invoice.dueDate" size="10" />
					</s:if> <s:else>
						<s:date name="invoice.dueDate" format="MMM d, yyyy" />
					</s:else></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td style="padding-top: 15px;">

			<table width="100%" class="allborder">
				<tr>
					<th colspan="2">Item &amp; Description</th>
					<th width="200px">Fee Amount</th>
				</tr>
				<s:iterator value="invoice.items" status="stat">
					<tr>
						<td style="border-right: 0"><s:set name="o" value="[0]"></s:set> <s:include value="../who.jsp"></s:include> <s:property
							value="invoiceFee.fee" /> <span style="color: #444; font-style: italic; font-size: 10px;"> <s:if
							test="invoiceFee.feeClass == 'Activation'">effective
								<s:if test="paymentExpires == null">
								<s:date name="invoice.creationDate" format="MMM d, yyyy" />
							</s:if>
							<s:else>
								<s:date name="paymentExpires" />
							</s:else>
						</s:if> <s:if test="invoiceFee.feeClass == 'Membership' && paymentExpires != null">
								expires <s:date name="paymentExpires" format="MMM d, yyyy" />
						</s:if> </span></td>
						<s:if test="edit">
							<td><s:textfield name="invoice.items[%{#stat.index}].description" value="%{description}" size="30" />
							(optional description) <pics:permission perm="InvoiceEdit" type="Edit">
								<s:if test="invoiceFee.feeClass == 'Membership' && invoiceFee.fee != contractor.newMembershipLevel.fee">
									<div class="buttons"><a
										href="InvoiceDetail.action?invoice.id=<s:property value="invoice.id" />&button=Change to"
										class="picsbutton positive">Change to: <s:property value="contractor.newMembershipLevel.fee" /></a></div>
								</s:if>
							</pics:permission></td>
							<td class="right"><s:textfield value="%{amount}" size="6" name="invoice.items[%{#stat.index}].amount" /> <s:property value="invoice.currency"/>
							</td>
						</s:if>
						<s:else>
							<td style="border-left: 0"><s:property value="description" /></td>
							<td class="right"><s:property value="amount" /> <s:property value="invoice.currency"/></td>
						</s:else>
					</tr>
				</s:iterator>
				<s:if test="edit">
					<tr>
						<td colspan="2"><s:select list="feeList" name="newFeeId" headerKey="0"
							headerValue="- Select a New Fee to Add -" listKey="id" listValue="fee" /></td>
						<td class="right">___ <s:property value="invoice.currency"/></td>
					</tr>
				</s:if>
				<tr>
					<th colspan="2" class="big right">Invoice Total</th>
					<td class="big right"><s:property value="invoice.totalAmount" /> <s:property value="invoice.currency"/></td>
				</tr>
				<s:if test="invoice.payments.size() > 0">
					<tr>
						<th colspan="2" class="big right">Payment(s)</th>
						<td class="right"><s:iterator value="invoice.payments">
							<pics:permission perm="Billing">
								<a href="PaymentDetail.action?payment.id=<s:property value="payment.id" />"><s:date name="payment.creationDate" format="MMM d, yyyy" /></a>
							</pics:permission>
							<pics:permission perm="Billing" negativeCheck="true">
								<s:date name="payment.creationDate" format="MMM d, yyyy" />
							</pics:permission>
							
							<br />
							<span class="small">
							<s:if test="payment.paymentMethod.creditCard">
								<s:if test="payment.ccType == null || payment.ccType.length() == 0">
									Credit Card
								</s:if>
								<s:else>
									<s:property value="payment.ccType"/>
								</s:else>
							</s:if>
							<s:else>
								Check <s:if test="payment.checkNumber != null && payment.checkNumber.length() > 0">#<s:property value="payment.checkNumber"/></s:if>
							</s:else>
							</span>
							<span class="big">(<s:property value="amount" /> <s:property value="invoice.currency"/>)</span>
							<br />
						</s:iterator></td>
					</tr>
					<tr>
						<th colspan="2" class="big right">Balance</th>
						<td class="big right"><s:property value="invoice.balance" /> <s:property value="invoice.currency"/></td>
					</tr>
				</s:if>
			</table>
			</td>
		</tr>
		<tr>
			<td style="padding: 15px;">Comments: <s:if test="edit">
				<br />
				<s:textarea name="invoice.notes" cols="60" rows="4"></s:textarea>
			</s:if> <s:else>
				<s:property value="invoice.notes" />
			</s:else></td>
		</tr>
		<tr>
			<td>

			<table width="100%" class="allborder">
				<tr>
					<th width="25%">Phone#</th>
					<th width="25%">Fax#</th>
					<th width="25%">Email</th>
					<th width="25%">Website</th>
				</tr>
				<tr>
					<td class="center">(800) 506-PICS (7427)</td>
					<td class="center">(949) 269-9146</td>
					<td class="center">billing@picsauditing.com</td>
					<td class="center">www.picsauditing.com</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form>

<div style="font-style: italic; font-size: 10px;"></div>

</body>
</html>
