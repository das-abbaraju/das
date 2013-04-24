<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Url --%>
<s:url action="ManageReports" method="%{#report.favorite ? 'unfavorite' : 'favorite'}" var="report_favorite_url">
    <s:param name="reportId">${report.id}</s:param>
</s:url>

<s:url action="ManageReports" method="access" var="report_access_url">
    <s:param name="reportId">${report.id}</s:param>
</s:url>

<s:url action="ManageReports" method="removeReport" var="report_remove_url">
    <s:param name="reportId">${report.id}</s:param>
</s:url>

<%-- Favorite Text --%>
<s:set name="favorite_text" value="%{#report.favorite ? 'Unfavorite' : 'Favorite'}" />

<%-- Favorite Class --%>
<s:set name="favorite_class" value="%{#report.favorite ? 'unfavorite' : 'favorite'}" />

<div class="report-options btn-group pull-right">
    <button class="dropdown-toggle btn btn-link" data-toggle="dropdown">
        Options
    </button>

    <ul class="dropdown-menu">
        <li>
            <a href="${report_favorite_url}" class="${favorite_class}" data-report-id="${report.id}">${favorite_text}</a>
        </li>
        
        <s:if test="#report.editable">
            <li>
                <a href="${report_access_url}">Share&hellip;</a>
            </li>
        </s:if>
        
        <li class="divider"></li>
        
        <li>
            <a href="${report_remove_url}" class="remove" data-report-id="${report.id}">Remove</a>
        </li>
    </ul>
</div>

<div class="icons pull-right">
    <s:if test="#report.editable">
        <i class="icon-edit icon-large"></i>
    </s:if>
</div>