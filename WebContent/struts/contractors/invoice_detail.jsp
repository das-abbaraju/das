<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:property value="contractor.name" /> Billing Detail</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
</head>
<body>
<s:include value="conHeader.jsp"></s:include>

<br clear="all" />

	<table>
		<tr>
			<td style="vertical-align: top; width: 50%;">
				<fieldset class="form">
				<legend><span>Details</span></legend>
				<ol>
					<li><label>Name:</label>
						<s:property value="contractor.name"/>
					</li>
					<li><label>Requested By:</label>
						<s:property value="requestedBy.name"/>
					</li>
					<li><label>Facilities:</label>
						<s:iterator value="contractor.operators">
							<br/><s:property value="operatorAccount.name"/>
						</s:iterator>
					</li>
					<li><label>Active:</label>
						<s:property value="contractor.active"/>
					</li>
					<li><label>Current Level:</label> 
						$<s:property value="currentMemebershipFee.amount" /> USD
					</li>
					<li><label>New Level:</label> 
						$<s:property value="newMembershipFee.amount" /> USD
					</li>
					<li><label>Billing State:</label>
						<s:property value="contractor.billingStatus" />
					</li>
					<li><label>Payment Method:</label>
						<s:property value="contractor.paymentMethod" />
					</li>
					<li><label>Payment Method Status:</label>
						<s:property value="contractor.paymentMethodStatus" />
					</li>
					
				</ol>
				</fieldset>
				<fieldset class="form">
				<legend><span>Invoicing</span></legend>
				<ol>
					<li><label>Past Invoices:</label>
							<br/>Invoice # Amount Paid Date Paid
						<s:iterator value="contractor.invoices">
							<br/><s:property value="id"/> <s:property value="totalAmount"/> <s:property value="paidDate"/>
						</s:iterator>
					</li>
					<li><label>Current Balance:</label>
						$<s:property value="contractor.balance" /> USD
					</li>
					<li><label>Renewal Date:</label>
						<s:date name="contractor.paymentExpires" format="MMM d, yyyy" />
					</li>
					<li><label>Will be Renewed:</label> 
						<s:if test="contractor.renew == true">
							Yes
						</s:if>
						<s:else>
							No
						</s:else>
					</li>
					<li><label>Activation Date:</label>
						<s:date name="contractor.membershipDate" format="MMM d, yyyy" />
					</li>
					<li><label>Last Upgrade Date:</label>
						<s:date name="contractor.lastUpgradeDate" format="MMM d, yyyy" />
					</li>

				</ol>
				</fieldset>
			</td>
			<s:if test="permissions.admin">
			<td style="vertical-align: top; width: 50%; padding-left: 10px;">
			<s:form id="save" method="POST" enctype="multipart/form-data">
			<s:hidden name="id" />
				<fieldset class="form">
				<legend><span>Create Invoice</span></legend>
				<ol>
					<s:iterator value="invoiceItems">
						<li><label><s:property value="invoiceFee.fee"/>:</label>
							$<s:property value="amount"/> USD
						</li>
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
			</td>
		</s:if>			
		</tr>
	</table>
	
</body>
</html>
