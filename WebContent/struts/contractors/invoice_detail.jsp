<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:property value="contractor.name" /> - Invoice <s:property
	value="invoice.id" /></title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="all"
	href="css/invoice.css" />

<style type="text/css" media="print">
h1 {
	display: none;
}
</style>
</head>
<body>
<s:include value="conHeader.jsp"></s:include>

<s:if test="invoice.paid">
	<div id="info" class="noprint">This invoice was paid on <s:date name="invoice.paidDate" format="MMM d, yyyy"/></div>
</s:if>
<s:elseif test="invoice.overdue">
	<div id="alert" class="noprint">This invoice is currently overdue!</div>
</s:elseif>

<s:form id="save" method="POST">
	<s:hidden name="edit"></s:hidden>
	<s:hidden name="id"></s:hidden>
	<s:hidden name="invoice.id"></s:hidden>

	<table width="100%">
		<tr>
			<td>
			<table width="100%">
				<tr>
					<td width="146"><img 
						src="images/logo.gif" width="146" height="146" /></td>
					<td style="padding: 10px;">PICS <br>
							P.O. Box 51387 <br>
							Irvine, CA 92619-1387</td>
					<td width="200">
					<table width="100%" border="0" cellspacing="0" cellpadding="4"
						class="allborder">
						<tr>
							<th>Date</th>
							<th class="big">Invoice #</th>
						</tr>
						<tr>
							<td class="center"><s:date name="invoice.creationDate" format="MMM d, yyyy" /></td>
							<td class="center"><s:property value="invoice.id" /></td>
						</tr>
					</table>
					
					<div class="center">
					<a class="edit noprint"
						href="InvoiceDetail.action?invoice.id=<s:property value="invoice.id"/>&edit=<s:property value="!edit"/>">
						<s:if test="edit">View</s:if>
						<s:else>Edit</s:else>
					</a>
					<a class="print noprint" href="javascript: window.print();">Print</a>
					<s:if test="edit">
						<br />
						<input type="submit" class="positive" name="button" value="Save" />
					</s:if>
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
					<th width="16%">Payment Method</th>
				</tr>
				<tr>
					<td><s:property value="contractor.name" /><br />
					<s:if test="contractor.billingContact.length > 0">
						c/o <s:property value="contractor.billingContact" /><br />
					</s:if>
					<s:property value="contractor.address" /><br />
					<s:property value="contractor.city" />, <s:property
						value="contractor.state" /> <s:property value="contractor.zip" /></td>
					<td>
						<s:if test="edit"><s:textfield name="invoice.poNumber" size="10" /></s:if>
						<s:else><s:property value="invoice.poNumber" /></s:else>
					</td>
					<td><s:date name="invoice.dueDate" format="MMM d, yyyy" /></td>
					<td><s:property value="invoice.paymentMethod" /></td>
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
							<td style="border-right: 0">
								<s:property
								value="invoiceFee.fee" />
							</td>
							<s:if test="edit">
								<td>
									<s:textfield
										name="invoice.items[%{#stat.index}].description"
										value="%{description}" size="30" /> (optional description)
								</td>
								<td class="right">
									$<s:textfield value="%{amount}" size="6"
										name="invoice.items[%{#stat.index}].amount" /> USD
								</td>
							</s:if>
							<s:else>
								<td style="border-left: 0">
									<s:property value="description" />
								</td>
								<td class="right">
									$<s:property
										value="invoiceFee.amount" /> USD
								</td>
							</s:else>
						</tr>
					</s:iterator>
					<s:if test="edit">
						<tr>
							<td colspan="2"><s:select list="feeList"
								name="newFeeId" headerKey="0"
								headerValue="- Select a New Fee to Add -" listKey="id"
								listValue="fee" /></td>
							<td class="right">$___ USD</td>
						</tr>
					</s:if>
					<tr>
						<th colspan="2" class="big right">Total</th>
						<td class="big right">$<s:property value="invoice.totalAmount" /> USD</td>
					</tr>
					<tr>
						<s:if test="invoice.paid">
							<th colspan="2" class="right">Paid</th>
							<td><s:date name="invoice.paidDate" format="MMM d, yyyy"/></td>
						</s:if>
						<s:else>
						</s:else>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td style="padding: 15px;">
			Comments: 
					<s:if test="edit"><br /><s:textarea name="invoice.notes" cols="60" rows="4"></s:textarea> </s:if>
					<s:else><s:property value="invoice.notes"/> <br /><br /><br /></s:else>
			</td>
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
					<td class="center">949-387-1940</td>
					<td class="center">949-269-9146</td>
					<td class="center">billing@picsauditing.com</td>
					<td class="center">www.picsauditing.com</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</s:form>
<div>https://www.picsauditing.com/InvoiceDetail.action?invoice.id=<s:property value="invoice.id"/></div>
</body>
</html>
