<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="project" var="contractor_project_list_url"/>
<s:url action="project" method="create" var="contractor_project_create_url"/>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Projects</s:param>
</s:include>

<tw:form formName="contractor_project_search" action="${contractor_project_list_url}" class="search-query" role="form">
    <fieldset>
        <div class="search-wrapper col-md-4">
            <tw:input inputName="searchTerm" type="text" class="form-control" placeholder="Search Projects" value="${searchForm.searchTerm}"/>
            <i class="icon-search"></i>
            <ul id="contractor_project_search" class="search-results"></ul>
    </fieldset>
</tw:form>

<div class="table-responsive">
    <table class="table table-striped table-condensed table-hover">
        <thead>
        <tr>
            <th>Site</th>
            <th>Project</th>
            <th>Location</th>
            <th>Start Date</th>
            <th>End Date</th>
        </tr>
        </thead>

        <tbody>
            <s:iterator value="projects" var="project">
                <s:url action="project" var="contractor_project_show_url">
                    <s:param name="id">${project.projectId}</s:param>
                </s:url>

                <tr>
                    <td>${project.siteName}</td>
                    <td>
                        <a href="${contractor_project_show_url}">${project.projectName}</a>
                    </td>
                    <td>${project.location}</td>
                    <td><s:date name="#project.startDate" format="yyyy-MM-dd" /></td>
                    <td><s:date name="#project.endDate" format="yyyy-MM-dd" /></td>
                </tr>
            </s:iterator>
        </tbody>
    </table>
</div>