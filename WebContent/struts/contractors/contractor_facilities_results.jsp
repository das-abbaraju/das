<%@ taglib prefix="s" uri="/struts-tags"%>

<s:set name="conID" value="contractor.id"/>
<s:set name="con" value="contractor"/>

<s:if test="searchResults.size() == 0">
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
	<s:iterator value="searchResults" id="op">
		<tr id="results_<s:property value="id"/>">
			<td	class="account<s:property value="status" />">
				<s:property value="name"/>
			</td>
			<td class="center"><a id="facility_<s:property value="id"/>" href="#" onclick="javascript: return addOperator( <s:property value="#conID"/>, <s:property value="id"/>);"
				class="add">Add</a></td>
		</tr>
	</s:iterator>
	<s:if test="searchResults.size() > 1 && !permissions.corporate">
		<tr id="showAllLink">
			<td colspan="3" class="right">
				<a href="#" onclick="showAllOperators(); return false;" class="add">Show All Operators</a>
			</td>
		</tr>
	</s:if>
	</tbody>
</table>

<s:include value="../actionMessages.jsp"/>

</s:else>
