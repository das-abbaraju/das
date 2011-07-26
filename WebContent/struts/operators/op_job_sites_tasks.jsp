<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<h3><s:property value="jobSite.name" /></h3>
<h4><s:text name="%{scope}.label.Tasks" /></h4>
<s:if test="jobSite.tasks.size > 0">
	<table class="report">
		<thead>
			<tr>
				<th><s:text name="JobTask.label" /></th>
				<th><s:text name="JobTask.name" /></th>
				<th><s:text name="JobSiteTask.controlSpan" /></th>
				<pics:permission perm="ManageProjects" type="Edit">
					<th><s:text name="button.Remove" /></th>
				</pics:permission>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="jobSite.tasks" id="siteTask">
				<tr>
					<td><s:property value="#siteTask.task.label" /></td>
					<td><s:property value="#siteTask.task.name" /></td>
					<td class="center"><s:text name="%{scope}.text.ControlSpan"><s:param value="%{#siteTask.controlSpan}" /></s:text></td>
					<pics:permission perm="ManageProjects" type="Edit">
						<td class="center"><a href="#" id="remove_<s:property value="jobSite.id" />_<s:property value="#siteTask.id" />" class="remove removeTask"></a></td>
					</pics:permission>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</s:if>
<s:else>
	<s:text name="%{scope}.message.NoTasksAssociated" /><br />
</s:else>

<a id="task_<s:property value="jobSite.id" />" href="#" class="add addTaskLink"><s:text name="%{scope}.link.AddNewTask" /></a>
<a id="closeTasks" href="#" class="remove"><s:text name="%{scope}.link.CloseTasks" /></a>

<h4><s:text name="global.Companies" /></h4>
<table class="report" id="companies_<s:property value="jobSite.id" />">
	<thead>
		<tr>
			<th></th>
			<th><s:text name="global.CompanyName" /></th>
			<th><s:text name="%{scope}.label.NumberOfEmployees" /></th>
		</tr>
	</thead>
	<tbody>
		<s:if test="siteCompanies.keySet().size > 0">
			<s:iterator value="siteCompanies.keySet()" var="a" status="stat">
				<tr>
					<td><s:property value="#stat.count" /></td>
					<td>
						<s:if test="isLinkable(#a)">
							<a href="ContractorView.action?id=<s:property value="#a.id" />"><s:property value="#a.name" /></a>
						</s:if>
						<s:else>
							<s:property value="#a.name" />
						</s:else>
					</td>
					<td class="right">
						<a href="ReportOQEmployees.action?filter.accountName=<s:property value="#a.id" />&filter.projects=<s:property value="siteID" />">
							<s:property value="siteCompanies.get(#a).size" />
						</a>
					</td>
				</tr>
			</s:iterator>
		</s:if>
		<s:else>
			<tr><td colspan="3"><s:text name="%{scope}.message.NoCompaniesAssociated" /></td></tr>
		</s:else>
		<s:if test="newContractors.size > 0">
			<tr>
				<td colspan="3">
					<s:select list="newContractors" id="addCompany" headerKey="0" headerValue="- %{getText(scope + '.select.AddNewCompany')} -" listKey="id" listValue="name" />
				</td>
			</tr>
		</s:if>
	</tbody>
</table>
