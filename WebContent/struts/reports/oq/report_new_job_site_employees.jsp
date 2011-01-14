<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:include value="../../actionMessages.jsp" />

<h3><s:property value="jobSite.name" /> Employees</h3>
<table class="report">
	<thead>
		<tr>
			<td></td>
			<td>Name</td>
			<td>Remove</td>
		</tr>
	</thead>
	<tbody>
		<s:if test="employees.size > 0">
			<s:iterator value="employees" status="stat">
				<tr>
					<td class="right"><s:property value="#stat.count" /></td>
					<td><a href="EmployeeDetail.action?employee.id=<s:property value="id" />"><s:property value="lastName" />, <s:property value="firstName" /></a></td>
					<td class="center"><a href="#" onclick="return removeEmployee(<s:property value="jobSite.id" />, <s:property value="id" />);" class="remove"></a></td>
				</tr>
			</s:iterator>
		</s:if>
		<s:else>
			<tr><td colspan="3"><h5>There are no employees at this site.</h5></td></tr>
		</s:else>
		<s:if test="newEmployees.size > 0">
			<tr>
				<td colspan="3">
					<s:select list="newEmployees" listKey="id" listValue="%{lastName + ', ' + firstName}" headerKey="0" headerValue="- Add New Employee -" onchange="addEmployee(%{jobSiteID}, this.value);" />
				</td>
			</tr>
		</s:if>
		<s:else>
			<tr><td colspan="3"><h5>All employees have been assigned to this site.</h5></td></tr>
		</s:else>
	</tbody>
</table>
<s:if test="prevEmployees.size > 0">
	<h3>Previous Employees</h3>
	<table class="report">
		<thead>
			<tr>
				<td></td>
				<td>Name</td>
				<td>Expiration</td>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="prevEmployees" status="stat">
				<tr>
					<td class="right"><s:property value="#stat.count" /></td>
					<td><a href="EmployeeDetail.action?employee.id=<s:property value="employee.id" />"><s:property value="employee.lastName" />, <s:property value="employee.firstName" /></a></td>
					<td><s:date name="expirationDate" format="M/d/yyyy" /></td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</s:if>