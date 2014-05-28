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
    <s:param name="title">Assignments: ${project.name}</s:param>
    <s:param name="breadcrumb_name">${project.name}</s:param>
    <s:param name="breadcrumb_id">${project.id}</s:param>
</s:include>

<div class="row">
    <ul class="nav nav-pills nav-stacked col-md-3">

        <li class="active">
            <a href="${contractor_project_assignments}">Project</a>
        </li>
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
            <li>
                <a href="${contractor_project_role_url}">${contractor_project_role.name}</a>
            </li>
        </s:iterator>
    </ul>

    <s:if test="!contractorProjectAssignmentMatrix.assignments.isEmpty()">
        <div class="table-responsive col-md-9">
            <table class="table table-striped table-condensed table-hover">
                <thead>
                    <tr>
                        <th>Employee</th>
                        <th>Title</th>
                        <s:iterator value="contractorProjectAssignmentMatrix.skillNames" var="skill_name">
                            <th class="text-center">${skill_name}</th>
                        </s:iterator>
                    </tr>
                </thead>

                <tbody>
                    <s:iterator value="contractorProjectAssignmentMatrix.assignments" var="contractor_project_employee">
                        <tr class="assign-employee-container">
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
        <div class="col-md-9">
            <section class="employee-guard-section">
                <h1>
                    <i class="icon-map-marker icon-large"></i>Project Status
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