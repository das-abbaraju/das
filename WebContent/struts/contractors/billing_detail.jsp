<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:property value="contractor.name" /> Billing Detail</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<s:include value="conHeader.jsp"></s:include>

	<table width="100%">
		<tr>
			<td style="vertical-align: top; width: 48%;">
				<fieldset class="form">
				<legend><span>Info</span></legend>
				<ol>
					<li><label>Active:</label>
						<s:if test="contractor.active == 'Y'">
							Yes
						</s:if>
						<s:else>
							No
						</s:else>
					</li>
					<li><label>Registration Date:</label>
						<s:date name="contractor.creationDate" format="MMM d, yyyy" />
					</li>
					<li><label title="the date the activation fee was paid">Activation Date:</label>
						<s:date name="contractor.membershipDate" format="MMM d, yyyy" />
					</li>
					<li><label>Will be Renewed:</label> 
						<s:if test="contractor.renew == true">
							Yes
						</s:if>
						<s:else>
							No
						</s:else>
					</li>
					<li><label>Renewal Date:</label>
						<s:date name="contractor.paymentExpires" format="MMM d, yyyy" />
					</li>
					<li><label>Payment Method:</label>
						<s:property value="contractor.paymentMethod" />
					</li>
					<li><label>Credit Card on File?</label>
						<s:if test="contractor.ccOnFile">Yes</s:if>
						<s:else>No</s:else>
					</li>
				</ol>
				</fieldset>
					
				<fieldset class="form">
				<legend><span>Facilities</span></legend>
				<ol>
					<li><label>Requested By:</label>
						<s:property value="requestedBy.name"/>
					</li>
					<li><label>Risk Level:</label>
						<s:property value="contractor.riskLevel"/>
					</li>
					<li><label>Facilities:</label>
						<s:property value="contractor.operators.size()"/> operator(s)<br />
						<br />
						<ul style="float: right; list-style-type: disc;">
							<s:iterator value="contractor.operators">
								<li><s:property value="operatorAccount.name"/></li>
							</s:iterator>
						</ul>
					</li>
					<li><label>Last Upgrade Date:</label>
						<s:date name="contractor.lastUpgradeDate" format="MMM d, yyyy" />
					</li>
				</ol>
				</fieldset>
			</td>
			<td style="width: 5px;">
			</td>
			<td style="vertical-align: top; width: 48%;">
				<fieldset class="form">
				<legend><span>Invoicing</span></legend>
				<ol>
					<li><label>Billing Status:</label>
						<s:property value="contractor.billingStatus" />
					</li>
					<li><label>New Level:</label>
						$<s:property value="contractor.newMembershipLevel.amount" /> USD
						<br ><s:property value="contractor.newMembershipLevel.fee" />
					</li>
					<li><label>Current Level:</label>
						$<s:property value="contractor.membershipLevel.amount" /> USD
						<br ><s:property value="contractor.membershipLevel.fee" />
					</li>
					<li><label>Current Balance:</label>
						$<s:property value="contractor.balance" /> USD
					</li>
				</ol>
				</fieldset>

				<s:if test="permissions.admin">
					<s:form id="save" method="POST" enctype="multipart/form-data">
					<s:hidden name="id" />
						<fieldset class="form">
						<legend><span>Create Invoice</span></legend>
						<ol>
							<s:iterator value="invoiceItems">
								<s:if test="invoiceFee != null">
									<li><label><s:property value="invoiceFee.fee"/>:</label>
										$<s:property value="amount"/> USD
									</li>
								</s:if>
								<s:else>
									<li><label><s:property value="description"/>:</label>
										$<s:property value="amount"/> USD
									</li>
								</s:else>
							</s:iterator>
							<li><label>Total:</label>
								$<s:property value="invoiceTotal"/> USD
							</li>
						</ol>
						<div class="buttons">
							<input type="submit" class="positive" name="button" value="Create"/>
						</div>
						</fieldset>
					</s:form>
				</s:if>
				
				
				<h3 style="margin-top: 450px">Past Invoices</h3>
				<table class="report">
					<thead>
						<tr>
    						<th>Invoice #</a></th>
							<th>Amount</th>
							<th>Due Date</th>	    
							<th>Date Paid</th>	    
						</tr>
					</thead>
					<tbody>
					<s:iterator value="contractor.invoices">
						<tr style="cursor: pointer;" onclick="window.location = 'InvoiceDetail.action?invoice.id=<s:property value="id"/>'">
							<td class="center"><a href="InvoiceDetail.action?id=<s:property value="invoice.id"/>"><s:property value="id" /></a></td>
							<td class="right">$<s:property value="totalAmount" /></td>
							<td class="right"><s:date name="dueDate" format="M/d/yy"/></td>
							<td class="right"><s:date name="paidDate" format="M/d/yy"/></td>
						</tr>
					</s:iterator>
					</tbody>
				</table>					
			</td>
		</tr>
	</table>
</body>
</html>
