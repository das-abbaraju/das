<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:include value="../actionMessages.jsp"/>

<div id="thinking_roles" class="right"></div>
<ol>
	<s:if test="employee.employeeRoles.size > 0">
		<li>
			<table class="report">
				<thead>
					<tr>
						<th colspan="2">Role</th>
					</tr>
				</thead>
				<s:iterator value="employee.employeeRoles">
					<tr>
						<td><s:property value="jobRole.name"/></td>
						<td class="right"><a href="#" onclick="return removeJobRole('<s:property value="id"/>');" class="remove"></a></td>
					</tr>
				</s:iterator>
			</table>
		</li>
	</s:if>
	<li>
		<s:select list="unusedJobRoles" onchange="addJobRole(this.value)" headerKey="" headerValue=" - Add New Role - " listKey="id" listValue="name"/>
	</li>
</ol>