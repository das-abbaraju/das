<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report" id="requestedContractor">
	<thead>
		<tr>
			<td>Requested Contractor</td>
			<td>Requested By</td>
			<td>Deadline</td>
			<td>Last Call Date</td>
		</tr>
	</thead>
	<s:iterator value="requestedContractors">
		<tr>
			<td><a href="RequestNewContractor.action?requestID=<s:property value="id"/>"><s:property value="name" /></a></td>
			<td><s:property value="requestedBy.name" /></td>
			<td><nobr><s:property value="maskDateFormat(deadline)"/></nobr></td>
			<td class="call"><nobr><s:property value="maskDateFormat(lastContactDate)"/></nobr></td>
		</tr>
	</s:iterator>
</table>