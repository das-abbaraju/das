<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<h3>Assign Tasks</h3>
<s:if test="getAddableTasks().size() > 0">
	<table class="report">
		<thead>
			<tr>
				<th>Label</th>
				<th>Name</th>
				<th>Span of Control</th>
				<pics:permission perm="ManageProjects" type="Edit">
					<th>Add</th>
				</pics:permission>
			</tr>
		</thead>
		<s:iterator value="getAddableTasks()" id="newTask">
			<tbody>
				<tr id="<s:property value="#newTask.id" />">
					<td><s:property value="#newTask.label" /></td>
					<td><s:property value="#newTask.name" /></td>
					<td class="center">1 of <input type="text" name="controlSpan" value="1" size="1" /></td>
					<pics:permission perm="ManageProjects" type="Edit">
						<td class="center"><a href="#"
							onclick="addTask(<s:property value="siteID" />, <s:property value="#newTask.id" />); return false;"
							class="add"></a></td>
					</pics:permission>
				</tr>
			</tbody>
		</s:iterator>
	</table>
</s:if>
<s:else>
	No (additional) tasks are available to add.
</s:else>

<a id="closeAssignTasks" href="#" onclick="$('#addSiteTasks:visible').slideUp(); $('#addTaskLink:hidden').fadeIn(); return false;" class="remove">Close Tasks</a>