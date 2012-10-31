<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:include value="../../actionMessages.jsp"/>

<ol>
	<s:if test="employee.employeeRoles.size > 0">
		<li>
			<table class="report">
				<thead>
					<tr>
						<th colspan="2">
							<s:text name="ManageEmployees.header.JobRoles" />
						</th>
					</tr>
				</thead>
				<s:iterator value="employee.employeeRoles" var="employee_role">
					<tr>
						<td>
							${employee_role.jobRole.name}
						</td>
						<td class="right">
							<a href="javascript:;" class="remove role" data-role="${employee_role.id}"></a>
						</td>
					</tr>
				</s:iterator>
			</table>
		</li>
	</s:if>
	<li>
		<s:if test="unusedJobRoles.size > 0">
			<s:select
				list="unusedJobRoles"
				id="available_employee_roles"
				headerKey=""
				headerValue=" - %{getText('ManageEmployees.header.AddNewRole')} - "
				listKey="id"
				listValue="name"
				data-employee="${employee.id.toString()}" />
		</s:if>
		<s:else>
			<h5>
				<s:text name="ManageEmployees.message.AssignedAllRoles" />
			</h5>
		</s:else>
	</li>
</ol>