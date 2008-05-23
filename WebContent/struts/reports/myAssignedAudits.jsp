<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
	<tr>
	    <td>Contractor</td>
	    <td>Type</td>
	    <td>Status</td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td><a href="ContractorView.action?id=<s:property value="[0].get('id')"/>"><s:property value="[0].get('name')"/></a></td>
			<td><a href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>"><nobr><s:property value="[0].get('auditName')"/></nobr></a></td>
			<td><s:property value="[0].get('auditStatus')"/></td>
		</tr>
	</s:iterator>
</table>
