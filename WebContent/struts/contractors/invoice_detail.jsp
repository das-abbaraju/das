<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
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
					<s:if test="!permissions.contractor">
						<s:if test="!invoice.paid">
							<a class="edit noprint"
								href="InvoiceDetail.action?invoice.id=<s:property value="invoice.id"/>&edit=<s:property value="!edit"/>">
								<s:if test="edit">View</s:if>
								<s:else>Edit</s:else>
							</a>
						</s:if>
					</s:if>
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
						<s:if test="edit"><s:textfield name="invoice.poNumber" size="20" /></s:if>
						<s:else><s:property value="invoice.poNumber" /></s:else>
					</td>
					<td class="center">
						<s:if test="edit"><s:textfield name="invoice.dueDate" size="10" /></s:if>
						<s:else><s:date name="invoice.dueDate" format="MMM d, yyyy" /></s:else>
					</td>
					<td class="center">
						<s:property value="invoice.paymentMethod.description" />
					</td>
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
										value="amount" /> USD
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
							<td>
								<s:date name="invoice.paidDate" format="MMM d, yyyy"/>
								<br /><s:property value="invoice.checkNumber" />
							</td>
						</s:if>
						<s:else>
							<pics:permission perm="Billing" type="Edit">
							<s:if test="invoice.totalAmount > 0">
							<td colspan="3" class="print noprint">
								<s:if test="invoice.paymentMethod.creditCard">
									<s:if test="contractor.ccOnFile">
										<input type="submit" name="button" value="Charge Credit Card for $<s:property value="invoice.totalAmount" />">
									</s:if>
									<s:else>
										No Credit Card on File
									</s:else>								
								</s:if>
								<s:else>
									Check#<s:textfield name="invoice.checkNumber" size="8"></s:textfield>
									<input type="submit" name="button" value="Collect Check for $<s:property value="invoice.totalAmount" />">
								</s:else>
								</td>
							</s:if>	
							</pics:permission>
						</s:else>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td style="padding: 15px;">
			Comments: 
					<s:if test="edit"><br /><s:textarea name="invoice.notes" cols="60" rows="4" value="%{contractorNotes}"></s:textarea> </s:if>
					<s:else><s:property value="contractorNotes"/><br /><br /><br /></s:else>
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
</body>
</html>
