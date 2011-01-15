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

<h4>Companies</h4>
<table class="report">
	<thead>
		<tr>
			<th></th>
			<th>Company Name</th>
			<th># of Employees</th>
		</tr>
	</thead>
	<tbody>
		<s:if test="siteCompanies.keySet().size > 0">
			<s:iterator value="siteCompanies.keySet()" var="a" status="stat">
				<tr>
					<td><s:property value="#stat.count" /></td>
					<td><s:property value="getCompanyLink(#a)" escape="false" /></td>
					<td class="right">
						<a href="ReportOQEmployees.action?filter.accountName=<s:property value="#a.id" />&filter.projects=<s:property value="siteID" />">
							<s:property value="siteCompanies.get(#a).size" />
						</a>
					</td>
				</tr>
			</s:iterator>
		</s:if>
		<s:else>
			<tr><td colspan="3">No companies associated with this project.</td></tr>
		</s:else>
		<s:if test="newContractors.size > 0">
			<tr>
				<td colspan="3">
					<s:select list="newContractors" headerKey="0" headerValue="- Add New Company -" listKey="id" listValue="name" onchange="addCompany(this.value, %{siteID});" />
				</td>
			</tr>
		</s:if>
	</tbody>
</table>
