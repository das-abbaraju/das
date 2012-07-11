<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="com.picsauditing.report.access.ReportUtil" %>

<%-- Url --%>
<s:url action="ManageReports!viewFavoriteReports" var="manage_favorite_reports_url" />
<s:url action="ManageReports!viewMyReports" var="manage_my_reports_url" />
<s:url action="ManageReports!viewAllReports" var="manage_all_reports_url" />

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

<title>Manage Reports</title>

<s:include value="../actionMessages.jsp" />

<h1 class="title">Manage Reports</h1>
<p class="subtitle">
    Favorite, move, update, and search for new reports
</p>

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

<h2>${PageDescription}</h2>

<s:if test="viewingAllReports()">
    <div id="report_search">
        <i class="icon-search icon-large"></i>
        <input type="text" value="Search Reports"/>
    </div>
    <hr />
</s:if>

<div id="manage_report_list_container">
    <ul id="manage_report_list">
        <s:iterator value="userReports" var="user_report">
            <s:set name="report" value="#user_report.report" />
            <s:set name="report_id" value="#report.id" />

            <%-- Url --%>
            <s:url action="ManageReports" method="toggleFavorite" var="report_favorite_url">
                <s:param name="reportId">${report_id}</s:param>
            </s:url>

            <s:url action="ReportDynamic" var="report_url">
                <s:param name="report">${report_id}</s:param>
            </s:url>

            <s:url action="ManageReports" method="deleteReport" var="delete_report_url">
                <s:param name="report">${report_id}</s:param>
            </s:url>

            <s:url action="ManageReports" method="removeUserReport" var="remove_report_url">
                <s:param name="report">${report_id}</s:param>
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

                    <s:if test="user_report.report.createdBy.id != permissions.userId">
                        <span class="created-by">Created by ${report.createdBy.name}</span>
                    </s:if>
                </div>

                <s:if test="%{@com.picsauditing.model.ReportDynamicModel@canUserDelete(permissions.userId, report)}">
                    <a href="${delete_report_url}" class="delete">Delete</a>
                </s:if>
                <s:else>
                    <a href="${remove_report_url}" class="delete">Remove</a>
                </s:else>
            </li>
        </s:iterator>
    </ul>
</div>