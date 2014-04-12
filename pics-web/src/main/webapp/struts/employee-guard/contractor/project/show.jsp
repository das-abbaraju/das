<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="project/{projectId}/assignments/{id}" var="contractor_assignments_url">
    <s:param name="projectId">${project.projectId}</s:param>
    <s:param name="id">${project.siteId}</s:param>
</s:url>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Project: ${project.projectName}</s:param>
    <s:param name="actions">
        <a href="${contractor_assignments_url}" class="btn btn-warning"><i class="icon-map-marker icon-large"></i>
            Assign Employees</a>
    </s:param>
    <s:param name="breadcrumb_name">${project.projectName}</s:param>
    <s:param name="breadcrumb_id">${project.projectId}</s:param>
</s:include>

<div class="row">
    <div class="col-md-8">
        <section class="employee-guard-section">
            <h1>
                <div class="row">
                    <div class="col-md-9 col-xs-9">
                        <i class="icon-sitemap icon-large"></i> Project
                    </div>
                </div>
            </h1>

            <div class="content">
                <dl class="employee-guard-information">
                    <dt class="col-md-3">Site</dt>
                    <dd class="col-md-9">${project.siteName}</dd>
                    <dt class="col-md-3">Name</dt>
                    <dd class="col-md-9">${project.projectName}</dd>
                    <dt class="col-md-3">Location</dt>
                    <dd class="col-md-9">${project.location}</dd>
                    <dt class="col-md-3">Start Date</dt>
                    <dd class="col-md-9"><s:date name="project.startDate" format="yyyy-MM-dd" /></dd>
                    <dt class="col-md-3">End Date</dt>
                    <dd class="col-md-9"><s:date name="project.endDate" format="yyyy-MM-dd" /></dd>
                    <dt class="col-md-3">Project Skills</dt>
                    <dd class="col-md-9">
                        <s:set var="operator_skills" value="project.skills"/>
                        <s:include value="/struts/employee-guard/operator/skill/_list.jsp"/>
                    </dd>
                </dl>
            </div>
        </section>

        <section class="employee-guard-section">
            <h1>
                <div class="row">
                    <div class="col-md-9 col-xs-9">
                        <i class="icon-group icon-large"></i> Project Job Roles
                    </div>
                </div>
            </h1>

            <div class="content">
                <dl class="employee-guard-information">
                    <s:iterator value="project.jobRoles" var="project_role">
                        <s:url action="role" var="project_role_url">
                            <s:param name="id">
                                ${project_role.id}
                            </s:param>
                        </s:url>
                        <dt class="col-md-3">${project_role.name}</dt>
                        <dd class="col-md-9">
                            <s:set var="operator_skills" value="#project_role.skills"/>
                            <s:if test="!#project_role.skills.isEmpty()">
                                <ul class="employee-guard-list skills">
                                    <s:iterator value="#project_role.skills" var="operator_skill">
                                        <li>
                                            <span class="label label-default" data-toggle="tooltip" data-placement="right" title="" data-original-title="${operator_skill.description}" data-container="body">${operator_skill.name}</span>
                                        </li>
                                    </s:iterator>
                                </ul>
                            </s:if>
                        </dd>
                    </s:iterator>
                </dl>
            </div>
        </section>
    </div>
    <div class="col-md-4">
        <section class="employee-guard-section">
            <h1>
                <i class="icon-sitemap icon-large"></i> Assigned Employees
            </h1>

            <div class="content">
                <ul class="employee-guard-list-rollup">
                    <li class="danger">
                        <div class="row">
                            <div class="col-md-1 col-xs-1">
                                <i class="icon-minus-sign-alt"></i>
                            </div>
                            <div class="col-md-9 col-xs-9">Expired or Incomplete</div>
                            <div class="col-md-1 col-xs-1 assigned-count">${projectAssignmentBreakdown.expired}</div>
                        </div>
                    </li>
                    <li class="warning">
                        <div class="row">
                            <div class="col-md-1 col-xs-1">
                                <i class="icon-warning-sign"></i>
                            </div>
                            <div class="col-md-9 col-xs-9">Expiring</div>
                            <div class="col-md-1 col-xs-1 assigned-count">${projectAssignmentBreakdown.expiring}</div>
                        </div>
                    </li>
                    <li class="success">
                        <div class="row">
                            <div class="col-md-1 col-xs-1">
                                <i class="icon-ok-sign"></i>
                            </div>
                            <div class="col-md-9 col-xs-9">Complete</div>
                            <div class="col-md-1 col-xs-1 assigned-count">${projectAssignmentBreakdown.complete}</div>
                        </div>
                    </li>
                </ul>
            </div>
        </section>
    </div>
</div>