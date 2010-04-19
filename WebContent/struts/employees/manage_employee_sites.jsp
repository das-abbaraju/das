<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:include value="../actionMessages.jsp"/>

<div id="thinking_sites" class="right"></div>

<h3>Assigned Sites</h3>
<table class="report"">
	<thead>
		<tr>
			<th colspan="2">Assigned Site</th>
		</tr>
	</thead>
	<s:iterator value="employee.employeeSites" id="site">
		<tr>
			<td><s:property value="#site.operator.name"/></td>
			<td><a href="#" onclick="modJobSite('removeSite','<s:property value="employee.id"/>','','<s:property value="#site.operator.id"/>'); return false;" class="remove">Remove</a></td>
		</tr>
	</s:iterator>
	<s:if test="operators.size > 0">
		<tr>
			<td>
				<s:select onchange=""
					list="operators"
					name="operator.id"
					listKey="id"
					listValue="name"
					id="operator"
				/>
			</td>
			<td><a href="#" onclick="modJobSite('addSite','<s:property value="employee.id"/>','',$('#operator').val()); return false;" class="add">Add</a></td>
		</tr>
	</s:if>
</table>
<s:if test="operators.size == 0">
	<h5>This employee as been assigned to all available sites.</h5>
</s:if>