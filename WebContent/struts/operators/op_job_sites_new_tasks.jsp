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
				<pics:permission perm="ManageJobSites" type="Edit">
					<th>Add</th>
				</pics:permission>
			</tr>
		</thead>
		<s:iterator value="getAddableTasks()" id="newTask">
			<tbody>
				<tr>
					<td><s:property value="#newTask.label" /></td>
					<td><s:property value="#newTask.name" /></td>
					<td>1 of 1</td>
					<pics:permission perm="ManageJobSites" type="Edit">
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