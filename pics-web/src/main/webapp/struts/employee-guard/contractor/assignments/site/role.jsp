<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="project/site-assignment/{id}" var="contractor_site_assignments">
    <s:param name="id">
        ${site.id}
    </s:param>
</s:url>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Assignments: ${site.name}</s:param>
    <s:param name="breadcrumb_name">${site.name}</s:param>
    <s:param name="breadcrumb_id">${site.id}</s:param>
</s:include>

<%-- Unassign Confirmation --%>
<s:include value="/struts/employee-guard/_unassign-employee-confirmation.jsp">
    <s:param name="modal_title">Unassign Employee from Job Role</s:param>
    <s:param name="modal_message">Unassigning this employee from this Job Role will also unassign them from this Job Role on any Projects that they may currently be assigned to.</s:param>
</s:include>

<div class="row">
    <div class="col-md-3">
        <ul class="nav nav-pills nav-stacked nav-assignment">
            <li class="site-status">
                <a href="${contractor_site_assignments}">
                    <span class="badge pull-right">${assignmentMatrix.totalNumberOfEmployeesAssignedToSite}</span>
                    Site Status
                </a>
            </li>
            <li class="nav-divider"></li>
            <s:set var="selected_role" value="%{id}"/>
            <s:iterator value="assignmentMatrix.roleEmployee.keySet()" var="operator_job_role">

                <s:if test="#selected_role == #operator_job_role.id">
                    <s:set var="active_role">active</s:set>
                </s:if>
                <s:else>
                    <s:set var="active_role" value="" />
                </s:else>

                <li class="${active_role}">
                    <s:url var="operator_job_role_url" action="project/site-assignment/{siteId}/role/{id}">
                        <s:param name="siteId">
                            ${site.id}
                        </s:param>
                        <s:param name="id">
                            ${operator_job_role.id}
                        </s:param>
                    </s:url>
                    <a href="${operator_job_role_url}">
                        <span class="badge pull-right">${assignmentMatrix.roleEmployee.get(operator_job_role)}</span>
                        ${operator_job_role.name}
                    </a>
                </li>
            </s:iterator>
        </ul>
    </div>

    <s:if test="!assignmentMatrix.assignments.isEmpty()">
        <div class="table-responsive col-md-9">
            <table class="table table-striped table-condensed table-hover table-assignment">
                <thead>
                    <tr>
                        <th class="text-center">Assign</th>
                        <th>Employee</th>
                        <th>Title</th>
                        <s:iterator value="assignmentMatrix.skillNames" var="skill_name">
                            <th class="text-center">${skill_name}</th>
                        </s:iterator>
                    </tr>
                </thead>

                <tbody>
                    <s:iterator value="assignmentMatrix.assignments" var="employee_site_assignment">
                        <s:if test="#employee_site_assignment.assigned">
                            <s:set var="employee_assigned_class">assigned</s:set>
                        </s:if>
                        <s:else>
                            <s:set var="employee_assigned_class" value="" />
                        </s:else>

                        <s:url action="project/site-assignment/{siteId}/role/{roleId}/employee/{id}/assign" var="assign_contractor">
                            <s:param name="siteId">
                                ${site.id}
                            </s:param>
                            <s:param name="roleId">
                                ${id}
                            </s:param>
                            <s:param name="id">
                                ${employee_site_assignment.employeeId}
                            </s:param>
                        </s:url>
                        <s:url action="project/site-assignment/{siteId}/role/{roleId}/employee/{id}/unassign" var="unassign_contractor">
                            <s:param name="siteId">
                                ${site.id}
                            </s:param>
                            <s:param name="roleId">
                                ${id}
                            </s:param>
                            <s:param name="id">
                                ${employee_site_assignment.employeeId}
                            </s:param>
                        </s:url>

                        <tr class="assign-employee-container ${employee_assigned_class}" data-assign-url="${assign_contractor}" data-unassign-url="${unassign_contractor}">
                            <td class="assign-employee text-center">
                                <a href="#"><i class="icon-map-marker icon-large"></i></a>
                            </td>

                            <td>
                                <s:url action="employee" var="employee_site_assignment_url">
                                    <s:param name="id">
                                        ${employee_site_assignment.employeeId}
                                    </s:param>
                                </s:url>
                                <a href="${employee_site_assignment_url}" class="disable-assignment">
                                    ${employee_site_assignment.name}
                                </a>
                            </td>
                            <td>${employee_site_assignment.title}</td>
                            <s:iterator value="#employee_site_assignment.skillStatuses" var="employee_skill_status">
                                <s:if test="#employee_skill_status.expired">
                                    <s:set var="skill_icon">icon-minus-sign-alt</s:set>
                                    <s:set var="skill_status_class">danger</s:set>
                                </s:if>
                                <s:elseif test="#employee_skill_status.expiring">
                                    <s:set var="skill_icon">icon-warning-sign</s:set>
                                    <s:set var="skill_status_class">warning</s:set>
                                </s:elseif>
                                <s:elseif test="#employee_skill_status.pending">
                                    <s:set var="skill_icon">icon-ok-circle</s:set>
                                    <s:set var="skill_status_class">success</s:set>
                                </s:elseif>
                                <s:else>
                                    <s:set var="skill_icon">icon-ok-sign</s:set>
                                    <s:set var="skill_status_class">success</s:set>
                                </s:else>

                                <s:if test="#employee_assigned_class != 'assigned'">
                                    <s:set var="skill_status_class" value="" />
                                </s:if>

                                <td class="${skill_status_class} text-center">
                                    <i class="${skill_icon} icon-large"></i>
                                </td>
                            </s:iterator>
                        </tr>
                    </s:iterator>
                </tbody>
            </table>
        </div>
    </s:if>
    <s:else>
        <div class="col-md-9 no-value text-center">
            No current assignments
        </div>
    </s:else>
</div>