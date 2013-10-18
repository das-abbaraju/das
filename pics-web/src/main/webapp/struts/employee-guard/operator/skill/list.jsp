<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="skill" var="operator_skill_list_url" />
<s:url action="skill" method="create" var="operator_skill_create_url" />

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Skills</s:param>
    <s:param name="actions">
        <a href="${operator_skill_create_url}" class="btn btn-default"><i class="icon-plus-sign"></i> Skill</a>
    </s:param>
</s:include>

<%-- Pagination --%>
<s:include value="/struts/employee-guard/_pagination.jsp" />

<tw:form formName="operator_skill_search" action="${operator_skill_list_url}" class="search-query" role="form">
    <fieldset>
        <div class="search-wrapper col-md-4">
            <tw:input inputName="searchTerm" type="text" class="form-control" placeholder="Search by name" value="${searchForm.searchTerm}" />
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
                        <s:set name="operator_roles" value="#operatorSkill.groups"/>
                        <s:include value="/struts/employee-guard/operator/role/_list.jsp" />
                    </td>
                </tr>
            </s:iterator>
        </tbody>
    </table>
</div>