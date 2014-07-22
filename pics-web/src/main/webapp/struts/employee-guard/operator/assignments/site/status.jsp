<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title"><s:text name="OPERATOR.SITE_ASSIGNMENT.PAGE.HEADER"/></s:param>
    <s:param name="breadcrumb_name"><s:text name="OPERATOR.SITE_ASSIGNMENT.BREADCRUMB_NAME"/></s:param>
    <s:param name="breadcrumb_id">${site.id}</s:param>
</s:include>

<div class="row">
    <div class="col-md-3">
        <ul class="nav nav-pills nav-stacked nav-assignment ">
            <li class="active site-status">
                <a href="#">
                    <span class="badge badge-info pull-right">${siteAssignmentModel.totalEmployeesAssignedToSite}</span>
                    <s:text name="OPERATOR.SITE_ASSIGNMENT.TABLE.SITE_STATUS"/>
                </a>
            </li>
            <li class="nav-divider"></li>
            <li>
                <span class="nav-title"><s:text name="OPERATOR.SITE_ASSIGNMENT.SIDE_NAV.JOB_ROLES"/></span>
            </li>
            <s:iterator value="siteAssignmentModel.roleEmployee.keySet()" var="operator_project_role">
                <s:url action="project/site-assignment/{siteId}/role/{id}" var="operator_project_role_url">
                    <s:param name="siteId">
                        ${site.id}
                    </s:param>
                    <s:param name="id">
                        ${operator_project_role.id}
                    </s:param>
                </s:url>
                <li>
                    <a href="${operator_project_role_url}">
                        <span class="badge badge-info pull-right">${siteAssignmentModel.roleEmployee.get(operator_project_role)}</span>
                        ${operator_project_role.name}
                    </a>
                </li>
            </s:iterator>
        </ul>
    </div>

    <s:if test="!siteAssignmentModel.employeeSiteAssignmentModels.isEmpty()">
        <div class="table-responsive col-md-9">
            <table class="table table-striped table-condensed table-hover table-assignment view-only">
                <thead>
                    <tr>
                        <th><s:text name="OPERATOR.SITE_ASSIGNMENT.TABLE.HEADER.COMPANY"/></th>
                        <th><s:text name="OPERATOR.SITE_ASSIGNMENT.TABLE.COLUMN.EMPLOYEE"/></th>
                        <th><s:text name="OPERATOR.SITE_ASSIGNMENT.TABLE.COLUMN.TITLE"/></th>
                        <th class="text-center"><s:text name="OPERATOR.SITE_ASSIGNMENT.TABLE.COLUMN.SITE_STATUS"/></th>
                    </tr>
                </thead>

                <tbody>
                    <s:iterator value="siteAssignmentModel.employeeSiteAssignmentModels" var="site_assignment_employee">
                        <tr>
                            <td>${site_assignment_employee.accountName}</td>
                            <td>
                                <s:url action="employees/{id}" var="employee_liveID">
                                    <s:param name="id">
                                        ${site_assignment_employee.employeeId}
                                    </s:param>
                                </s:url>
                                <a href="${employee_liveID}/sites/${site.id}">${site_assignment_employee.employeeName}</a>
                            </td>
                            <s:if test="#site_assignment_employee.status.expired" >
                                <s:set var="skill_icon">icon-minus-sign-alt</s:set>
                                <s:set var="skill_status_class">danger</s:set>
                            </s:if>
                            <s:elseif test="#site_assignment_employee.status.expiring" >
                                <s:set var="skill_icon">icon-warning-sign</s:set>
                                <s:set var="skill_status_class">warning</s:set>
                            </s:elseif>
                            <s:elseif test="#site_assignment_employee.status.pending" >
                                <s:set var="skill_icon">icon-ok-circle</s:set>
                                <s:set var="skill_status_class">success</s:set>
                            </s:elseif>
                            <s:else>
                                <s:set var="skill_icon">icon-ok-sign</s:set>
                                <s:set var="skill_status_class">success</s:set>
                            </s:else>

                            <td>${site_assignment_employee.employeeTitle}</td>
                            <td class="${skill_status_class} text-center"><i class="${skill_icon} icon-large"></i></td>
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
                    <i class="icon-map-marker icon-large"></i><s:text name="OPERATOR.SITE_ASSIGNMENT.TABLE.NO_ASSIGNMENTS_MSG.TITLE"/>
                </h1>
                <div class="content">
                    <div class="row">
                        <div class="col-md-8 col-md-offset-2">
                            <div class="alert alert-info">
                                <h4><s:text name="OPERATOR.SITE_ASSIGNMENT.TABLE.NO_ASSIGNMENTS_MSG.SUB_TITLE"/></h4>

                                <p><s:text name="OPERATOR.SITE_ASSIGNMENT.TABLE.NO_ASSIGNMENTS_MSG.MSG1"/></p>

                                <p>
                                    <a href="#"><i class="icon-question-sign"></i> <s:text name="OPERATOR.SITE_ASSIGNMENT.TABLE.NO_ASSIGNMENTS_MSG.MSG2"/></a>
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </div>
    </s:else>
</div>