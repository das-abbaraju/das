<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<h3>Task <s:property value="siteTask.task.label" /> Employees</h3>
<s:if test="getEmployeesByTask(siteTaskID).size() > 0">
	<table class="report">
		<thead>
			<tr>
				<th></th>
				<th>Name</th>
				<th>Employer</th>
				<th>Qualified</th>
			</tr>
		</thead>
		<s:iterator value="getEmployeesByTask(siteTaskID)" id="qualification" status="stat">
			<tbody>
				<tr>
					<td><s:property value="#stat.count" /></td>
					<td><s:property value="employee.lastName" />, <s:property value="employee.firstName" /></td>
					<td>
						<s:if test="employee.account.type == 'Contractor'">
							<a href="ContractorView.action?id=<s:property value="employee.account.id" />">
								<s:property value="employee.account.name" /></a>
						</s:if>
						<s:else>
							<a href="FacilitiesEdit.action?id=<s:property value="employee.account.id" />">
								<s:property value="employee.account.name" /></a>
						</s:else>
					</td>
					<td class="center"><s:if test="qualified"><span class="verified"></span></s:if></td>
				</tr>
			</tbody>
		</s:iterator>
	</table>
</s:if>
<s:else>
	No employees associated with this task.
</s:else>