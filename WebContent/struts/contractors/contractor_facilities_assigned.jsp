<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<table class="report">
	<thead>
		<th>Operator Name</th>
		<pics:permission perm="RemoveContractors">
			<th>Remove Operator</th>		
		</pics:permission>
	</thead>
	<tbody>
<s:sort source="contractor.operators" comparator="conOpComparator">
<s:iterator>
	<tr>
		<td><s:property value="operatorAccount.name"/></td>
		<pics:permission perm="RemoveContractors">
			<td><a href="#" onclick="javascript: return removeOperator( <s:property value="contractor.id"/>, <s:property value="operatorAccount.id"/> );">Remove</a></td>		
		</pics:permission>
	</tr>
</s:iterator>
</s:sort>
	</tbody>
</table>