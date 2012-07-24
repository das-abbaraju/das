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
				<s:include value="manage_employee_sites_oq_tasks.jsp" />
				<s:if test="#oqSiteCount == 0">
					<tr>
						<td colspan="5">
							<s:text name="ManageEmployees.message.NoAssignedProjects" />
						</td>
					</tr>
				</s:if>
			</table>
		</li>
		<li>
			<s:if test="oqOperators.size > 0">
				<s:select
					data-employee="${employee.id}"
					headerKey=""
					headerValue=" - %{getText('ManageEmployees.header.AssignProject')} - "
					id="oq_project_list"
					list="oqOperators"
					listKey="id"
					listValue="name" />
			</s:if>
		</li>
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
<pics:permission perm="ManageProjects" type="Edit">
	<div id="new_project_form">
		<fieldset class="form">
			<ol>
				<s:if test="permissions.admin && employee.account.contractor">
					<li>
						<label><s:text name="global.Operator" />:</label>
						<s:select
							list="allOqOperators"
							listKey="id"
							listValue="name"
							name="op.id"
							id="op"
							onchange="$('#opName').val($(this).text().trim())" />
						<s:hidden name="op.name" value="%{allOqOperators.get(0).name}" id="opName" />
					</li>
				</s:if>
				<s:else>
					<s:hidden name="op.id" value="%{employee.account.id}" />
					<s:hidden name="op.name" value="%{employee.account.name}" />
				</s:else>
				<li class="required">
					<label for="new_project_label">
						<s:text name="JobSite.label" />:
					</label>
					<s:textfield name="jobSite.label" maxlength="15" id="new_project_label" />
				</li>
				<li class="required">
					<label for="new_project_name">
						<s:text name="JobSite.name" />:
					</label>
					<s:textfield name="jobSite.name" maxlength="255" id="new_project_name" />
				</li>
				<li>
					<label for="new_project_start">
						<s:text name="JobSite.projectStart" />:
					</label>
					<s:textfield
						name="jobSite.projectStart"
						cssClass="datepicker"
						value="%{today}"
						id="new_project_start" />
				</li>
				<li>
					<label for="new_project_stop">
						<s:text name="JobSite.projectStop" />:
					</label>
					<s:textfield
						name="jobSite.projectStop"
						cssClass="datepicker"
						value="%{expirationDate}"
						id="new_project_stop" />
				</li>
			</ol>
		</fieldset>
	</div>
</pics:permission>