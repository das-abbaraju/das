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
            <a href="${manage_reports_favorites_url}"><s:text name="ManageReports.ACTION.tab.favorites" /></a>
        </li>
        <li class="${methodName == 'ownedBy' ? 'active' : ''}">
            <a href="${manage_reports_owned_by_url}"><s:text name="ManageReports.ACTION.tab.ownedBy" /></a>
        </li>
        <li class="${methodName == 'sharedWith' ? 'active' : ''}">
            <a href="${manage_reports_shared_with_url}"><s:text name="ManageReports.ACTION.tab.sharedWith" /></a>
        </li>
        <li class="${methodName == 'search' ? 'active' : ''}">
            <a href="${manage_reports_search_url}"><s:text name="ManageReports.ACTION.tab.search" /></a>
        </li>
    </ul>
</div>