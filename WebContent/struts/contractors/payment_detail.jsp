<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="contractor.name" /> - Payment <s:property value="invoice.id" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="all" href="css/invoice.css" />
<link rel="stylesheet" type="text/css" media="all" href="css/audit.css" />
<link rel="stylesheet" type="text/css" media="all" href="css/reports.css" />
</head>
<body>
<s:if test="!permissions.contractor || contractor.activeB">
	<s:include value="conHeader.jsp"></s:include>
</s:if>

<div>
	<s:form>
		<s:hidden name="id" value="%{contractor.id}"/>
		<s:if test="payment != null">
			<s:hidden name="payment.id"/>
		</s:if>
		<div class="auditHeader">
			<s:if test="payment != null">
				<fieldset>
					<ul>
						<li><label>Payment:</label>
							<s:url id="paymentURL">
								<s:param name="payment.id" value="%{payment.id}"/>
							</s:url>
							<s:a href="%{paymentURL}">#<s:property value="payment.id"/></s:a>
						</li>
						<li><label>Total Amount:</label>$<s:textfield name="payment.totalAmount"/></li>
						<li><label>Applied:</label>$<s:property value="payment.amountApplied"/></li>
						<li><label>Balance:</label>$<s:property value="payment.balance"/></li>
					</ul>
				</fieldset>
				<fieldset>
					<ul>
						<li><label>Date:</label><s:date name="payment.creationDate" format="M/d/yy"/></li>
						<li><label>Status:</label><s:property value="payment.status"/></li>
					</ul>
				</fieldset>
				<fieldset>
					<ul>
						<li><label>Method:</label><s:property value="contractor.paymentMethod"/></li>
						<s:if test="contractor.paymentMethod.check">
							<li><label>Check Number:</label><s:textfield name="payment.checkNumber"/></li>
						</s:if>
						<s:if test="contractor.paymentMethod.creditCard">
							<li><label>Transaction ID:</label><s:property value="payment.transactionID"/></li>
						</s:if>
					</ul>
				</fieldset>
			</s:if>
			<s:else>
				<fieldset>
					<ul>
						<li><label>Total Amount:</label>$<s:textfield name="payment.totalAmount"/></li>
						<li><label>Check Number:</label><s:textfield name="payment.checkNumber"/></li>
					</ul>
				</fieldset>
				<br/>
				<input type="submit" class="picsbutton positive" value="Collect Check" name="button"/>
			</s:else>
			<s:if test="payment != null && payment.id > 0">
				<input type="submit" class="picsbutton negative" value="Delete" name="button"/>
			</s:if>
			<div class="clear"></div>
		</div>
		<s:if test="payment.invoices.size > 0">
			<div>
				<h3>Currently Applied Invoices</h3>
				<table class="report">
					<thead>
						<tr>
							<th></th>
							<th>Invoice</th>
							<th>Date</th>
							<th>Invoice Amount</th>
							<th>Balance</th>
							<th>Payment Amount</th>
							<th>Status</th>
						</tr>
					</thead>
				
					<s:iterator value="payment.invoices" id="i">
						<tr>
							<td>
								<s:checkbox name="invoiceID"/>
							</td>
							<td>
								Invoice #<s:property value="invoice.id"/>
							</td>
							<td>
								<s:date name="invoice.creationDate" format="M/d/yy"/>
							</td>
							<td>
								$<s:property value="invoice.totalAmount"/>
							</td>
							<td>
								$<s:property value="invoice.balance"/>
							</td>
							<td>
								$<s:property value="amount"/>
							</td>
							<td>
								<s:property value="invoice.status"/>
							</td>
						</tr>
					</s:iterator>
				</table>
				<br clear="all"/>
			</div>
		</s:if>
		<s:if test="hasUnpaidInvoices">
		<div>
				<h3>Unapplied Invoices</h3>
				<table class="report">
					<thead>
						<tr>
							<s:if test="payment != null">
								<th></th>
							</s:if>
							<th>Invoice</th>
							<th>Date</th>
							<th>Balance</th>
							<s:if test="payment != null">
								<th>Apply</th>
								<th>Auto</th>
							</s:if>
							<th>Status</th>
						</tr>
					</thead>
					<s:iterator value="contractor.invoices" id="i">
						<s:if test="status.unpaid">
							<tr>
								<s:if test="payment != null">
									<td>
										<s:checkbox name="applyMap[%{id}]"/>
									</td>
								</s:if>
								<td>
									<a href="InvoiceDetail.action?invoice.id=<s:property value="id"/>" target="_BLANK">Invoice #<s:property value="id"/></a>
									- <s:property value="status"/>
								</td>
								<td>
									<s:date name="creationDate" format="M/d/yy"/>
								</td>
								<td>
									$<s:property value="balance"/>
								</td>
								<s:if test="payment != null">
									<td>
										$<s:textfield name="amountApplyMap[%{id}]" size="6"/>
									</td>
									<td>
										$<s:property value="getAmountLeft(balance.min(payment.balance))"/>
									</td>
								</s:if>
								<td><s:property value="status"/></td>
							</tr>
						</s:if>
					</s:iterator>
				</table>
				<s:if test="payment != null">
					<input type="submit" class="picsbutton positive" value="Apply" name="button"/>
				</s:if>
				<br clear="all"/>
			</div>
		</s:if>
	</s:form>
</div>

<br clear="all"/>
</body>
</html>
