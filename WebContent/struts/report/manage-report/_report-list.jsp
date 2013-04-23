<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<% if (request.getParameter("actions_path") != null) { %>
    <s:set var="actions_path">${param.actions_path}</s:set>
<% } else { %>
    <s:set var="actions_path" value="%{''}" />
<% } %>

<ul class="report-list unstyled">
    <s:iterator value="#reports" var="report" status="rowstatus">
        <%-- Url --%>
        <s:url action="ManageReports" method="%{#report.favorite ? 'unfavorite' : 'favorite'}" var="report_favorite_url">
            <s:param name="reportId">${report.id}</s:param>
        </s:url>

        <s:url action="Report" var="report_url">
            <s:param name="report">${report.id}</s:param>
        </s:url>
        
        <%-- Favorite Class --%>
        <s:set name="favorite_class" value="%{#report.favorite ? 'favorite' : 'unfavorite'}" />

        <%-- Icon --%>
        <s:set name="is_favorite_class" value="%{#report.favorite ? 'selected' : ''}" />

        <li class="report clearfix">
            <s:if test="#actions_path != ''">
                <%-- hidden options may be being passed from the parent include --%>
                <s:include value="%{#actions_path}" />
            </s:if>
            
            <a href="${report_favorite_url}" class="${favorite_class}" data-report-id="${report.id}">
                <i class="icon-star ${is_favorite_class}"></i>
            </a>

            <a href="${report_url}" class="name">${report.name}</a>

            <s:if test="#report.createdBy.id != permissions.userId">
                <span class="created-by">${report.createdBy.name}</span>
            </s:if>
        </li>
    </s:iterator>
</ul>