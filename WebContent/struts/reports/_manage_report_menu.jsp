<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Url --%>
<s:url action="ManageReports!favorites" var="manage_favorite_reports_url" />
<s:url action="ManageReports!myReports" var="manage_my_reports_url" />
<s:url action="ManageReports!search" var="manage_all_reports_url" />

<div id="manage_report_menu_container">
    <ul id="manage_report_menu" class="nav nav-pills">
        <li class="${methodName == 'favorites' ? 'active' : ''}">
            <a href="${manage_favorite_reports_url}"><s:text name="ManageReports.report.Favorites" /></a>
        </li>
        <li class="${methodName == 'myReports' ? 'active' : ''}">
            <a href="${manage_my_reports_url}"><s:text name="ManageReports.report.MyReports" /></a>
        </li>
        <li class="${methodName == 'search' ? 'active' : ''}">
            <a href="${manage_all_reports_url}"><s:text name="ManageReports.report.Search" /></a>
        </li>
    </ul>
</div>