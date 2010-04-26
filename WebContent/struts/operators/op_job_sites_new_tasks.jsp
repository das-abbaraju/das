<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<h3>Add Tasks to <s:property value="newSite.name" /></h3>
<s:if test="getAddableTasks().size() > 0">
	<table class="report">
		<thead>
			<tr>
				<th></th>
				<th>Label</th>
				<th>Name</th>
				<pics:permission perm="ManageJobSites" type="Edit">
					<th>Add</th>
				</pics:permission>
			</tr>
		</thead>
		<s:iterator value="getAddableTasks()" id="newTask" status="stat">
			<tbody>
				<tr>
					<td><s:property value="#stat.count" /></td>
					<td><s:property value="#newTask.label" /></td>
					<td><s:property value="#newTask.name" /></td>
					<pics:permission perm="ManageJobSites" type="Edit">
						<td class="center"><a href="#" onclick="addTask(<s:property value="siteID" />, <s:property value="#newTask.id" />); return false;" class="add"></a></td>
					</pics:permission>
				</tr>
			</tbody>
		</s:iterator>
	</table>
</s:if>
<s:else>
	No (additional) tasks are available for this account.
</s:else>