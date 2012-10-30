<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="com.picsauditing.report.access.ReportUtil" %>

<s:url action="ManageReports" method="myReportsList" var="alpha_sort_url">
    <s:param name="sort">${alphaSort}</s:param>
    <s:param name="direction">${alphaSortDirection}</s:param>
</s:url>

<s:url action="ManageReports" method="myReportsList" var="date_added_sort_url">
    <s:param name="sort">${dateAddedSort}</s:param>
    <s:param name="direction">${dateAddedSortDirection}</s:param>
</s:url>

<s:url action="ManageReports" method="myReportsList" var="last_viewed_sort_url">
    <s:param name="sort">${lastViewedSort}</s:param>
    <s:param name="direction">${lastViewedSortDirection}</s:param>
</s:url>
    
<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title"><s:text name="ManageReports.title" /></s:param>
    <s:param name="subtitle"><s:text name="ManageReports.subtitle" /></s:param>
</s:include>

<s:include value="/struts/report/manage-report/_report-menu.jsp" />

<h4 class="report-subtitle"><s:text name="ManageReports.myReports.subtitle" /></h4>

<s:if test="!reportPermissionUsers.isEmpty()">
    <div id="my_reports_filter" class="btn-group">
        <a href="${alpha_sort_url}" class="btn active"><s:text name="ManageReports.myReports.alphabetical" /></a>
        <a href="${date_added_sort_url}" class="btn"><s:text name="ManageReports.myReports.dateAdded" /></a>
        <a href="${last_viewed_sort_url}" class="btn"><s:text name="ManageReports.myReports.lastViewed" /></a>
    </div>
</s:if>

<div id="report_my_reports">
    <s:include value="/struts/_action-messages.jsp" />

    <s:include value="/struts/report/manage-report/_my-reports-list.jsp" />
</div>