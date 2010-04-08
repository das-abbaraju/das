<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<h3><s:property value="newSite.name" /> Tasks</h3>
<s:if test="getTasks(siteID).size() > 0">
	<table class="report">
		<thead>
			<tr>
				<th>Label</th>
				<th>Name</th>
				<th><span title="Click on the number to get the list of employees associated with this task."
					style="padding-right: 17px; margin-left: 2px; background: url('images/help.gif') no-repeat right center;"># Employees</span>
				</th>
				<s:if test="canEdit">
					<th>Remove</th>
				</s:if>
			</tr>
		</thead>
		<s:iterator value="getTasks(siteID)" id="siteTask">
			<tbody>
				<tr>
					<td><s:property value="#siteTask.task.label" /></td>
					<td><s:property value="#siteTask.task.name" /></td>
					<td class="center">
						<a onclick="getEmployees(<s:property value="#siteTask.id" />); return false;" href="#">
							<s:property value="getEmployeesByTask(#siteTask.id).size()" />
						</a>
					</td>
					<s:if test="canEdit">
						<td class="center"><a href="#" onclick="removeTask(<s:property value="siteID" />, <s:property value="#siteTask.id" />); return false;" class="remove"></a></td>
					</s:if>
				</tr>
			</tbody>
		</s:iterator>
	</table>
</s:if>
<s:else>
	No tasks associated with this site.<br />
</s:else>

<s:if test="getAddableTasks().size() > 0">
	<a id="addTaskLink" href="#" onclick="getNewSiteTasks(<s:property value="siteID" />); return false;" class="add">Add New Task</a>
</s:if>