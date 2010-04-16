<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:include value="../actionMessages.jsp"/>

<div id="thinking_roles" class="right"></div>

<h3>Job Roles</h3>
<table class="report">
	<thead>
		<tr>
			<th colspan="2">Role</th>
		</tr>
	</thead>
	<s:iterator value="employee.employeeRoles">
		<tr>
			<td><s:property value="jobRole.name"/></td>
			<td><a href="#" onclick="modJobRole('removeRole','<s:property value="employee.id"/>','<s:property value="jobRole.id"/>'); return false;" class="remove">Remove</a></td>
		</tr>
	</s:iterator>
	<s:iterator value="unusedJobRoles">
		<tr>
			<td><s:property value="name"/></td>
			<td><a href="#" onclick="modJobRole('addRole','<s:property value="employee.id"/>','<s:property value="id"/>'); return false;" class="add">Add</a></td>
		</tr>
	</s:iterator>					
</table>