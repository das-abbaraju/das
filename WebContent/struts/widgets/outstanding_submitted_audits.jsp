<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
	<tr>
		<td>Contractor</td>
		<td>Type</td>
		<td>Submitted</td>
	</tr>
	</thead>
	<s:if test="submittedAudits.size() > 0">
		<s:iterator value="submittedAudits" status="stat">
			<tr>
				<td><a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="get('name')"/></a></td>
				<td><a href="Audit.action?auditID=<s:property value="get('auditID')"/>"><s:property value="get('auditName')"/></a></td>
				<td class="center"><s:date name="get('completedDate')" format="M/d/yy" /></td>
			</tr>
		</s:iterator>
	</s:if>
	<s:else>
		<tr><td colspan="2" class="center"> No outstanding submitted audits currently</td></tr>
	</s:else>
</table>