<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="project/{projectId}/assignments/{id}" var="operator_project_assignments">
    <s:param name="projectId">
        ${project.id}
    </s:param>
    <s:param name="id">
        ${project.accountId}
    </s:param>
</s:url>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Assignments: ${project.name}</s:param>
    <s:param name="breadcrumb_name">${project.name}</s:param>
    <s:param name="breadcrumb_id">${project.id}</s:param>
</s:include>

<div class="row">
    <ul class="nav nav-pills nav-stacked nav-assignment col-md-3">
        <li class="site-status">
            <a href="${operator_project_assignments}">
                <span class="badge pull-right">33</span>
                Site Status
            </a>
        </li>
        <li class="nav-divider"></li>
        <s:set var="selected_role" value="%{id}"/>
        <s:iterator value="operatorProjectAssignmentMatrix.roles" var="operator_project_role">
            <s:if test="#selected_role == #operator_project_role.id">
                <s:set var="active_role">active</s:set>
            </s:if>
            <s:else>
                <s:set var="active_role" value="" />
            </s:else>

            <s:url action="project/{projectId}/assignments/{assignmentId}/role/{id}" var="operator_project_role_url">
                <s:param name="projectId">
                    ${project.id}
                </s:param>
                <s:param name="assignmentId">
                    ${project.accountId}
                </s:param>
                <s:param name="id">
                    ${operator_project_role.id}
                </s:param>
            </s:url>
            <li class="${active_role}">
                <a href="${operator_project_role_url}">
                    <span class="badge pull-right">11</span>
                    ${operator_project_role.name}
                </a>
            </li>
        </s:iterator>
    </ul>

    <div class="table-responsive col-md-9">
        <table class="table table-striped table-condensed table-hover table-assignment view-only">
            <thead>
                <tr>
                    <th>Company</th>
                    <th>Employee</th>
                    <s:iterator value="operatorProjectAssignmentMatrix.skillNames" var="skill_name">
                        <th class="text-center">${skill_name}</th>
                    </s:iterator>
                </tr>
            </thead>

            <tbody>
                <s:iterator value="operatorProjectAssignmentMatrix.assignments" var="operator_project_employee">
                    <tr class="assigned">
                        <td>${operator_project_employee.companyName}</td>
                        <td>${operator_project_employee.employeeName}</td>
                        <s:iterator value="#operator_project_employee.skillStatuses" var="employee_skill_status">
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

                            <td class="${skill_status_class} text-center">
                                <i class="${skill_icon} icon-large"></i>
                            </td>
                        </s:iterator>
                    </tr>
                </s:iterator>
            </tbody>
        </table>
    </div>
</div>