<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<fieldset class="form">
	<h2 class="formLegend">
		<s:text name="ManageEmployees.header.OQProjects" />
	</h2>
	<ol>
		<li>
			<table class="report" style="width: 500px;">
				<thead>
					<tr>
						<th>
							<s:text name="global.Operator" />
						</th>
						<th>
							<s:text name="ManageEmployees.label.Project" />
						</th>
						<th>
							<s:text name="ManageEmployees.label.Since" />
						</th>
						<th>
							<s:text name="button.Edit" />
						</th>
						<th>
							<s:text name="ManageEmployees.label.Tasks" />
						</th>
					</tr>
				</thead>
				<s:set name="oqSiteCount" value="0" />
				<s:include value="manage_employees_sites_oq_tasks.jsp" />
				<s:if test="#oqSiteCount == 0">
					<tr>
						<td colspan="5">
							<s:text name="ManageEmployees.message.NoAssignedProjects" />
						</td>
					</tr>
				</s:if>
			</table>
		</li>
		<s:if test="oqOperators.size > 0">
			<li>
				<s:select
					data-employee="${employee.id}"
					headerKey=""
					headerValue=" - %{getText('ManageEmployees.header.AssignProject')} - "
					id="oq_project_list"
					list="oqOperators"
					listKey="id"
					listValue="name" />
			</li>
		</s:if>
		<pics:permission perm="ManageProjects" type="Edit">
			<li>
				<a class="add" href="javascript:;" id="new_project_link">
					<s:text name="ManageEmployees.link.AddNewJobSite" />
				</a>
			</li>
		</pics:permission>
		<s:if test="employee.account.contractor">
			<li>
				<s:url action="ReportNewProjects" var="report_new_projects" />
				<a href="${report_new_projects}" class="add">
					<s:text name="ManageEmployees.link.FindNewProjects" />
				</a>
			</li>
		</s:if>
	</ol>
</fieldset>