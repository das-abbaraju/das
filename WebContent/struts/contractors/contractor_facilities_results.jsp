<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<th>Flag</th>
		<th>Operator Name</th>
		<th>Add Operator</th>
	</thead>
	<tbody>
	<s:set name="conId" value="contractor.id"/>
<s:sort source="searchResults" comparator="opComparator">
<s:iterator>
	<s:set name="conOp" value="opMap.get(id)"/>
	
	<tr id="results_<s:property value="id"/>">
	
		<td>
			<s:if test="(permissions.picsEmployee == true) || (#attr.conOp != null)">
				<s:set name="theFlag" value="getContractor().flags.get(top)"/>			
				<s:if test="#attr.theFlag != null">
					<s:a href="%{flagUrl}"><img src="images/icon_<s:property value="#attr.theFlag.flagColor.name().toLowerCase()"/>Flag.gif" width="12" height="15"></s:a>
				</s:if>
			</s:if>
		</td>
		<td><s:property value="name"/></td>
		<td><a href="#" onclick="javascript: return addOperator( <s:property value="#attr.conId"/>, <s:property value="id"/> );">Add</a></td>
	</tr>
</s:iterator>
</s:sort>
	</tbody>
</table>