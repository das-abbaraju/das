<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<ul class="report-list unstyled">
    <s:iterator value="#reports" var="user_report" status="rowstatus">
        <s:set name="report" value="#user_report.report" />

        <%-- Url --%>
        <s:url action="ManageReports" method="%{#user_report.favorite ? 'unfavorite' : 'favorite'}" var="report_favorite_url">
            <s:param name="reportId">${report.id}</s:param>
        </s:url>

        <s:url action="Report" var="report_url">
            <s:param name="report">${report.id}</s:param>
        </s:url>

        <%-- Icon --%>
        <s:set name="is_favorite_class" value="%{#user_report.favorite ? 'selected' : ''}" />

        <li class="report clearfix">
            <a href="${report_favorite_url}" class="favorite" data-id="${report.id}">
                <i class="icon-star icon-large ${is_favorite_class}"></i>
            </a>

            <div class="summary">
                <a href="${report_url}" class="name">${report.name}</a>

                <s:if test="#report.createdBy.id != permissions.userId">
                    <span class="created-by"><s:text name="ManageReports.report.createdBy" /> ${report.createdBy.name}</span>
                </s:if>
            </div>
        </li>
    </s:iterator>
</ul>
