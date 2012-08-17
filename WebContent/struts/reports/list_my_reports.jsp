<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="com.picsauditing.report.access.ReportUtil" %>

<s:include value="../actionMessages.jsp" />

<s:include value="/struts/layout/_page_header.jsp">
    <s:param name="title"><s:text name="ManageReports.title" /></s:param>
    <s:param name="subtitle"><s:text name="ManageReports.subtitle" /></s:param>
</s:include>

<s:include value="/struts/reports/_manage_report_menu.jsp" />

<h3><s:text name="ManageReports.myReports.subtitle" /></h3>

<div id="my_reports_filter">
    <div class="btn-group">
        <a href="${myReportsUrl}?sort=${alphaSort}" class="btn"><s:text name="ManageReports.myReports.alphabetical" /></a>
        <a href="${myReportsUrl}?sort=${dateAddedSort}" class="btn"><s:text name="ManageReports.myReports.dateAdded" /></a>
        <a href="${myReportsUrl}?sort=${lastOpenedSort}" class="btn"><s:text name="ManageReports.myReports.lastOpened" /></a>
    </div>
</div>

<ul id="report_my_reports_list" class="report-list">
    <s:iterator value="userReports" var="user_report">
        <s:set name="report" value="#user_report.report" />
        <s:set name="report_id" value="#report.id" />

        <%-- Url --%>
        <s:if test="favorite">
            <s:url action="ManageReports" method="unfavorite" var="report_favorite_url">
                <s:param name="reportId">${report_id}</s:param>
            </s:url>
        </s:if>
        <s:else>
            <s:url action="ManageReports" method="favorite" var="report_favorite_url">
                <s:param name="reportId">${report_id}</s:param>
            </s:url>
        </s:else>

        <s:url action="ReportDynamic" var="report_url">
            <s:param name="report">${report_id}</s:param>
        </s:url>

        <s:url action="ManageReports" method="deleteReport" var="delete_report_url">
            <s:param name="reportId">${report_id}</s:param>
        </s:url>

        <s:url action="ManageReports" method="removeUserReport" var="remove_report_url">
            <s:param name="reportId">${report_id}</s:param>
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

                <s:if test="#report.createdBy.id != permissions.userId">
                    <span class="created-by"><s:text name="ManageReports.report.createdBy" /> ${report.createdBy.name}</span>
                </s:if>
            </div>

            <div class="btn-group options">
                <a class="dropdown-toggle" data-toggle="dropdown" href="#"><s:text name="ManageReports.myReports.Options" /></a>

                <ul class="dropdown-menu">
                    <%--
                    <li>
                        <a href="#">Share</a>
                    </li>
                    --%>
                    <li>
                        <s:if test="%{@com.picsauditing.model.ReportModel@canUserDelete(permissions.userId, report)}">
                            <a href="${delete_report_url}" class="delete"><s:text name="ManageReports.myReports.Delete" /></a>
                        </s:if>
                        <s:else>
                            <a href="${remove_report_url}" class="delete"><s:text name="ManageReports.myReports.Remove" /></a>
                        </s:else>
                    </li>
                </ul>
            </div>

            <div class="clearfix"></div>
        </li>
    </s:iterator>
</ul>