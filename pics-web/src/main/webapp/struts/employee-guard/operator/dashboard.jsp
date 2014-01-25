<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Url --%>
<s:url action="skill" var="operator_skill_url"/>
<s:url action="role" var="operator_role_url"/>
<s:url action="project" var="operator_project_url"/>
<s:url action="employee" method="editCorporateSection" var="corporate_skill_edit_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="dashboard" method="editRequiredSkillsSection" var="site_skill_edit_url">
    <s:param name="id">${permissions.accountId}</s:param>
</s:url>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">EmployeeGUARD</s:param>
    <s:param name="subtitle">Summary</s:param>
    <s:param name="breadcrumbs">false</s:param>
</s:include>

<div class="row">
    <ul class="nav nav-pills nav-stacked col-md-3">
        <li>
            <a href="${operator_project_url}"><i class="icon-sitemap"></i>Assignments and Projects</a>
        </li>
        <li>
            <a href="${operator_skill_url}"><i class="icon-certificate"></i>Skills</a>
        </li>
        <li>
            <a href="${operator_role_url}"><i class="icon-group"></i>Job Roles</a>
        </li>
        <%--         <li>
                    <a href="#"><i class="icon-ok"></i>Quick Manage</a>
                </li> --%>

    </ul>
    <div class="col-md-9">
        <s:if test="permissions.corporate">
            <section class="employee-guard-section edit-container" data-url="${site_skill_edit_url}">
                <h1>
                    <div class="row">
                        <div class="col-md-9 col-xs-9">Corporate Skills</div>
                        <s:if test="permissions.corporate">
                            <div class="col-md-3 col-xs-3 edit">
                                <i class="icon-edit icon-large edit-toggle"></i>
                            </div>
                        </s:if>
                    </div>
                </h1>

                <div class="content">
                    <s:if test="requiredSkills.isEmpty()">
                        <div class="col-md-9 col-md-offset-3 empty-message-text edit-display-values">
                            No Required Skills
                        </div>
                    </s:if>
                    <s:else>
                        <dl class="employee-guard-information edit-display-values">
                            <dt class="col-md-3">Required Skills</dt>
                            <dd class="col-md-9">
                                <ul class="employee-guard-list skills">
                                    <s:iterator value="requiredSkills" var="operator_skill">
                                        <s:url action="skill" var="operator_skill_show_url">
                                            <s:param name="id">${operator_skill.id}</s:param>
                                        </s:url>

                                        <li>
                                            <a href="${operator_skill_show_url}"><span class="label label-pics">${operator_skill.name}</span></a>
                                        </li>
                                    </s:iterator>
                                </ul>
                            </dd>
                        </dl>
                    </s:else>
                </div>
            </section>

            <section class="employee-guard-section edit-container">
                <h1>
                    <div class="row">
                        <div class="col-md-9 col-xs-9">Site Skills</div>
                    </div>
                </h1>

                <div class="content">
                    <dl class="employee-guard-information">
                        <s:iterator value="siteSkills.keySet()" var="operator_site">
                            <dt class="col-md-3">${operator_site.name}</dt>
                            <dd class="col-md-9">
                                <ul class="employee-guard-list skills">
                                    <s:iterator value="siteSkills.get(#operator_site)" var="operator_skill">
                                        <s:url var="operator_skill_show_url" action="skill">
                                            <s:param name="id">${operator_skill.id}</s:param>
                                        </s:url>

                                        <li>
                                            <a href="${operator_skill_show_url}"><span class="label label-pics">${operator_skill.name}</span></a>
                                        </li>
                                    </s:iterator>
                                </ul>
                            </dd>
                        </s:iterator>
                    </dl>
                </div>
            </section>
        </s:if>
        <s:else>
            <section class="employee-guard-section edit-container" data-url="${site_skill_edit_url}">
                <h1>
                    <div class="row">
                        <div class="col-md-9 col-xs-9">Site Skills</div>
                        <div class="col-md-3 col-xs-3 edit">
                            <i class="icon-edit icon-large edit-toggle"></i>
                        </div>
                    </div>
                </h1>

                <div class="content">
                    <s:if test="requiredSkills.isEmpty()">
                        <div class="col-md-9 col-md-offset-3 empty-message-text edit-display-values">
                            No Required Skills
                        </div>
                    </s:if>
                    <s:else>
                        <dl class="employee-guard-information edit-display-values">
                            <dt class="col-md-3">Required Skills</dt>
                            <dd class="col-md-9">
                                <ul class="employee-guard-list skills">
                                    <s:iterator value="requiredSkills" var="operator_skill">
                                        <s:url action="skill" var="operator_skill_show_url">
                                            <s:param name="id">${operator_skill.id}</s:param>
                                        </s:url>

                                        <li>
                                            <a href="${operator_skill_show_url}"><span class="label label-pics">${operator_skill.name}</span></a>
                                        </li>
                                    </s:iterator>
                                </ul>
                            </dd>
                        </dl>
                    </s:else>
                </div>
            </section>
        </s:else>
    </div>
</div>