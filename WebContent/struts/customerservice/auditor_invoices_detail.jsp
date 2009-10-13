<%@ taglib prefix="s" uri="/struts-tags"%>
<h1>Invoice <s:date name="auditList.get(0).paidDate" format="MMM d, yyyy"/>
	<span class="sub"><s:property value="auditList.get(0).auditor.name"/></span>
</h1>

<button class="noprint" onclick="window.print();">Print</button>
<div id="mainThinkingDiv" class="noprint"></div>
<table class="report" id="invoicedetail">
	<thead>
		<tr>
			<td>#</td>
			<td>Audit Type</td>
			<td>Date</td>
			<td>Contractor</td>
			<td></td>
		</tr>
	</thead>
	<s:iterator value="auditList" status="stat">
	<tr>
		<td class="center"><s:property value="#stat.index + 1"/></td>
		<td><a href="Audit.action?auditID=<s:property value="id"/>"><s:property value="auditType.auditName"/></a></td>
		<td><a href="ContractorView.action?id=<s:property value="contractorAccount.id"/>"><s:property value="contractorAccount.name"/></a></td>
		<td><s:date name="completedDate" format="MMM d"/></td>
		<td class="right">$<s:if test="auditType.id == 2">75</s:if> </td>
	</tr>
	</s:iterator>
	<tr>
		<td></td>
		<td class="right" colspan="3"><h4>Total</h4></td>
		<td class="right"><h4>$<s:property value="total"/></h4></td>
	</tr>
</table>
