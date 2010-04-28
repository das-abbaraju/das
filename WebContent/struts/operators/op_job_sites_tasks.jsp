<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<h3><s:property value="newSite.name" /> Tasks</h3>
<s:if test="getTasks(siteID).size() > 0">
	<table class="report">
		<thead>
			<tr>
				<th>Label</th>
				<th>Name</th>
				<pics:permission perm="ManageJobSites" type="Edit">
					<th>Remove</th>
				</pics:permission>
			</tr>
		</thead>
		<s:iterator value="getTasks(siteID)" id="siteTask">
			<tbody>
				<tr>
					<td><s:property value="#siteTask.task.label" /></td>
					<td><s:property value="#siteTask.task.name" /></td>
					<pics:permission perm="ManageJobSites" type="Edit">
						<td class="center"><a href="#" onclick="return removeTask(<s:property value="siteID" />, <s:property value="#siteTask.id" />);" class="remove"></a></td>
					</pics:permission>
				</tr>
			</tbody>
		</s:iterator>
	</table>
</s:if>
<s:else>
	No tasks associated with this site.<br />
</s:else>

<a id="addTaskLink" href="#" onclick="getNewSiteTasks(<s:property value="siteID" />); return false;" class="add">Add New Task</a>
