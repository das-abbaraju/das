<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Url --%>
<s:url action="ManageReports!favorites" var="manage_favorite_reports_url" />
<s:url action="ManageReports!myReports" var="manage_my_reports_url" />
<s:url action="ManageReports!search" var="manage_all_reports_url" />

<%-- Menu --%>
<s:set name="menu_favorite_reports_class" value="''" />
<s:set name="menu_my_reports_class" value="''" />
<s:set name="menu_all_reports_class" value="''" />

<s:if test="viewingFavoriteReports()">
    <s:set name="menu_favorite_reports_class">active</s:set>
</s:if>
<s:elseif test="viewingMyReports()">
    <s:set name="menu_my_reports_class">active</s:set>
</s:elseif>
<s:elseif test="viewingAllReports()">
    <s:set name="menu_all_reports_class">active</s:set>
</s:elseif>

<div id="manage_report_menu_container">
    <ul id="manage_report_menu" class="nav nav-pills">
        <li class="${menu_favorite_reports_class}">
            <a href="${manage_favorite_reports_url}">Favorites</a>
        </li>
        <li class="${menu_my_reports_class}">
            <a href="${manage_my_reports_url}">My Reports</a>
        </li>
        <li class="${menu_all_reports_class}">
            <a href="${manage_all_reports_url}">Search</a>
        </li>
    </ul>
</div>