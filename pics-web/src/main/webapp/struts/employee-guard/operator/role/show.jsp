<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="role" method="delete" var="operator_role_delete_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="role" method="editRoleCurrentProjectsSection" var="operator_role_project_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="role" method="editNameSkillsSection
" var="operator_role_name_skills_url">
    <s:param name="id">${id}</s:param>
</s:url>

<%-- Page title --%>
<s:if test="permissions.accountId == role.accountId">
    <s:include value="/struts/employee-guard/_page-header.jsp">
        <s:param name="title"><s:text name="OPERATOR.JOB_ROLES.SHOW.PAGE.HEADER"/> ${role.name}</s:param>
        <s:param name="actions">
            <button type="button" data-toggle="modal" data-target="#deleteModal" class="btn btn-danger"><s:text name="CORPORATE.JOB_ROLES.SHOW.DELETE.BUTTON"/>
            </button>
        </s:param>
        <s:param name="breadcrumb_name">${role.name}</s:param>
    </s:include>
</s:if>
<s:else>
    <s:include value="/struts/employee-guard/_page-header.jsp">
        <s:param name="title"><s:text name="OPERATOR.JOB_ROLES.SHOW.PAGE.HEADER"/> ${role.name}</s:param>
        <s:param name="breadcrumb_name">${role.name}</s:param>
    </s:include>
</s:else>

<%-- Delete Confirmation --%>
<s:include value="/struts/employee-guard/_delete-confirmation.jsp">
    <s:param name="delete_url">${operator_role_delete_url}</s:param>
    <s:param name="modal_title"><s:text name="CORPORATE.JOB_ROLES.SHOW.DELETE.MODEL.TITLE"/></s:param>
    <s:param name="modal_message"><s:text name="CORPORATE.JOB_ROLES.SHOW.DELETE.MODEL.MSG"/></s:param>
</s:include>

<div class="row">
    <div class="col-md-8">
        <section class="employee-guard-section edit-container" data-url="${operator_role_name_skills_url}">
            <h1>
                <div class="row">
                    <div class="col-md-9 col-xs-9">
                        <i class="icon-group icon-large"></i> <s:text name="OPERATOR.JOB_ROLES.SHOW.ROLE.HEADER"/>
                    </div>
                    <s:if test="permissions.accountId == role.accountId">
                        <div class="col-md-3 col-xs-3 edit">
                            <i class="icon-edit icon-large edit-toggle"></i>
                        </div>
                    </s:if>
                </div>
            </h1>

            <div class="content">
                <dl class="employee-guard-information edit-display-values">
                    <dt class="col-md-3"><s:text name="OPERATOR.JOB_ROLES.SHOW.ROLE.NAME"/></dt>
                    <dd class="col-md-9">${role.name}</dd>
                    <dt class="col-md-3"><s:text name="OPERATOR.JOB_ROLES.SHOW.ROLE.REQUIRED_SKILLS"/></dt>
                    <dd class="col-md-9">
                        <s:set var="operator_skills" value="role.skills"/>
                        <s:include value="/struts/employee-guard/operator/skill/_list.jsp"/>
                    </dd>
                </dl>
            </div>
        </section>
    </div>
    <div class="col-md-4">
        <s:if test="permissions.corporate">
            <section class="employee-guard-section">
                <h1>
                    <div class="row">
                        <div class="col-md-9 col-xs-9">
                            <i class="icon-sitemap icon-large"></i> <s:text name="OPERATOR.JOB_ROLES.SHOW.CURRENT_PROJECTS.HEADER"/>
                        </div>
                    </div>
                </h1>
                <div class="content">
                    <dl class="employee-guard-information edit-display-values operator-role-projects">
                        <s:iterator value="siteProjects.keySet()" var="role_project_site">
                            <dt>${role_project_site.name}</dt>
                            <dd>
                                <s:set name="role_projects" value="siteProjects.get(#role_project_site)"/>
                                <s:include value="/struts/employee-guard/operator/role/_project-list.jsp"/>
                            </dd>
                        </s:iterator>
                    </dl>
                </div>
            </section>
        </s:if>
        <s:else>
            <section class="employee-guard-section">
                <h1>
                    <div class="row">
                        <div class="col-md-9 col-xs-9">
                            <i class="icon-sitemap icon-large"></i> <s:text name="OPERATOR.JOB_ROLES.SHOW.CURRENT_PROJECTS.HEADER"/>
                        </div>
                    </div>
                </h1>

                <div class="content">
                    <s:set name="role_projects" value="role.projects"/>
                    <s:include value="/struts/employee-guard/operator/role/_project-list.jsp"/>
                </div>
            </section>
        </s:else>
    </div>
</div>