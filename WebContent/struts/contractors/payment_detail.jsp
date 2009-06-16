<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="contractor.name" /> - Payment <s:property value="invoice.id" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="all" href="css/invoice.css" />
<link rel="stylesheet" type="text/css" media="all" href="css/audit.css" />
</head>
<body>
<s:if test="!permissions.contractor || contractor.activeB">
	<s:include value="conHeader.jsp"></s:include>
</s:if>

<div id="<s:property value="id"/>">
	<s:form>
		<div class="auditHeader">
			<fieldset>
				<ul>
					<li><label>Payment:</label>#<s:property value="payment.id"/></li>
					<li><label>Total Amount:</label>$<s:property value="payment.totalAmount"/></li>
					<li><label>Applied:</label>$<s:property value="payment.amountApplied"/></li>
					<li><label>Balance:</label>$<s:property value="payment.balance"/></li>
				</ul>
			</fieldset>
			<fieldset>
				<ul>
					<li><label>Date:</label><s:date name="payment.creationDate" format="M/d/yy"/></li>
				</ul>
			</fieldset>
			<div class="clear"></div>
		</div>
		<div>
			<s:iterator value="contractor.invoices" id="i">
				<s:include value="con_invoice_embed.jsp"/>
				<div>
					<table>
						<tr>
							<td>
								Invoice #<s:property value="id"/>
							</td>
							<td>
								$<s:property value="totalAmount"/>
							</td>
							<td>
								$<s:textfield name="apply" value="%{payment.balance}" size="6"/>
							</td>
							<td>
								<input type="submit" class="picsbutton positive" value="Apply"/>
							</td>
						</tr>
					</table>
				</div>
			</s:iterator>
		</div>
	</s:form>
</div>

<br clear="all"/>
</body>
</html>
