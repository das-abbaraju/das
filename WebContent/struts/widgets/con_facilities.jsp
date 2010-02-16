<%@ taglib prefix="s" uri="/struts-tags"%>

<p><label>Facility Count:</label> <s:property value="operators.size" /></p>
<table class="report">
	<thead>
		<tr>
		<th>Flag</th>
		<th>Facility</th>
		<th>Waiting On</th>
		</tr>
	</thead>
	<s:iterator value="activeOperators">
		<tr>
			<td class="center"><a href="ContractorFlag.action?id=<s:property value="contractor.id" />&opID=<s:property value="operatorAccount.id" />"><s:property value="flagColor.smallIcon" escape="false" /></a></td>
			<td><a href="ContractorFlag.action?id=<s:property value="contractor.id" />&opID=<s:property value="operatorAccount.id" />"><s:property value="operatorAccount.name" /></a></td>
			<td class="center"><s:property value="waitingOn"/></td>
		</tr>
	</s:iterator>
	<tr><td colspan="3" class="right"><a href="ContractorFacilities.action?id=<s:property value="id" />">... add more Facilities</a></td>
	</tr>
</table>
