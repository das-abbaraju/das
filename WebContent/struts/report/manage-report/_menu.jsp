<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Url --%>
<s:url action="ManageReports!favorites" var="manage_reports_favorites_url" />
<s:url action="ManageReports!ownedBy" var="manage_reports_owned_by_url" />
<s:url action="ManageReports!sharedWith" var="manage_reports_shared_with_url" />
<s:url action="ManageReports!search" var="manage_reports_search_url" />

<div id="manage_report_menu_container">
    <ul id="manage_report_menu" class="nav nav-tabs">
        <li class="${methodName == 'favorites' ? 'active' : ''}">
            <a href="${manage_reports_favorites_url}"><s:text name="ManageReports.report.Favorites" /></a>
        </li>
        <li class="${methodName == 'ownedBy' ? 'active' : ''}">
            <a href="${manage_reports_owned_by_url}">Owned by Me</a>
        </li>
        <li class="${methodName == 'sharedWith' ? 'active' : ''}">
            <a href="${manage_reports_shared_with_url}">Shared with Me</a>
        </li>
        <li class="${methodName == 'search' ? 'active' : ''}">
            <a href="${manage_reports_search_url}">Search for Reports</a>
        </li>
    </ul>
</div>