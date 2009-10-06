<%@ taglib prefix="s" uri="/struts-tags"%>

<s:set name="conID" value="contractor.id"/>
<s:set name="conAcceptBid" value="contractor.acceptsBids.toString()"/>

<s:if test="searchResults.size == 0">
	<div id="alert">No Facilities Found</div>
</s:if>
<s:else>
<table class="report">
	<thead>
		<tr>
			<th>Operator Name</th>
			<th>Add Operator</th>
			<s:if test="#conAcceptBid.equals('true')">
				<th>Accepts Trial Accounts</th>
			</s:if>
		</tr>
	</thead>
	<tbody>
	<s:iterator value="searchResults">
		<tr id="results_<s:property value="id"/>">
			<td><s:property value="name"/></td>
			<td class="center"><a id="facility_<s:property value="id"/>" href="#" onclick="javascript: return addOperator( <s:property value="#conID"/>, <s:property value="id"/>);">Add</a></td>
			<s:if test="#conAcceptBid.equals('true')">
			<td class="center"><s:if test="acceptsBids">
				<span class="verified" style="font-size: 16px;"></span>
			</s:if></td>
			</s:if>
		</tr>
	</s:iterator>
	</tbody>
</table>
</s:else>
