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
						<th colspan="2"><s:text name="ManageEmployees.header.JobRoles" /></th>
					</tr>
				</thead>
				<s:iterator value="employee.employeeRoles" var="er">
					<tr>
						<td><s:property value="#er.jobRole.name" /></td>
						<td class="right"><a href="#" id="removeJobRole_<s:property value="#er.id" />" class="remove removeJobRole"></a></td>
					</tr>
				</s:iterator>
			</table>
		</li>
	</s:if>
	<li>
		<s:if test="unusedJobRoles.size > 0">
			<s:select list="unusedJobRoles" onchange="addJobRole(this.value)" headerKey="" 
				headerValue=" - %{getText('ManageEmployees.header.AddNewRole')} - " listKey="id" listValue="name" />
		</s:if>
		<s:else>
			<h5><s:text name="ManageEmployees.message.AssignedAllRoles" /></h5>
		</s:else>
	</li>
</ol>