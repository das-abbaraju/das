<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<tr>
			<th>Contractor</th>
			<th>Date</th>
		</tr>
	</thead>
	<s:iterator value="pendingApprovalBiddingContractors">
		<tr>
			<td><a href="ContractorView.action?id=<s:property value="contractorAccount.id"/>"><s:property value="contractorAccount.name" /></a></td>
			<td class="center"><s:date name="creationDate" format="MMM d HH:mm" /></td>
		</tr>
	</s:iterator>
</table>
