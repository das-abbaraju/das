<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="skill" var="operator_skill_list_url"/>
<s:url action="skill" method="create" var="operator_skill_create_url"/>
<s:url action="skills/site/{id}" method="editCorporateSection" var="corporate_skill_edit_url">
    <s:param name="id">${permissions.accountId}</s:param>
</s:url>
<s:url action="skills/site/{id}" method="editRequiredSkillsSection" var="site_skill_edit_url">
    <s:param name="id">${permissions.accountId}</s:param>
</s:url>

<%-- Page title --%>
<s:if test="permissions.corporate">
    <s:include value="/struts/employee-guard/_page-header.jsp">
        <s:param name="title">Skills</s:param>
        <s:param name="actions">
            <a href="${operator_skill_create_url}" class="btn btn-default"><i class="icon-plus-sign"></i> Skill</a>
        </s:param>
    </s:include>
</s:if>
<s:else>
    <s:include value="/struts/employee-guard/_page-header.jsp">
        <s:param name="title">Skills</s:param>
    </s:include>
</s:else>

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
                <div class="col-md-9 col-md-offset-3 no-value edit-display-values">
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
                <div class="col-md-9 col-md-offset-3 no-value edit-display-values">
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

<%-- Pagination --%>
<s:include value="/struts/employee-guard/_pagination.jsp"/>

<tw:form formName="operator_skill_search" action="${operator_skill_list_url}" class="search-query" role="form">
    <fieldset>
        <div class="search-wrapper col-md-4">
            <tw:input inputName="searchTerm" type="text" class="form-control" placeholder="Search Skills"
                      value="${searchForm.searchTerm}"/>
            <i class="icon-search"></i>
            <ul id="operator-skill-search" class="search-results"></ul>
        </div>
    </fieldset>
</tw:form>

<div class="table-responsive">
    <table class="table table-striped table-condensed table-hover">
        <thead>
        <tr>
            <th class="col-md-5">Skill</th>
            <th class="col-md-7">Job Roles</th>
        </tr>
        </thead>

        <tbody>
        <s:iterator value="skills" var="operatorSkill">
            <s:url action="skill" var="operator_skill_show_url">
                <s:param name="id">${operatorSkill.id}</s:param>
            </s:url>

            <tr>
                <td><a href="${operator_skill_show_url}">${operatorSkill.name}</a></td>
                <td>
                    <s:if test="#operatorSkill.ruleType.required">
                        <label class="label label-default">All Employees</label>
                    </s:if>
                    <s:else>
                        <s:set name="operator_roles" value="#operatorSkill.groups"/>
                        <s:include value="/struts/employee-guard/operator/role/_list.jsp"/>
                    </s:else>
                </td>
            </tr>
        </s:iterator>
        </tbody>
    </table>
</div>