<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="skill" var="contractor_skill_list_url" />
<s:url action="skill" method="create" var="contractor_skill_create_url" />

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title"><s:text name="CONTRACTOR.SKILL.LIST.SKILLS" /></s:param>
    <s:param name="actions">
        <a href="${contractor_skill_create_url}" class="btn btn-default"><i class="icon-plus-sign"></i> <s:text name="CONTRACTOR.SKILL.LIST.SKILL" /></a>
    </s:param>
</s:include>

<s:if test="!skillModels.isEmpty()">
    <tw:form formName="contractor_skill_search" action="${contractor_skill_list_url}" class="search-query" role="form">
        <fieldset>
            <div class="search-wrapper col-md-4">
                <s:set var="SEARCH_SKILLS"><s:text name="CONTRACTOR.SKILL.LIST.SEARCH_SKILLS" /></s:set>
                <tw:input inputName="searchTerm" type="text" class="form-control" placeholder="${SEARCH_SKILLS}" value="${searchForm.searchTerm}" />
                <i class="icon-search"></i>
                <ul id="contractor_skill_search" class="search-results"></ul>
        </fieldset>
    </tw:form>

    <div class="table-responsive">
        <table class="table table-striped table-condensed table-hover">
            <thead>
                <tr>
                    <s:set var="EMPLOYEES"><s:text name="CONTRACTOR.SKILL.LIST.EMPLOYEES" /></s:set>
                    <th class="text-center"><i class="icon-user icon-large" data-toggle="tooltip" data-placement="top" title="" data-original-title="${EMPLOYEES}"></i></th>
                    <th><s:text name="CONTRACTOR.SKILL.LIST.SKILL" /></th>
                    <th><s:text name="CONTRACTOR.SKILL.LIST.EMPLOYEE_GROUPS" /></th>
                </tr>
            </thead>

            <tbody>
                <s:iterator value="skillModels" var="contractorSkill">
                    <s:url action="skill" var="contractor_skill_show_url">
                        <s:param name="id">${contractorSkill.id}</s:param>
                    </s:url>

                    <s:if test="true">
                        <s:set var="is_skill_required"><i class="icon icon-ok"></i></s:set>
                    </s:if>
                    <s:else>
                        <s:set var="is_skill_required"><i class="icon icon-remove"></i></s:set>
                    </s:else>

                    <tr>

                            <td class="text-center">${contractorSkill.numberOfEmployees}</td>
                            <td><a href="${contractor_skill_show_url}">${contractorSkill.name}</a></td>
                            <td>
                                <s:if test="#contractorSkill.ruleType.required">
                                    <label class="label label-default"><s:text name="CONTRACTOR.SKILL.LIST.ALL_EMPLOYEES" /></label>
                                </s:if>
                                <s:else>
                                    <s:set var="contractor_groups" value="#contractorSkill.groups" />
                                    <s:include value="/struts/employee-guard/contractor/group/_list.jsp" />
                                </s:else>
                            </td>
                        </tr>
                    </s:iterator>
                </tbody>

        </table>
    </div>
</s:if>
<s:else>
    <section class="employee-guard-section">
        <h1>
            <i class="icon-certificate icon-large"></i>Skills
        </h1>
        <div class="content">
            <div class="row">
                <div class="col-md-8 col-md-offset-2">
                    <div class="alert alert-info">
                        <h4><s:text name="CONTRACTOR.SKILL.LIST.NO_SKILLS" /></h4>

                        <p><s:text name="CONTRACTOR.SKILL.LIST.SKILLS_INFO" /></p>

                        <p><s:text name="CONTRACTOR.SKILL.LIST.CREATE_FIRST_SKILL" /></p>

                        <p>
                            <a href="#"><i class="icon-question-sign"></i> <s:text name="CONTRACTOR.SKILL.LIST.LEARN_MORE" /></a>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </section>
</s:else>