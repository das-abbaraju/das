<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Assignments: ${site.name}</s:param>
    <s:param name="breadcrumb_name">${site.name}</s:param>
    <s:param name="breadcrumb_id">${site.id}</s:param>
</s:include>

<%-- Unassign Confirmation --%>
<s:include value="/struts/employee-guard/_unassign-employee-confirmation.jsp">
    <s:param name="modal_title">Unassign Employee from Site</s:param>
    <s:param name="modal_message">Unassigning this employee from the site will unassign them from all Job Roles and Projects that they may currently be assigned to.</s:param>
</s:include>

<div class="row">
    <div class="col-md-3">
        <ul class="nav nav-pills nav-stacked nav-assignment">
            <li class="active site-status">
                <a href="#">
                    <span class="badge badge-info pull-right">${siteAssignmentModel.totalEmployeesAssignedToSite}</span>
                    Site Status
                </a>
            </li>
            <li class="nav-divider"></li>
            <li>
                <span class="nav-title">Job Roles</span>
            </li>
            <s:iterator value="siteAssignmentModel.roleEmployee.keySet()" var="operator_job_role">
                <li>
                    <s:url var="operator_job_role_url" action="project/site-assignment/{siteId}/role/{id}">
                        <s:param name="siteId">
                            ${site.id}
                        </s:param>
                        <s:param name="id">
                            ${operator_job_role.id}
                        </s:param>
                    </s:url>
                    <a href="${operator_job_role_url}">
                        <span class="badge badge-info pull-right">${siteAssignmentModel.roleEmployee.get(operator_job_role)}</span>
                        ${operator_job_role.name}
                    </a>
                </li>
            </s:iterator>
        </ul>
    </div>

    <s:if test="!siteAssignmentModel.employeeSiteAssignmentModels.isEmpty()">
        <div class="table-responsive col-md-9">
            <table class="table table-striped table-condensed table-hover table-assignment">
                <thead>
                    <tr>
                        <th class="text-center">Assign</th>
                        <th>Employee</th>
                        <th>Title</th>
                        <th class="text-center">Site Status</th>
                    </tr>
                </thead>

                <tbody>
                    <s:iterator value="siteAssignmentModel.employeeSiteAssignmentModels" var="employee_site_assignment">
                        <s:url action="project/site-assignment/{siteId}/employee/{id}/unassign" var="employee_unassign_from_site">
                            <s:param name="siteId">
                                ${site.id}
                            </s:param>
                            <s:param name="id">
                                ${employee_site_assignment.employeeId}
                            </s:param>
                        </s:url>
                        <tr class="assign-employee-container assigned site-level" data-unassign-url="${employee_unassign_from_site}">
                            <td class="assign-employee text-center">
                                <i class="icon-map-marker icon-large"></i> ${employee_site_assignment.numberOfRolesAssigned}
                            </td>
                            <td>
                                <s:url action="employee" var="employee_site_assignment_url">
                                    <s:param name="id">
                                        ${employee_site_assignment.employeeId}
                                    </s:param>
                                </s:url>
                                <a href="${employee_site_assignment_url}">
                                    ${employee_site_assignment.employeeName}
                                </a>
                            </td>
                            <td>${employee_site_assignment.employeeTitle}</td>
                            <s:if test="#employee_site_assignment.status.expired">
                                <s:set var="skill_icon">icon-minus-sign-alt</s:set>
                                <s:set var="skill_status_class">danger</s:set>
                            </s:if>
                            <s:elseif test="#employee_site_assignment.status.expiring">
                                <s:set var="skill_icon">icon-warning-sign</s:set>
                                <s:set var="skill_status_class">warning</s:set>
                            </s:elseif>
                            <s:elseif test="#employee_site_assignment.status.pending">
                                <s:set var="skill_icon">icon-ok-circle</s:set>
                                <s:set var="skill_status_class">success</s:set>
                            </s:elseif>
                            <s:else>
                                <s:set var="skill_icon">icon-ok-sign</s:set>
                                <s:set var="skill_status_class">success</s:set>
                            </s:else>

                            <td class="${skill_status_class} text-center">
                                <i class="${skill_icon} icon-large"></i>
                            </td>
                        </tr>
                    </s:iterator>
                </tbody>
            </table>
        </div>
    </s:if>
    <s:else>
        <div class="col-md-9">
            <section class="employee-guard-section">
                <h1>
                    <i class="icon-map-marker icon-large"></i>Site Status
                </h1>
                <div class="content">
                    <div class="row">
                        <div class="col-md-8 col-md-offset-2">
                            <div class="alert alert-info">
                                <h4>No Employees Assigned</h4>

                                <p>Assign employees by first selecting a Job Role, and then click on the assign icon ( <i class='icon-map-marker'></i> ).  Once an employee is assigned, they'll see the assignment and the required skills that they'll need to complete.</p>

                                <p>
                                    <a href="#"><i class="icon-question-sign"></i> Learn more about Assigning Employees</a>
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </div>
    </s:else>
</div>
