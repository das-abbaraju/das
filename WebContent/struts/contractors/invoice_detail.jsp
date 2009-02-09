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
			<li><label>City:</label>Irvine<label>State:</label>CA<label>Zip:</label>92614
			</li>
		</ol>
		</fieldset>
		</td>
		<td>
		<fieldset class="form"><legend><span>Billing Information</span></legend>
		<ol>
			<li>
				<label>Name:</label> <s:property value="account.name" />
			</li>
			<li>
				<label>Address:</label> <s:property value="account.address" />
			</li>
			<li>
				<label>City:</label><s:property value="account.city" />
				<label>State:</label><s:property value="account.state" /> 
				<label>Zip:</label><s:property	value="account.zip" />
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
					<s:iterator value="contractor.invoices">
						<li>
						<label><s:property value="items.invoiceFee.fee" />:</label> 
							$<s:property value="amount" /> USD
						</li>
					</s:iterator>
					<li>
						<label>Total:</label> 
						$<s:property value="totalAmount" /> USD
					</li>
				</ol>
				<s:if test="permissions.admin">
				<div class="buttons"><input type="submit" class="positive" name="button" value="Charge" />
				</div>
				</s:if>
				</fieldset>
			</s:form>
		</td>
	</tr>
</table>
</body>
</html>
