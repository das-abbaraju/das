<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="siteTasks.size() > 0">
	<h3><span title="<s:property value="employeeSite.jobSite.name" />"><s:property value="employeeSite.jobSite.label" /></span> Tasks</h3>
	<table class="report">
		<thead>
			<tr>
				<th>Task</th>
				<th>Assign</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="siteTasks" id="task">
				<tr>
					<td class="center">
						<span title="<s:property value="#task.name" />">
							<s:property value="#task.label" />
						</span>
					</td>
					<td class="center">
						<input type="checkbox"<s:if test="assignedTask.get(employeeSite, #task)"> checked="checked"</s:if> onclick="assignTask(<s:property value="employeeSite.id" />, <s:property value="#task.id" />, this.value)" />
					</td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</s:if>













































<s:iterator value="employeeSiteTasks.keySet()" id="key">
	<h3><span title="<s:property value="#key.jobSite.name" />"><s:property value="#key.jobSite.label" /></span> Tasks</h3>
	<s:if test="employeeSiteTasks.get(#key).size() > 0">
		<table class="report">
			<thead>
				<tr>
					<th colspan="2">Tasks</th>
				</tr>
			</thead>
			<s:iterator value="employeeSiteTasks.get(#key)" id="task">
				<tr>
					<td class="center"><span title="<s:property value="#task.name" />"><s:property value="#task.label" /></span></td>
					<td class="center"><input type="checkbox" onclick=""<s:if test=""></s:if></td>
				</tr>
			</s:iterator>
		</table>
	</s:if>
	<s:else>
		<h5>No tasks have been assigned to this job site.</h5>
		<a href="ManageProjects.action?id=<s:property value="id > 0 ? id : employee.account.id" />">Click here to add tasks</a>.
	</s:else>
</s:iterator>