<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="com.picsauditing.report.access.ReportUtil" %>

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title"><s:text name="ManageReports.title" /></s:param>
    <s:param name="subtitle"><s:text name="ManageReports.subtitle" /></s:param>
</s:include>

<s:include value="/struts/report/_report-list-menu.jsp" />

<h3><s:text name="ManageReports.myReports.subtitle" /></h3>

<s:url action="ManageReports" method="myReportsList" var="alpha_sort_url">
    <s:param name="sort">${alphaSort}</s:param>
    <s:param name="direction">${alphaSortDirection}</s:param>
</s:url>

<s:url action="ManageReports" method="myReportsList" var="date_added_sort_url">
    <s:param name="sort">${dateAddedSort}</s:param>
    <s:param name="direction">${dateAddedSortDirection}</s:param>
</s:url>

<s:url action="ManageReports" method="myReportsList" var="last_opened_sort_url">
    <s:param name="sort">${lastOpenedSort}</s:param>
    <s:param name="direction">${lastOpenedSortDirection}</s:param>
</s:url>

<div id="my_reports_filter">
    <div class="btn-group">
        <a href="${alpha_sort_url}" class="btn"><s:text name="ManageReports.myReports.alphabetical" /></a>
        <a href="${date_added_sort_url}" class="btn"><s:text name="ManageReports.myReports.dateAdded" /></a>
        <a href="${last_opened_sort_url}" class="btn"><s:text name="ManageReports.myReports.lastOpened" /></a>
    </div>
</div>

<div id="report_my_reports">
    <s:include value="/struts/report/_list-my-reports.jsp" />
</div>