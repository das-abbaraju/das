<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<tr>
			<th>Contractor</th>
			<th>Date</th>
		</tr>
	</thead>
	<s:iterator value="newContractors">
		<tr>
			<td>
				<a href="ContractorView.action?id=<s:property value="id"/>"><s:property value="name" /></a>
				<s:if test="permissions.admin">
					<s:property value="requestedById" />
				</s:if>
			</td>
			<td class="center"><s:date name="creationDate" format="M/d HH" /></td>
		</tr>
	</s:iterator>
</table>
