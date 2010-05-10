<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<tr>
			<td>Requested Contractor</td>
			<td>Requested By</td>
			<td>Deadline</td>
		</tr>
	</thead>
	<s:iterator value="requestedContractors">
		<tr>
			<td><a href="RequestNewContractor.action?requestID=<s:property value="id"/>"><s:property value="name" /></a></td>
			<td><s:property value="requestedBy.name" /></td>
			<td><nobr><s:property value="formatDate(deadline, 'MM/dd/yyyy')"/></nobr></td>
		</tr>
	</s:iterator>
</table>
