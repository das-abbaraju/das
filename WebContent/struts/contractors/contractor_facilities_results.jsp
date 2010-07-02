<%@ taglib prefix="s" uri="/struts-tags"%>

<s:set name="conID" value="contractor.id"/>

<s:if test="searchResults.size == 0">
	<div class="alert">No Facilities Found</div>
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
			<td	class="account<s:property value="status" />"><s:property value="name"/></td>
			<td class="center"><a id="facility_<s:property value="id"/>" href="#" onclick="javascript: return addOperator( <s:property value="#conID"/>, <s:property value="id"/>);"
				class="add">Add</a></td>
		</tr>
	</s:iterator>
	</tbody>
</table>

<s:include value="../actionMessages.jsp"/>

</s:else>
