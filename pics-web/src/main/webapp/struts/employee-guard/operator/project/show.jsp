<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="project" method="delete" var="operator_project_delete_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="project" method="editProjectNameSkillsSection" var="operator_project_name_skills_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="project" method="editProjectJobRolesSection" var="operator_project_job_roles_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="project" method="editProjectCompaniesSection" var="operator_project_companies_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="project/{projectId}/assignments/{id}" var="operator_assignments_url">
    <s:param name="projectId">
        ${project.id}
    </s:param>
    <s:param name="id">
        ${project.accountId}
    </s:param>
</s:url>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Project: ${project.name}</s:param>
    <s:param name="actions">
        <button type="button" data-toggle="modal" data-target="#deleteModal" class="btn btn-danger">Delete Project
        </button>
    </s:param>
    <s:param name="breadcrumb_name">${project.name}</s:param>
</s:include>

<%-- Delete Confirmation --%>
<s:include value="/struts/employee-guard/_delete-confirmation.jsp">
    <s:param name="delete_url">${operator_project_delete_url}</s:param>
    <s:param name="modal_title">Delete Project</s:param>
    <s:param name="modal_message">Deleting will remove the project and its assigned job roles, assigned companies, and employee assignments.</s:param>
</s:include>

<%-- Remove Requested Companies Confirmation --%>
<s:include value="/struts/employee-guard/_remove-requested-company-confirmation.jsp">
    <s:param name="delete_url">#</s:param>
    <s:param name="modal_title">Confirm Removing Requested Company</s:param>
    <s:param name="modal_message">You are removing one or more requested companies. Removing a company also removes all of a company's employees assigned to the project.</s:param>
</s:include>

<div class="row">
    <div class="col-md-8">
        <section class="employee-guard-section edit-container" data-url="${operator_project_name_skills_url}">
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
                    <s:if test="permissions.corporate">
                        <dt class="col-md-3">Site</dt>
                        <dd class="col-md-9">${project.site}</dd>
                    </s:if>
                    <dt class="col-md-3">Name</dt>
                    <dd class="col-md-9">${project.name}</dd>
                    <dt class="col-md-3">Location</dt>
                    <dd class="col-md-9">${project.location}</dd>
                    <dt class="col-md-3">Start Date</dt>
                    <dd class="col-md-9"><s:date name="project.startDate" format="yyyy-MM-dd"/></dd>
                    <dt class="col-md-3">End Date</dt>
                    <dd class="col-md-9"><s:date name="project.endDate" format="yyyy-MM-dd"/></dd>

                    <dt class="col-md-3">Project Skills</dt>
                    <dd class="col-md-9">
                        <s:set var="operator_skills" value="project.skills"/>
                        <s:include value="/struts/employee-guard/operator/skill/_list.jsp"/>
                    </dd>
                </dl>
            </div>
        </section>

        <section class="employee-guard-section edit-container" data-url="${operator_project_job_roles_url}">
            <h1>
                <div class="row">
                    <div class="col-md-9 col-xs-9">
                        <i class="icon-group icon-large"></i> Project Job Roles
                    </div>
                    <div class="col-md-3 col-xs-3 edit">
                        <i class="icon-edit icon-large edit-toggle"></i>
                    </div>
                </div>
            </h1>

            <div class="content">
                <dl class="employee-guard-information edit-display-values">
                    <s:iterator value="project.roles" var="project_role">
                        <s:url action="role" var="project_role_url">
                            <s:param name="id">
                                ${project_role.role.id}
                            </s:param>
                        </s:url>
                        <dt class="col-md-3">
                            <a href="${project_role_url}">${project_role.role.name}</a>
                        </dt>
                        <dd class="col-md-9">
                            <s:set var="operator_skills" value="#project_role.role.skills"/>
                            <s:include value="/struts/employee-guard/operator/skill/_list.jsp"/>
                        </dd>
                    </s:iterator>
                </dl>
            </div>
        </section>

        <section class="employee-guard-section edit-container" data-url="${operator_project_companies_url}">
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
                <s:set name="operator_companies" value="projectSites"/>
                <s:include value="/struts/employee-guard/operator/project/_companies-list.jsp"/>
            </div>
        </section>
    </div>
    <div class="col-md-4">
        <section class="employee-guard-section" data-url="${operator_project_url}">
            <h1>
                <i class="icon-sitemap icon-large"></i> Assigned Employees
            </h1>

            <div class="content">
                <a href="${operator_assignments_url}" class="btn btn-primary btn-block"><i class="icon-table"></i>
                    Current Assignments</a>
            </div>
        </section>
    </div>
</div>