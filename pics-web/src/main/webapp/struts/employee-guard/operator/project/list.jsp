<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="project" var="operator_project_list_url"/>
<s:url action="project" method="create" var="operator_project_create_url"/>
<s:url action="project/site-assignment" var="operator_assignments_list_url" />

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Assignments and Projects</s:param>
    <s:param name="actions">
        <a href="${operator_project_create_url}" class="btn btn-default"><i class="icon-plus-sign"></i> Project</a>
    </s:param>
</s:include>

<section class="employee-guard-section operator-site-assignments">
    <h1>Site Assignments</h1>
    <div class="content">
        <div class="row">
            <div class="col-md-2 col-md-offset-1">
                <i class="icon-building icon-large"></i><a href="${operator_assignments_list_url}">17</a>
            </div>
            <div class="col-md-2">
                <i class="icon-user icon-large"></i><a href="#">852</a>
            </div>
            <div class="col-md-2 success">
                <i class="icon-ok-sign icon-large"></i>714
            </div>
            <div class="col-md-2 warning">
                <i class="icon-warning-sign icon-large"></i>24
            </div>
            <div class="col-md-2 danger">
                <i class="icon-minus-sign-alt icon-large"></i>114
            </div>
        </div>
    </div>
</section>

<tw:form formName="operator_project_search" action="${operator_project_list_url}" class="search-query" role="form">
    <fieldset>
        <div class="search-wrapper col-md-4">
            <tw:input inputName="searchTerm" type="text" class="form-control" placeholder="Search Projects" value="${searchForm.searchTerm}"/>
            <i class="icon-search"></i>
            <ul id="operator_project_search" class="search-results"></ul>
    </fieldset>
</tw:form>

<div class="table-responsive">
    <table class="table table-striped table-condensed table-hover">
        <thead>
            <tr>
                <th class="success text-center">
                    <i class="icon-ok-sign icon-large" data-toggle="tooltip" data-placement="top" title="" data-original-title="Expiring"></i>
                </th>
                <th class="warning text-center">
                    <i class="icon-warning-sign icon-large" data-toggle="tooltip" data-placement="top" title="" data-original-title="Expiring"></i>
                </th>
                <th class="danger text-center">
                    <i class="icon-minus-sign-alt icon-large" data-toggle="tooltip" data-placement="top" title="" data-original-title="Expired or Incomplete"></i>
                </th>
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
                    <td class="success text-center">3</td>
                    <td class="warning text-center">1</td>
                    <td class="danger text-center">2</td>
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