<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="operator-project" method="delete" var="operator_project_delete_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="operator-project" method="editProjectSection" var="operator_project_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="operator-project" method="editJobRolesSection" var="operator_job_roles_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="operator-project" method="editCompaniesRequestedSection" var="operator_companies_requested_url">
    <s:param name="id">${id}</s:param>
</s:url>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Project: ${project.name}</s:param>
    <s:param name="actions">
		<button type="button" data-toggle="modal" data-target="#deleteModal" class="btn btn-danger">Delete Project</button>
    </s:param>
</s:include>

<%-- Delete Confirmation --%>
<s:include value="/struts/employee-guard/_delete-confirmation.jsp">
    <s:param name="delete_url">${operator_project_delete_url}</s:param>
    <s:param name="modal_title">Delete Project</s:param>
    <s:param name="modal_message">Deleting will remove the project and its assigned job roles, assigned companies, and employee assignments.</s:param>
</s:include>

<div class="row">
    <div class="col-md-6">
		<section class="employee-guard-section edit-container" data-url="${operator_project_url}">
		    <h1>
		        <div class="row">
		            <div class="col-md-9 col-xs-9">
		                <i class="icon-sitemap icon-large"></i> Project
		            </div>
		            <div class="col-md-3 col-xs-3 edit">
		                <i class="icon-edit icon-large edit-toggle"></i>
		            </div>
		        </div>
		    </h1>

		    <div class="content">
				<dl class="employee-guard-information edit-display-values">
					<dt class="col-md-3">Site</dt>
					<dd class="col-md-9">${project.site}</dd>
					<dt class="col-md-3">Name</dt>
					<dd class="col-md-9">${project.name}</dd>
					<dt class="col-md-3">Location</dt>
					<dd class="col-md-9">${project.location}</dd>
					<dt class="col-md-3">Start Date</dt>
					<dd class="col-md-9">${project.start_date}</dd>
					<dt class="col-md-3">End Date</dt>
					<dd class="col-md-9">${project.end_date}</dd>

					<dt class="col-md-3">Project Skills</dt>
					<dd class="col-md-9">
						<s:set var="contractor_skills" value="group.skills"/>
						<s:include value="/struts/employee-guard/contractor/skill/_list.jsp"/>
					</dd>
                </dl>
			</div>

			<%-- <s:include value="/struts/employee-guard/operator/project/_projects-form.jsp" /> --%>

		</section>
	</div>

	<div class="col-md-6">
		<section class="employee-guard-section-full edit-container" data-url="${contractor_employee_employment_url}">
		    <h1>
		        <div class="row">
		            <div class="col-md-9 col-xs-9">
		                <i class="icon-building icon-large"></i> Companies Requested for Project
		            </div>
		            <div class="col-md-3 col-xs-3 edit">
		                <i class="icon-edit icon-large edit-toggle"></i>
		            </div>
		        </div>
		    </h1>

		    <div class="content">
				<s:set name="contractor_employees" value="group.employees"/>
				<s:include value="/struts/employee-guard/contractor/employee/_list.jsp"/>
			</div>

			<%-- <s:include value="/struts/employee-guard/operator/project/_companies-requested-form.jsp" /> --%>

		</section>
	</div>
</div>
<div class="row">
	<div class="col-md-6">
		<section class="employee-guard-section edit-container" data-url="${operator_project_url}">
		    <h1>
		        <div class="row">
		            <div class="col-md-9 col-xs-9">
		                <i class="icon-group icon-large"></i> Job Roles
		            </div>
		            <div class="col-md-3 col-xs-3 edit">
		                <i class="icon-edit icon-large edit-toggle"></i>
		            </div>
		        </div>
		    </h1>

		    <div class="content">
				<dl class="employee-guard-information edit-display-values">
					<dt class="col-md-3">
						<a href="#">Front End Developer</a>
					</dt>
					<dd class="col-md-9">
						<s:set var="contractor_skills" value="group.skills"/>
						<s:include value="/struts/employee-guard/contractor/skill/_list.jsp"/>
					</dd>
					<dt class="col-md-3">
						<a href="#">Back End Developer</a>
					</dt>
					<dd class="col-md-9">
						<s:set var="contractor_skills" value="group.skills"/>
						<s:include value="/struts/employee-guard/contractor/skill/_list.jsp"/>
					</dd>
					<dt class="col-md-3">
						<a href="#">Designer</a>
					</dt>
					<dd class="col-md-9">
						<s:set var="contractor_skills" value="group.skills"/>
						<s:include value="/struts/employee-guard/contractor/skill/_list.jsp"/>
					</dd>
	            </dl>
			</div>

			<%-- <s:include value="/struts/employee-guard/operator/project/_job-roles-form.jsp" /> --%>

		</section>
	</div>
</div>