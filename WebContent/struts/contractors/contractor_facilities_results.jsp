<%@ taglib prefix="s" uri="/struts-tags"%>

<s:set name="conID" value="contractor.id"/>
<s:set name="con" value="contractor"/>

<s:if test="searchResults.size() == 0">
	<div class="alert">No Facilities Found</div>
</s:if>
<s:else>
	<s:if test="state == '' && operator.name == ''">
	<div id="help">
		Here are ten suggestions for other locations you might work at:
	</div>
	</s:if>
<table class="report">
	<thead>
		<tr>
			<th>Operator Name</th>
			<th>Add Operator</th>
		</tr>
	</thead>
	<tbody>
	<s:iterator value="searchResults" var="op">
		<tr id="results_<s:property value="id"/>">
			<td	class="account<s:property value="status" />">
				<s:if test="permissions.admin">
					<a href="FacilitiesEdit.action?id=<s:property value="id"/>"><s:property value="fullName" /></a>
				</s:if>
				<s:else><s:property value="fullName" /></s:else>
				<div class="operatorlocation"><s:property value="getShortAddress(permissions.getCountry())" /></div>
			</td>
			<td class="center"><a id="facility_<s:property value="id"/>" href="#" onclick="javascript: return addOperator( <s:property value="#conID"/>, <s:property value="id"/>);"
				class="add">Add</a></td>
		</tr>
	</s:iterator>
	<s:if test="searchResults.size() > 1 && !permissions.corporate">
		<tr id="showAllLink">
			<td colspan="3" class="right" style="padding:10px 0 10px 0;">
				<a href="#" onclick="showAllOperators(); return false;" class="picsbutton positive">Show ALL Operators</a>
			</td>
		</tr>
	</s:if>
	</tbody>
</table>

<s:include value="../actionMessages.jsp"/>

</s:else>
