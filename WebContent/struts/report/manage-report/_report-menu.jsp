<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Url --%>
<s:url action="ManageReports!favoritesList" var="manage_favorite_reports_url" />
<s:url action="ManageReports!myReportsList" var="manage_my_reports_url" />
<s:url action="ManageReports!searchList" var="manage_all_reports_url" />

<div id="manage_report_menu_container">
    <ul id="manage_report_menu" class="nav nav-tabs">
        <li class="${methodName == 'favoritesList' ? 'active' : ''}">
            <a href="${manage_favorite_reports_url}"><s:text name="ManageReports.report.Favorites" /></a>
        </li>
        <li class="${methodName == 'myReportsList' ? 'active' : ''}">
            <a href="${manage_my_reports_url}"><s:text name="ManageReports.report.MyReports" /></a>
        </li>
        <li class="${methodName == 'searchList' ? 'active' : ''}">
            <a href="${manage_all_reports_url}"><s:text name="ManageReports.report.Search" /></a>
        </li>
    </ul>
</div>