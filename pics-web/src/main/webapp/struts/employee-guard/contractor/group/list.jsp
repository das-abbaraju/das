<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="employee-group" var="contractor_group_list_url"/>
<s:url action="employee-group" method="create" var="contractor_group_create_url"/>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Employee Groups</s:param>
    <s:param name="actions">
        <a href="${contractor_group_create_url}" class="btn btn-default"><i class="icon-plus-sign"></i> Employee
            Group</a>
    </s:param>
</s:include>

<%-- Pagination --%>
<s:include value="/struts/employee-guard/_pagination.jsp"/>

<tw:form formName="contractor_group_search" action="${contractor_group_list_url}" class="search-query" role="form">
    <fieldset>
        <div class="search-wrapper col-md-4">
            <tw:input inputName="searchTerm" type="text" class="form-control" placeholder="Search Employee Groups" value="${searchForm.searchTerm}"/>
            <i class="icon-search"></i>
            <ul id="contractor_group_search" class="search-results"></ul>
        </div>
    </fieldset>
</tw:form>
<div class="table-responsive">
    <table class="table table-striped table-condensed table-hover">
        <thead>
        <tr>
            <th class="icon number-of-employees">
                <i class="icon-user icon-large" data-toggle="tooltip" data-placement="top" title="" data-original-title="Employees"></i>
            </th>
            <th>Employee Group</th>
            <th>Required Skills</th>
        </tr>
        </thead>
        <tbody>
        <s:iterator value="groups" var="contractor_group">
            <s:url action="employee-group" var="contractor_group_show_url">
                <s:param name="id">${contractor_group.id}</s:param>
            </s:url>

            <tr>
                <td class="number number-of-employees">${contractor_group.employees == null ? 0 : contractor_group.employees.size()}</td>
                <td>
                    <a href="${contractor_group_show_url}">${contractor_group.name}</a>
                </td>
                <td>
                    <s:set name="contractor_skills" value="#contractor_group.skills"/>
                    <s:include value="/struts/employee-guard/contractor/skill/_list.jsp"/>
                </td>
            </tr>
        </s:iterator>
        </tbody>
    </table>
</div>