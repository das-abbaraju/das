<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="project/site-assignment/{siteId}" var="contractor_site_assignments">
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

<div class="row">
    <ul class="nav nav-pills nav-stacked nav-assignment col-md-3">
        <li class="site-status">
            <a href="${contractor_site_assignments}">
                <span class="badge pull-right">33</span>
                Site Status
            </a>
        </li>
        <li class="nav-divider"></li>
        <s:set var="selected_role" value="%{id}"/>
        <s:iterator value="siteAssignmentModel.roleEmployee.keySet()" var="operator_job_role">
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
                    <span class="badge pull-right">${siteAssignmentModel.roleEmployee.get(operator_job_role)}</span>
                        ${operator_job_role.name}
                </a>
            </li>
        </s:iterator>
    </ul>

    <div class="table-responsive col-md-9">
        <table class="table table-striped table-condensed table-hover table-assignment">
            <thead>
                <tr>
                    <th class="text-center">Assign</th>
                    <th>Employee</th>
                    <th>Title</th>
                    <s:iterator value="contractorProjectAssignmentMatrix.skillNames" var="skill_name">
                        <th class="text-center">${skill_name}</th>
                    </s:iterator>
                </tr>
            </thead>

            <tbody>
                <s:iterator value="siteAssignmentModel.employeeSiteAssignmentModels" var="employee_site_assignment">
                    <tr class="assign-employee-container">
                        <td>
                            <s:url action="employee" var="employee_site_assignment_url">
                                <s:param name="id">
                                    ${employee_site_assignment.employeeId}
                                </s:param>
                            </s:url>
                            <a href="${employee_site_assignment_url}" class="disable-assignment">${employee_site_assignment.employeeName}</a>
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
</div>