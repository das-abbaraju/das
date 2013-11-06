<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="assignments" var="operator_project_assignments">
    <s:param name="id">
        ${project.id}
    </s:param>
    <s:param name="siteId">
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
    <ul class="nav nav-pills nav-stacked col-md-3">

        <li class="active">
            <a href="${operator_project_assignments}">Project</a>
        </li>
        <s:iterator value="operatorProjectAssignmentMatrix.roles" var="operator_project_role">
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
            <li>
                <a href="${operator_project_role_url}">${operator_project_role.name}</a>
            </li>
        </s:iterator>
    </ul>

    <div class="table-responsive col-md-9">
        <table id="employee_project" class="table table-striped table-condensed table-hover table-status">
            <thead>
            <tr>
                <th>Employee</th>
                <th>Title</th>
                <s:iterator value="operatorProjectAssignmentMatrix.skillNames" var="skill_name">
                    <th class="status-title">${skill_name}</th>
                </s:iterator>
            </tr>
            </thead>

            <tbody>
            <s:iterator value="operatorProjectAssignmentMatrix.assignments" var="operator_project_employee">
                <s:if test="#operator_project_employee.hasRoles()">
                    <tr class="assign-employee-container">
                        <td>
                            ${operator_project_employee.employeeName}
                        </td>
                        <td>${operator_project_employee.title}</td>
                        <s:iterator value="#operator_project_employee.skillStatuses" var="employee_skill_status">
                            <s:set var="skill_icon">icon-ok-sign</s:set>
                            <s:if test="#employee_skill_status.expired" >
                                <s:set var="skill_icon">icon-minus-sign-alt</s:set>
                            </s:if>
                            <s:elseif test="#employee_skill_status.expiring" >
                                <s:set var="skill_icon">icon-warning-sign</s:set>
                            </s:elseif>
                            <s:elseif test="#employee_skill_status.pending" >
                                <s:set var="skill_icon">icon-ok-circle</s:set>
                            </s:elseif>

                            <td class="status ${employee_skill_status.displayValue}"><i class="${skill_icon} icon-large"></i></td>
                        </s:iterator>
                    </tr>
                </s:if>
            </s:iterator>
            </tbody>
        </table>
    </div>
</div>