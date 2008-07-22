<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<tr>
		<th>Contractor</th>
		<th>InvoiceDate</th>
		</tr>
	</thead>
	<s:iterator value="delinquentContractors">
		<tr>
			<td><a href="ContractorView.action?id=<s:property value="id"/>"><s:property value="name"/></a></td>
			<td class="center"><s:date name="lastInvoiceDate" format="M/d/yy" /></td>
		</tr>
	</s:iterator>
	<tr><td colspan="2" class="right"><a href="DelinquentContractorAccounts.action">... More</a></td>
	</tr>
</table>
