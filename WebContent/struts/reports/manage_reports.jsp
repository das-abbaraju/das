<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="com.picsauditing.report.access.ReportUtil" %>

<title>Manage Reports</title>

<s:include value="../actionMessages.jsp" />

<section id="page_header">
    <h1>Manage Reports</h1>
    <h2>Favorite, move, update, and search for new reports</h2>
</section>

<div id="report_menu_container">
    <ul id="report_menu" class="nav nav-pills">
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

<section id="reports">
    <h1> ${PageDescription} </h1>

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

    <ul id="report_list">
        <s:iterator value="userReports">
            <li>
                <section class="report">
                    <a href="ManageReports!toggleFavorite.action?reportId=<s:property value="report.id" />" class="favorite">
                        <s:if test="favorite">
                            <s:set name="icon_class">icon-star icon-large favorite</s:set>
                        </s:if>
                        <s:else>
                            <s:set name="icon_class">icon-star icon-large</s:set>
                        </s:else>

                        <i class="${icon_class}"></i>
                    </a>

                    <a class="name" href="ReportDynamic.action?report=<s:property value="report" />">
                        <s:property value="report.name" />
                    </a>

                    <span class="created-by">Created by <s:property value="report.createdBy.name" /></span>

                    <s:if test="report.id != 11 && report.id != 12">
                        <s:if test="%{@com.picsauditing.models.ReportDynamicModel@canUserDelete(permissions.userId, report)}">
                            <a class="delete" href="ManageReports!deleteReport.action?reportId=<s:property value="report.id" />">Delete</a>
                        </s:if>
                        <s:else>
                            <a class="delete" href="ManageReports!removeUserReport.action?reportId=<s:property value="report.id" />">Remove</a>
                        </s:else>
                    </s:if>
                </section>
            </li>
        </s:iterator>
    </ul>
</section>