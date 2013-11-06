<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="skill" var="contractor_skill_list_url" />
<s:url action="skill" method="create" var="contractor_skill_create_url" />

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Skills</s:param>
    <s:param name="actions">
        <a href="${contractor_skill_create_url}" class="btn btn-default"><i class="icon-plus-sign"></i> Skill</a>
    </s:param>
</s:include>

<%-- Pagination --%>
<s:include value="/struts/employee-guard/_pagination.jsp" />

<tw:form formName="contractor_skill_search" action="${contractor_skill_list_url}" class="search-query" role="form">
    <fieldset>
        <div class="search-wrapper col-md-4">
            <tw:input inputName="searchTerm" type="text" class="form-control" placeholder="Search Skills" value="${searchForm.searchTerm}" />
            <i class="icon-search"></i>
            <ul id="contractor_skill_search" class="search-results"></ul>
    </fieldset>
</tw:form>

<div class="table-responsive">
    <table class="table table-striped table-condensed table-hover">
        <thead>
            <tr>
                <th class="icon number-of-employees"><i class="icon-user icon-large" data-toggle="tooltip" data-placement="top" title="" data-original-title="Employees"></i></th>
                <th>Skill</th>
                <th>Employee Groups</th>
            </tr>
        </thead>

        <tbody>
            <s:iterator value="skills" var="contractorSkill">
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

                        <td class="number number-of-employees">${contractorSkill.employees.size()}</td>
                        <td><a href="${contractor_skill_show_url}">${contractorSkill.name}</a></td>
                        <td>
                            <s:if test="#contractorSkill.ruleType.required">
                                <label class="label label-default">All Employees</label>
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