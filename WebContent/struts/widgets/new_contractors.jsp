<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<tr>
			<th>Contractor</th>
			<s:if test="permissions.admin">
				<th>Requested By</th>
			</s:if>
			<th>Date <s:if test="permissions.admin">Registered</s:if><s:else>Added</s:else></th>
		</tr>
	</thead>
	<s:iterator value="newContractors">
		<tr>
			<s:if test="permissions.admin">
				<td><a class="account<s:property value="status" />" 
				href="ContractorView.action?id=<s:property value="id"/>"><s:property value="name" /></a></td>
			</s:if>
			<s:elseif test="permissions.operatorCorporate">
				<td><a class="account<s:property value="status" />" 
					href="ContractorView.action?id=<s:property value="contractorAccount.id"/>"><s:property value="contractorAccount.name" /></a></td>
			</s:elseif>
			<s:if test="permissions.admin">
				<td><s:property value="requestedBy.name" /></td>
			</s:if>
			<td class="center"><s:date name="creationDate" format="MMM dd yyyy" /></td>
		</tr>
	</s:iterator>
</table>
