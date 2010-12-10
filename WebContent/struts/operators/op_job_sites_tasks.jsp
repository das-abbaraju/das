<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<h3><s:property value="newSite.name" /></h3>
<h4>Tasks</h4>
<s:if test="getTasks(siteID).size() > 0">
	<table class="report">
		<thead>
			<tr>
				<th>Label</th>
				<th>Name</th>
				<th>Span of Control</th>
				<pics:permission perm="ManageProjects" type="Edit">
					<th>Remove</th>
				</pics:permission>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="getTasks(siteID)" id="siteTask">
				<tr>
					<td><s:property value="#siteTask.task.label" /></td>
					<td><s:property value="#siteTask.task.name" /></td>
					<td class="center">1 of <s:property value="#siteTask.controlSpan" /></td>
					<pics:permission perm="ManageProjects" type="Edit">
						<td class="center"><a href="#" onclick="return removeTask(<s:property value="siteID" />, <s:property value="#siteTask.id" />);" class="remove"></a></td>
					</pics:permission>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</s:if>
<s:else>
	No tasks associated with this site.<br />
</s:else>

<a id="addTaskLink" href="#" onclick="getNewSiteTasks(<s:property value="siteID" />); $('#addSiteTasks:hidden').slideDown(); return false;" class="add">Add New Task</a>
<a id="closeTasks" href="#" onclick="$('#jobSiteTasks:visible').slideUp(); $('#addSiteTasks:visible').slideUp(); return false;" class="remove">Close Tasks</a>

<h4>Contractors</h4>
<s:if test="siteCompanies.keySet().size > 0">
	<table class="report">
		<thead>
			<tr>
				<th></th>
				<th>Company Name</th>
				<th># of Employees</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="siteCompanies.keySet()" var="a" status="stat">
				<tr>
					<td><s:property value="#stat.count" /></td>
					<td>
						<s:if test="#a.contractor && permissions.hasPermission('ContractorDetails')">
							<a href="ContractorView.action?id=<s:property value="#a.id" />"><s:property value="#a.name" /></a>
						</s:if>
						<s:elseif test="#a.operator && (permissions.hasPermission('ManageOperator') || permissions.accountId == #a.id">
							<a href="FacilitiesEdit.action?id=<s:property value="#a.id" />"><s:property value="#a.name" /></a>
						</s:elseif>
						<s:else>
							<s:property value="#a.name" />
						</s:else>
					</td>
					<td class="right"><s:property value="siteCompanies.get(#a).size" /></td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</s:if>
<s:else>
	No companies associated with this site.<br />
</s:else>