<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="project" var="operator_project_list_url"/>
<s:url action="project" method="create" var="operator_project_create_url"/>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Projects</s:param>
    <s:param name="actions">
        <a href="${operator_project_create_url}" class="btn btn-default"><i class="icon-plus-sign"></i> Project</a>
    </s:param>
</s:include>

<tw:form formName="operator_project_search" action="${operator_project_list_url}" class="search-query" role="form">
    <fieldset>
        <div class="search-wrapper col-md-4">
                <tw:input inputName="searchTerm" type="text" class="form-control" placeholder="Search Projects"
                          value="${searchForm.searchTerm}"/>
            <i class="icon-search"></i>
            <ul id="operator_project_search" class="search-results"></ul>
    </fieldset>
</tw:form>

<div class="table-responsive">
    <table class="table table-striped table-condensed table-hover">
        <thead>
        <tr>
            <s:if test="permissions.corporate">
                <th>Site</th>
            </s:if>
            <th>Project</th>
            <th>Location</th>
            <th>Start Date</th>
            <th>End Date</th>
        </tr>
        </thead>

        <tbody>

        <s:iterator value="projects" var="project">
            <s:url action="project" var="operator_project_show_url">
                <s:param name="id">${project.id}</s:param>
            </s:url>

            <tr>
                <s:if test="permissions.corporate">
                    <td>${project.site}</td>
                </s:if>
                <td>
                    <a href="${operator_project_show_url}">${project.name}</a>
                </td>
                <td>${project.location}</td>
                <td><s:date name="#project.startDate" format="yyyy-MM-dd"/></td>
                <td><s:date name="#project.endDate" format="yyyy-MM-dd"/></td>
            </tr>
        </s:iterator>
        </tbody>
    </table>
</div>