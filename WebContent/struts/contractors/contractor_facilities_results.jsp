<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<th>Operator Name</th>
		<th>Add Operator</th>
	</thead>
	<tbody>
	<s:set name="conId" value="contractor.id"/>
<s:sort source="searchResults" comparator="opComparator">
<s:iterator>
	<tr id="results_<s:property value="id"/>">
		<td><s:property value="name"/></td>
		<td><a href="#" onclick="javascript: return addOperator( <s:property value="#attr.conId"/>, <s:property value="id"/> );">Add</a></td>
	</tr>
</s:iterator>
</s:sort>
	</tbody>
</table>