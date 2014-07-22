<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="project/{projectId}/assignments/{id}" var="contractor_project_assignments">
    <s:param name="projectId">
        ${project.id}
    </s:param>
    <s:param name="id">
        ${project.accountId}
    </s:param>
</s:url>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title"><s:text name="CONTRACTOR.PROJECT.ASSIGNMENTS.ROLE.PAGE_TITLE" /> ${project.name}</s:param>
    <s:param name="breadcrumb_name">${project.name}</s:param>
    <s:param name="breadcrumb_id">${project.id}</s:param>
</s:include>

<div class="row">
    <ul class="nav nav-pills nav-stacked col-md-3">
        <li>
            <a href="${contractor_project_assignments}"><s:text name="CONTRACTOR.PROJECT.ASSIGNMENTS.ROLE.SECONDARY_NAV_MENU.PROJECT" /></a>
        </li>

        <s:set var="selected_role" value="%{id}"/>
        <s:iterator value="contractorProjectAssignmentMatrix.roles" var="contractor_project_role">
            <s:url action="project/{projectId}/assignments/{assignmentId}/role/{id}" var="contractor_project_role_url">
                <s:param name="projectId">
                    ${project.id}
                </s:param>
                <s:param name="assignmentId">
                    ${project.accountId}
                </s:param>
                <s:param name="id">
                    ${contractor_project_role.id}
                </s:param>
            </s:url>
            <li <s:if test="#selected_role == #contractor_project_role.id">class="active"</s:if>>
                <a href="${contractor_project_role_url}">${contractor_project_role.name}</a>
            </li>
        </s:iterator>
    </ul>

    <div class="table-responsive col-md-9">
        <table class="table table-striped table-condensed table-hover table-assignment">
            <thead>
                <tr>
                    <th class="text-center"><s:text name="CONTRACTOR.PROJECT.ASSIGNMENTS.ROLE.TABLE.ASSIGN" /></th>
                    <th><s:text name="CONTRACTOR.PROJECT.ASSIGNMENTS.ROLE.TABLE.EMPLOYEE" /></th>
                    <th><s:text name="CONTRACTOR.PROJECT.ASSIGNMENTS.ROLE.TABLE.TITLE" /></th>
                    <s:iterator value="contractorProjectAssignmentMatrix.skillNames" var="skill_name">
                        <th class="text-center">${skill_name}</th>
                    </s:iterator>
                </tr>
            </thead>

            <tbody>
                <s:iterator value="contractorProjectAssignmentMatrix.assignments" var="contractor_project_employee">

                    <s:if test="#contractor_project_employee.assigned">
                        <s:set var="employee_assigned_class">assigned</s:set>
                    </s:if>
                    <s:else>
                        <s:set var="employee_assigned_class" value="" />
                    </s:else>

                    <s:url action="project/{projectId}/assignments/{assignmentId}/role/{roleId}/employee/{id}" method="assign" var="assign_contractor">
                        <s:param name="projectId">
                            ${project.id}
                        </s:param>
                        <s:param name="assignmentId">
                            ${project.accountId}
                        </s:param>
                        <s:param name="id">
                            ${contractor_project_employee.employeeId}
                        </s:param>
                        <s:param name="roleId">
                            ${id}
                        </s:param>
                    </s:url>

                    <s:url action="project/{projectId}/assignments/{assignmentId}/role/{roleId}/employee/{id}" method="unassign" var="unassign_contractor">
                        <s:param name="projectId">
                            ${project.id}
                        </s:param>
                        <s:param name="assignmentId">
                            ${project.accountId}
                        </s:param>
                        <s:param name="id">
                            ${contractor_project_employee.employeeId}
                        </s:param>
                        <s:param name="roleId">
                            ${id}
                        </s:param>
                    </s:url>

                    <tr class="assign-employee-container ${employee_assigned_class}" data-assign-url="${assign_contractor}" data-unassign-url="${unassign_contractor}">
                        <td class="assign-employee text-center">
                            <i class="icon-map-marker icon-large"></i>
                        </td>
                        <td>
                            <s:url action="employee" var="contractor_project_employee_url">
                                <s:param name="id">
                                    ${contractor_project_employee.employeeId}
                                </s:param>
                            </s:url>
                            <a href="${contractor_project_employee_url}" class="disable-assignment">${contractor_project_employee.name}</a>
                        </td>
                        <td>${contractor_project_employee.title}</td>
                        <s:iterator value="#contractor_project_employee.skillStatuses" var="employee_skill_status">
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

                            <td class="${skill_status_class} skill-status-icon text-center">
                                <i class="${skill_icon} icon-large"></i>
                            </td>
                        </s:iterator>
                    </tr>
                </s:iterator>
            </tbody>
        </table>
    </div>
</div>