<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:include value="../actionMessages.jsp"/>

<table class="report" id="competenceMatrix">
	<thead>
		<tr>
			<th></th>
			<th><a href="#" onclick="sortTable('employee'); return false;">Employee</a></th>
			<th><a href="#" onclick="sortTable('jobRole,employee'); return false;">Job Roles</a></th>
			<s:if test="selectedOC != null && selectedOC.size() > 0">
				<s:iterator value="selectedOC">
					<th><s:property value="label" /></th>
				</s:iterator>
			</s:if>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="employees" id="employee" status="stat">
			<tr>
				<td class="id"><s:property value="#stat.count" /></td>
				<td class="employee"><a href="?conID=<s:property value="conID" />&employeeID=<s:property value="#employee.id" />"><s:property value="#employee.displayName" /></a></td>
				<td class="jobRole">
					<s:iterator value="employeeRolesByContractor.get(#employee.id)" id="role" status="stat">
						<s:property value="#role.jobRole.name" /><s:if test="#stat.count < employeeRolesByContractor.get(#employee.id).size()">, </s:if>
					</s:iterator>
				</td>
				<s:if test="selectedOC != null && selectedOC.size() > 0">
					<s:iterator value="selectedOC" id="competency">
						<s:set name="ec" id="ec" value="map.get(#employee, #competency)" />
						<td class="center"
								<s:if test="!#ec.skilled"> style="background-color: #FAA"</s:if>
								<s:if test="#ec.skilled"> style="background-color: #AFA"</s:if>>
							<s:if test="#ec != null">
								<input type="checkbox" <s:if test="#ec.skilled">checked="checked"</s:if> <s:if test="canEdit">onclick="saveChange(<s:property value="#ec.id" />, this); return false;"</s:if><s:else>disabled="disabled"</s:else> />
							</s:if>
						</td>
					</s:iterator>
				</s:if>
			</tr>
		</s:iterator>
	</tbody>
</table>