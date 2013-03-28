<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<% if (request.getParameter("list_options_path") != null) { %>
    <s:set var="list_options_path">${param.list_options_path}</s:set>
<% } else { %>
    <s:set var="list_options_path" value="%{''}" />
<% } %>

<ul class="report-list unstyled">
    <s:iterator value="#reports" var="report" status="rowstatus">
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

            <a href="${report_url}" class="name">${report.name}</a>

            <s:if test="#report.createdBy.id != permissions.userId">
                <span class="created-by"><s:text name="ManageReports.report.createdBy" /> ${report.createdBy.name}</span>
            </s:if>
            
            <s:if test="#list_options_path != ''">
                <%-- hidden options may be being passed from the parent include --%>
                <s:include value="%{#list_options_path}" />
            </s:if>
        </li>
    </s:iterator>
</ul>
