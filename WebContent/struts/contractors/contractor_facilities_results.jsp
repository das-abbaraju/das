<%@ taglib prefix="s" uri="/struts-tags"%>

<s:set name="conID" value="contractor.id"/>

<s:if test="searchResults.size == 0">
	<div id="alert">No Facilities Found</div>
</s:if>
<s:else>
<table class="report">
	<thead>
		<tr>
			<th>Operator Name</th>
			<th>Add Operator</th>
		</tr>
	</thead>
	<tbody>
	<s:iterator value="searchResults">
		<tr id="results_<s:property value="id"/>">
			<td><s:property value="name"/></td>
			<td><a href="#" onclick="javascript: return addOperator( <s:property value="#conID"/>, <s:property value="id"/> );">Add</a></td>
		</tr>
	</s:iterator>
	</tbody>
</table>
</s:else>
