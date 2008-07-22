<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<tr>
		<th>Contractor</th>
		<th>InvoiceDate</th>
		<th>Days Left</th>
		</tr>
	</thead>
	<s:iterator value="delinquentContractors">
		<tr>
			<td><a href="ContractorView.action?id=<s:property value="id"/>"><s:property value="name"/></a></td>
			<td class="center"><s:date name="lastInvoiceDate" format="M/d/yy" /></td>
			<td class="center"><s:property value="getDaysLeft(lastInvoiceDate)" /></td>
		</tr>
	</s:iterator>
	<tr><td colspan="3" class="right"><a href="DelinquentContractorAccounts.action">... More</a></td>
	</tr>
</table>
