<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<h3><s:property value="newSite.name" /> Tasks</h3>
<s:if test="getTasks(siteID).size() > 0">
	<table class="report">
		<thead>
			<tr>
				<th>Label</th>
				<th>Name</th>
				<th># Employees</th>
			</tr>
		</thead>
		<s:iterator value="getTasks(siteID)" id="siteTask">
			<tbody>
				<tr>
					<td><s:property value="#siteTask.task.label" /></td>
					<td>
						<a onclick="getEmployees(<s:property value="#siteTask.id" />); return false;"
							href="#"><s:property value="#siteTask.task.name" /></a>
					</td>
					<td class="right"><s:property value="getEmployeesByTask(#siteTask.id).size()" /></td>
				</tr>
			</tbody>
		</s:iterator>
	</table>
</s:if>
<s:else>
	No tasks available for this site.
</s:else>