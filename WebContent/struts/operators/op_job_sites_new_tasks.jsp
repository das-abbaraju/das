<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<h3>Add Tasks to <s:property value="newSite.name" /></h3>
<s:if test="getAddableTasks().size() > 0">
	<table class="report">
		<thead>
			<tr>
				<th>Label</th>
				<th>Name</th>
				<s:if test="canEdit">
					<th>Add</th>
				</s:if>
			</tr>
		</thead>
		<s:iterator value="getAddableTasks()" id="newTask">
			<tbody>
				<tr>
					<td><s:property value="#newTask.label" /></td>
					<td><s:property value="#newTask.name" /></td>
					<s:if test="canEdit">
						<td class="center"><a href="#" onclick="addTask(<s:property value="siteID" />, <s:property value="#newTask.id" />); return false;" class="add"></a></td>
					</s:if>
				</tr>
			</tbody>
		</s:iterator>
	</table>
</s:if>
<s:else>
	No (additional) tasks are available for this account.
</s:else>