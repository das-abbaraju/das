<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:property value="contractor.name" /> Billing Detail</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css" />
</head>
<body>
<s:include value="conHeader.jsp"></s:include>

<br clear="all" />
<table>
	<tr>
		<td>
		<fieldset class="form"><legend><span>PICS Information</span></legend>
		<ol>
			<li><label>Name:</label> PICS</li>
			<li><label>Address:</label> 17701 Cowan St. Ste 140</li>
			<li><label>City:</label>Irvine</li>
			<li><label>State:</label>CA</li>
			<li><label>Zip:</label>92614</li>
			</li>
		</ol>
		</fieldset>
		</td>
		<td>
		<fieldset class="form"><legend><span>Billing Information</span></legend>
		<ol>
			<li><label>Name: </label><a href="ContractorView.action?id=<s:property value="contractor.id"/>"><s:property value="contractor.name" /></a>
			<li><label>Address:</label> <s:property value="contractor.address" /></li>
			<li><label>City:</label><s:property value="contractor.city" /></li>
			<li><label>State:</label><s:property value="account.state" /></li> 
			<li><label>Zip:</label><s:property	value="account.zip" /></li>
			</li>
		</ol>
		</fieldset>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<s:form id="save" method="POST" enctype="multipart/form-data">
				<s:hidden name="id" />
				<fieldset class="form"><legend><span>Invoice</span></legend>
				<ol>
					<li>
						<label>Invoice #</label><s:property value="invoice.id" />
					</li>
					<li><label>Invoice Items:</label><br/>
						<s:iterator value="invoice.items">
							<br/><s:property value="invoiceFee.fee"/> $<s:property value="invoiceFee.amount"/> USD
						</s:iterator>
					</li>
					<li>
						<label>Invoice Total:</label> $<s:property value="invoice.totalAmount" /> USD
					</li>
				</ol>
				<s:if test="permissions.admin && contractor.paymentMethodStatus != 'Missing' && !invoice.paid">
					<div class="buttons">
						<input type="submit" class="positive" name="button" value="Charge" />
					</div>
				</s:if>
				</fieldset>
			</s:form>
		</td>
	</tr>
</table>
</body>
</html>
