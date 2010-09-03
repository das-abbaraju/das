<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report" id="wynnewoodRates" width="100%">
	<thead>
		<tr>
			<td>Contractor</td>
			<td>Rate</td>
			<td>Contract</td>
		</tr>
	</thead>
	<s:if test="contractRates.size() == 0">
		<tr><td colspan="4" class="center">You have no outstanding Contract Rates to Approve</td></tr>
	</s:if>
	<s:else>
	<s:iterator value="contractRates.keySet()" id="key">
		<tr>
			<td><a href="ContractorView.action?id=<s:property value="contractorAccount.id" />"><s:property value="contractorAccount.name" /></a></td>
			<td class="center" width="5">
				<s:if test="contractRates.get(#key) != null">
					<a href="AuditCat.action?auditID=<s:property value="contractRates.get(#key).id" />#node_3153">Rate</a>
				</s:if>
				<s:else>
					No Rate
				</s:else>
			</td>
			<td class="center" width="5"><a href="AuditCat.action?auditID=<s:property value="id" />">Contract</a></td>
		</tr>
	</s:iterator>
	</s:else>
</table>