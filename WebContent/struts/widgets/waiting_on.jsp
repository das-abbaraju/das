<%@ taglib prefix="s" uri="/struts-tags"%>
<s:include value="../actionMessages.jsp" />
<table class="report">
	<thead>
	<tr>
	    <th>Contractor</th>
	    <th>Waiting On PICS</th>
	</tr>
	</thead>
	<tbody>
		<s:iterator value="data">
		<tr>
			<td><a href="ContractorView.action?id=<s:property value="get('conID')"/>"><s:property value="get('name')" /></a></td>
			<td><a href="Audit.action?auditID=<s:property value="get('id')" />"><s:property value="get('auditName')" /> (<s:property value="get('percentVerified')" />% Verified)</a></td>
		</tr>
		</s:iterator>
	</tbody>
</table>