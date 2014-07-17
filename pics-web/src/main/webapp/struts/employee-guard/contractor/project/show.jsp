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
    <s:param name="title"><s:text name="CONTRACTOR.PROJECT.SHOW.PROJECT_WITH_COLON" /> ${project.projectName}</s:param>
    <s:param name="actions">
        <a href="${contractor_assignments_url}" class="btn btn-primary"><i class="icon-map-marker icon-large"></i>
          <s:text name="CONTRACTOR.PROJECT.SHOW.MANAGE_PROJECT_ASSIGNMENTS" /></a>
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
                        <i class="icon-sitemap icon-large"></i> <s:text name="CONTRACTOR.PROJECT.SHOW.PROJECT" />
                    </div>
                </div>
            </h1>

            <div class="content">
                <dl class="employee-guard-information">
                    <dt class="col-md-3"><s:text name="CONTRACTOR.PROJECT.SHOW.SITE" /></dt>
                    <dd class="col-md-9">${project.siteName}</dd>
                    <dt class="col-md-3"><s:text name="CONTRACTOR.PROJECT.SHOW.NAME" /></dt>
                    <dd class="col-md-9">${project.projectName}</dd>
                    <dt class="col-md-3"><s:text name="CONTRACTOR.PROJECT.SHOW.LOCATION" /></dt>
                    <dd class="col-md-9">${project.location}</dd>
                    <dt class="col-md-3"><s:text name="CONTRACTOR.PROJECT.SHOW.START_DATE" /></dt>
                    <dd class="col-md-9"><s:date name="project.startDate" format="yyyy-MM-dd" /></dd>
                    <dt class="col-md-3"><s:text name="CONTRACTOR.PROJECT.SHOW.END_DATE" /></dt>
                    <dd class="col-md-9"><s:date name="project.endDate" format="yyyy-MM-dd" /></dd>
                    <dt class="col-md-3"><s:text name="CONTRACTOR.PROJECT.SHOW.PROJECT_SKILLS" /></dt>
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
                        <i class="icon-group icon-large"></i> <s:text name="CONTRACTOR.PROJECT.SHOW.PROJECT_JOB_ROLES" />
                    </div>
                </div>
            </h1>

            <div class="content">
                <dl class="employee-guard-information">
                    <s:if test="!project.jobRoles.isEmpty()">
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
                    </s:if>
                    <s:else>
                       <div class="col-md-8 col-md-offset-2">
                            <div class="alert alert-warning">
                                <h4><s:text name="CONTRACTOR.PROJECT.SHOW.NO_PROJECT_JOB_ROLES" /></h4>

                                <p><s:text name="CONTRACTOR.PROJECT.SHOW.CANNOT_ASSIGN_EMPLOYEES_MESSAGE" /></p>
                            </div>
                        </div>
                    </s:else>
                </dl>
            </div>
        </section>
    </div>
    <div class="col-md-4">
        <s:if test="(projectAssignmentBreakdown.expired <= 0) && (projectAssignmentBreakdown.expiring <= 0) && (projectAssignmentBreakdown.complete <= 0)">
            <section class="employee-guard-section-full">
                <h1>
                    <i class="icon-sitemap icon-large"></i> <s:text name="CONTRACTOR.PROJECT.SHOW.ASSIGNED_EMPLOYEES" />
                </h1>

                <div class="content">
                    <div class="alert alert-info">
                        <h4><s:text name="CONTRACTOR.PROJECT.SHOW.NO_ASSIGNMENTS" /></h4>

                        <p><s:text name="CONTRACTOR.PROJECT.SHOW.ASSIGN_EMPLOYEES_MESSAGE" /></p>

                        <p>
                            <a href="#"><i class="icon-question-sign"></i> <s:text name="CONTRACTOR.PROJECT.SHOW.LEARN_MORE" /></a>
                        </p>
                    </div>
                </div>
            </section>
        </s:if>
        <s:else>
            <section class="employee-guard-section">
                <h1>
                    <i class="icon-sitemap icon-large"></i> <s:text name="CONTRACTOR.PROJECT.SHOW.ASSIGNED_EMPLOYEES" />
                </h1>

                <div class="content">
                    <ul class="employee-guard-list-rollup">
                        <li class="danger">
                            <div class="row">
                                <div class="col-md-1 col-xs-1">
                                    <i class="icon-minus-sign-alt"></i>
                                </div>
                                <div class="col-md-9 col-xs-9"><s:text name="CONTRACTOR.PROJECT.SHOW.EXPIRED_OR_INCOMPLETE" /></div>
                                <div class="col-md-1 col-xs-1 assigned-count">${projectAssignmentBreakdown.expired}</div>
                            </div>
                        </li>
                        <li class="warning">
                            <div class="row">
                                <div class="col-md-1 col-xs-1">
                                    <i class="icon-warning-sign"></i>
                                </div>
                                <div class="col-md-9 col-xs-9"><s:text name="CONTRACTOR.PROJECT.SHOW.EXPIRING" /></div>
                                <div class="col-md-1 col-xs-1 assigned-count">${projectAssignmentBreakdown.expiring}</div>
                            </div>
                        </li>
                        <li class="success">
                            <div class="row">
                                <div class="col-md-1 col-xs-1">
                                    <i class="icon-ok-sign"></i>
                                </div>
                                <div class="col-md-9 col-xs-9"><s:text name="CONTRACTOR.PROJECT.SHOW.COMPLETE" /></div>
                                <div class="col-md-1 col-xs-1 assigned-count">${projectAssignmentBreakdown.complete}</div>
                            </div>
                        </li>
                    </ul>
                </div>
            </section>
        </s:else>
    </div>
</div>