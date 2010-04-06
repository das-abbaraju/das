<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:if test="getTasks(siteID).size() > 0">
	<h3><s:property value="newSite.name" /> Tasks</h3>
	<table class="report">
		<thead>
			<tr>
				<th></th>
				<th>Label</th>
				<th>Name</th>
				<s:if test="canEdit">
					<th>Remove</th>
				</s:if>
			</tr>
		</thead>
		<s:iterator value="getTasks(siteID)" id="siteTask" status="stat">
			<tbody>
				<tr>
					<td><s:property value="#stat.count" /></td>
					<td><s:property value="#siteTask.task.label" /></td>
					<td><s:property value="#siteTask.task.name" /></td>
					<s:if test="canEdit">
						<td class="center"><a href="#" onclick="return false;" class="remove"></a></td>
					</s:if>
				</tr>
			</tbody>
		</s:iterator>
	</table>
</s:if>