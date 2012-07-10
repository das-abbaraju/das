<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="com.picsauditing.report.access.ReportUtil" %>

<%-- Url --%>
<s:url action="ManageFavoriteReports" var="manage_favorite_reports_url" />
<s:url action="ManageMyReports" var="manage_my_reports_url" />

<%-- Menu --%>
<s:set name="menu_favorite_class"></s:set>
<s:set name="menu_my_report_class"></s:set>
    
<s:if test="viewingFavoriteReports()">
    <s:set name="menu_favorite_class">active</s:set>
</s:if>
<s:elseif test="viewingAllReports()">
    <s:set name="menu_my_report_class">active</s:set>
</s:elseif>

<title>Manage Reports</title>

<s:include value="../actionMessages.jsp" />

<h1 class="title">Manage Reports</h1>
<p class="subtitle">
    Favorite, move, update, and search for new reports
</p>

<div id="manage_report_menu_container">
    <ul id="manage_report_menu" class="nav nav-pills">
        <li class="${menu_favorite_class}">
            <a href="${manage_favorite_reports_url}">Favorites</a>
        </li>
        <li class="${menu_my_report_class}">
            <a href="${manage_my_reports_url}">My Reports</a>
        </li>
        <li <s:if test="viewingFavoriteReports()">class="active"</s:if>>
            <a href="ManageFavoriteReports.action">Favorites</a>
        </li>
        <li <s:if test="viewingMyReports()">class="active"</s:if>>
            <a href="ManageMyReports.action">My Reports</a>
        </li>
        <li <s:if test="viewingAllReports()">class="active"</s:if>>
            <a href="ManageAllReports.action">Search</a>
        </li>
    </ul>
</div>

<h2>${PageDescription}</h2>

<s:if test="viewingAllReports()">
    <div id="report_search">
        <h3>Search</h3>
        <ul>
            <li>
                <input type="text" />
            </li>
            <li>
                <input class="btn" type="button" value="Search" />
            </li>
        </ul>
    </div>
</s:if>

<div id="manage_report_list_container">
    <ul id="manage_report_list">
        <s:iterator value="userReports" var="report">
            <%-- Url --%>
            <s:url action="ManageReports" method="toggleFavorite" var="report_favorite_url">
                <s:param name="reportId">${report.id}</s:param>
            </s:url>
            
            <s:url action="ReportDynamic" var="report_url">
                <s:param name="report">${report.id}</s:param>
            </s:url>
            
            <s:url action="ManageReports" method="deleteReport" var="delete_report_url">
                <s:param name="report">${report.id}</s:param>
            </s:url>
            
            <s:url action="ManageReports" method="removeUserReport" var="remove_report_url">
                <s:param name="report">${report.id}</s:param>
            </s:url>
            
            <%-- Icon --%>
            <s:set name="is_favorite_class" value="''" />
                    
            <s:if test="favorite">
                <s:set name="is_favorite_class">selected</s:set>
            </s:if>

            <li class="report">
                <a href="${report_favorite_url}" class="favorite">
                    <i class="icon-star icon-large ${is_favorite_class}"></i>
                </a>

                <div class="summary">
                    <a href="${report_url}" class="name">
                        ${report.name}
                    </a>
    
                    <!-- TODO remove this hack after the MVP demo -->
                    <s:if test="report.id != 11 && report.id != 12">
                        <span class="created-by">Created by ${report.createdBy.name}</span>
                    </s:if>
                    <s:else>
                        <span class="created-by">Created by PICS</span>
                    </s:else>
                </div>

                <s:if test="report.id != 11 && report.id != 12">
                    <s:if test="%{@com.picsauditing.models.ReportDynamicModel@canUserDelete(permissions.userId, report)}">
                        <a href="${delete_report_url}" class="delete">Delete</a>
                    </s:if>
                    <s:else>
                        <a href="${remove_report_url}" class="delete">Remove</a>
                    </s:else>
                </s:if>
            </li>
        </s:iterator>
    </ul>
</div>