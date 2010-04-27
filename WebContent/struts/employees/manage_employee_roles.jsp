<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:include value="../actionMessages.jsp"/>

<div id="thinking_roles" class="right"></div>

<table class="report">
	<thead>
		<tr>
			<th colspan="2">Role</th>
		</tr>
	</thead>
	<s:iterator value="employee.employeeRoles">
		<tr>
			<td><s:property value="jobRole.name"/></td>
			<td><a href="#" onclick="removeJobRole('<s:property value="id"/>'); return false;" class="remove">Remove</a></td>
		</tr>
	</s:iterator>
	<tr>
		<td colspan="2">
			<s:select list="unusedJobRoles" onchange="addJobRole(this.value)" headerKey="" headerValue=" - Add New Role - " listKey="id" listValue="name"/>
		</td>
	</tr>
</table>