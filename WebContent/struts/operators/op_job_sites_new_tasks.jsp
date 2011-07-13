<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<h3><s:text name="%{scope}.label.AssignTasks" /></h3>
<s:if test="addableTasks.size > 0">
	<table class="report">
		<thead>
			<tr>
				<th><s:text name="JobTask.label" /></th>
				<th><s:text name="JobTask.name" /></th>
				<th><s:text name="JobSiteTask.controlSpan" /></th>
				<pics:permission perm="ManageProjects" type="Edit">
					<th><s:text name="button.Add" /></th>
				</pics:permission>
			</tr>
		</thead>
		<s:iterator value="addableTasks" id="newTask">
			<tbody>
				<tr id="<s:property value="#newTask.id" />">
					<td><s:property value="#newTask.label" /></td>
					<td><s:property value="#newTask.name" /></td>
					<td class="center"><s:text name="%{scope}.text.ControlSpan"><s:param><input type="text" name="controlSpan" value="1" size="1" /></s:param></s:text></td>
					<pics:permission perm="ManageProjects" type="Edit">
						<td class="center">
							<a href="#" id="addTask_<s:property value="jobSite.id" />_<s:property value="#newTask.id" />" class="add"></a>
						</td>
					</pics:permission>
				</tr>
			</tbody>
		</s:iterator>
	</table>
</s:if>
<s:else>
	<s:text name="%{scope}.message.NoAdditionalTasksAvailable" />
</s:else>

<a id="closeAssignTasks" href="#" class="remove"><s:text name="%{scope}.link.CloseTasks" /></a>