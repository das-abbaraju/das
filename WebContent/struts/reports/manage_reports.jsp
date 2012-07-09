<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="com.picsauditing.report.access.ReportUtil" %>

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

<div class="container-fluid">
    <h1 class="title">Manage Reports</h1>
    <p class="subtitle">
        Favorite, move, update, and search for new reports
    </p>

    <div class="manage_report_menu_container">
        <ul id="manage_report_menu" class="nav nav-pills">
            <li class="${menu_favorite_class}">
                <a href="ManageFavoriteReports.action">Favorites</a>
            </li>
            <li class="${menu_my_report_class}">
                <a href="ManageMyReports.action">My Reports</a>
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
    
    <div class="row-fluid">
        <div class="span8">
            <ul id="manage_report_list">
                <s:iterator value="userReports" var="report">
                    <li class="report">
                        <a href="ManageReports!toggleFavorite.action?reportId=${report.id}" class="favorite">
                            <s:set name="icon_class"></s:set>
                            
                            <s:if test="favorite">
                                <s:set name="icon_class">favorite</s:set>
                            </s:if>
    
                            <i class="icon-star icon-large ${icon_class}"></i>
                        </a>
    
                        <a class="name" href="ReportDynamic.action?report=<s:property value="report" />">
                            ${report.name}
                        </a>
    
                        <!-- TODO remove this hack after the MVP demo -->
                        <s:if test="report.id != 11 && report.id != 12">
                            <span class="created-by">Created by ${report.createdBy.name}</span>
                        </s:if>
                        <s:else>
                            <span class="created-by">Created by PICS</span>
                        </s:else>
    
                        <s:if test="report.id != 11 && report.id != 12">
                            <s:if test="%{@com.picsauditing.models.ReportDynamicModel@canUserDelete(permissions.userId, report)}">
                                <a class="delete" href="ManageReports!deleteReport.action?reportId=${report.id}">Delete</a>
                            </s:if>
                            <s:else>
                                <a class="delete" href="ManageReports!removeUserReport.action?reportId=${report.id}">Remove</a>
                            </s:else>
                        </s:if>
                    </li>
                </s:iterator>
            </ul>
        </div>
    </div>
</div>
