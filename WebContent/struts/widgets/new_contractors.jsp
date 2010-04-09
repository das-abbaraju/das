<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<tr>
			<th>Contractor</th>
			<s:if test="permissions.admin">
				<th>Requested By</th>
			</s:if>
			<th>Date</th>
		</tr>
	</thead>
	<s:iterator value="newContractors">
		<tr>
			<td><a class="account<s:property value="status" />" 
				href="ContractorView.action?id=<s:property value="id"/>"><s:property value="name" /></a></td>
			<s:if test="permissions.admin">
				<td><s:property value="requestedBy.name" /></td>
			</s:if>
			<td class="center"><s:date name="creationDate" format="MMM d HH:mm" /></td>
		</tr>
	</s:iterator>
</table>
