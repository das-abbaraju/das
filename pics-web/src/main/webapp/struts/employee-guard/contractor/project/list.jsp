<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="project" var="contractor_project_list_url"/>
<s:url action="project" method="create" var="contractor_project_create_url"/>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Assignments and Projects</s:param>
</s:include>

<section class="employee-guard-section info-toolbar">
    <s:iterator value="siteAssignmentsAndProjects.keySet()" var="site_assignment">
        <h1>
            <div class="row">
                <div class="col-md-6 col-sm-5 col-xs-6">
                    ${site_assignment.site.name}
                </div>
                <div class="col-md-6 col-sm-7 col-xs-6 text-right">
                    <s:url action="project/site-assignment/{id}" var="contractor_site_assignment">
                        <s:param name="id">
                            ${site_assignment.site.id}
                        </s:param>
                    </s:url>
                    <a href="${contractor_site_assignment}" class="btn btn-warning btn-xs pull-right">
                        <i class="icon-map-marker icon-large"></i> Site Assignments
                    </a>
                    <ul class="list-inline hidden-xs pull-right">
                        <li class="success">
                            <i class="icon-ok-sign icon-large"></i>${site_assignment.completed}
                        </li>
                        <li class="warning">
                            <i class="icon-warning-sign icon-large"></i>${site_assignment.expiring}
                        </li>
                        <li class="danger">
                            <i class="icon-minus-sign-alt icon-large"></i>${site_assignment.expired}
                        </li>
                    </ul>
                </div>
            </div>
        </h1>

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
                        <th>Project</th>
                        <th>Location</th>
                        <th>Start Date</th>
                        <th>End Date</th>
                    </tr>
                </thead>
                <tbody>
                    <s:iterator value="siteAssignmentsAndProjects.get(#site_assignment)" var="project_statistic">
                        <s:url action="project" var="contractor_project_show_url">
                            <s:param name="id">${project_statistic.project.projectId}</s:param>
                        </s:url>

                        <tr>
                            <td class="success text-center">${project_statistic.assignments.complete}</td>
                            <td class="warning text-center">${project_statistic.assignments.expiring}</td>
                            <td class="danger text-center">${project_statistic.assignments.expired}</td>
                            <td>
                                <a href="${contractor_project_show_url}">${project_statistic.project.projectName}</a>
                            </td>
                            <td>${project_statistic.project.location}</td>
                            <td><s:date name="#project_statistic.project.startDate" format="yyyy-MM-dd" /></td>
                            <td><s:date name="#project_statistic.project.endDate" format="yyyy-MM-dd" /></td>
                        </tr>
                    </s:iterator>
                </tbody>
            </table>
        </div>
    </s:iterator>
</section>