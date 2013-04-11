<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Url --%>
<s:url action="ManageReports" method="%{#report.favorite ? 'unfavorite' : 'favorite'}" var="report_favorite_url">
    <s:param name="reportId">${report.id}</s:param>
</s:url>

<s:url action="ManageReports" method="share" var="report_share_url">
    <s:param name="reportId">${report.id}</s:param>
</s:url>

<s:url action="ManageReports" method="removeReport" var="report_remove_url">
    <s:param name="reportId">${report.id}</s:param>
</s:url>

<%-- Favorite Text --%>
<s:set name="favorite_text" value="%{#report.favorite ? 'Unfavorite' : 'Favorite'}" />

<%-- Favorite Class --%>
<s:set name="favorite_class" value="%{#report.favorite ? 'unfavorite' : 'favorite'}" />

<div class="btn-group pull-right">
    <button class="dropdown-toggle btn btn-link" data-toggle="dropdown">
        Options
    </button>

    <ul class="dropdown-menu">
        <li>
            <a href="${report_favorite_url}" class="favorite-action ${favorite_class}" data-id="${report.id}">${favorite_text}</a>
        </li>
        <li>
            <a href="${report_share_url}">Share&hellip;</a>
        </li>
        <li>
            <a href="${report_remove_url}">Remove</a>
        </li>
    </ul>
</div>

<div class="icons pull-right">
    <s:if test="#report.editable">
        <i class="icon-edit icon-large"></i>
    </s:if>
</div>