<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Url --%>
<s:url action="ManageReports" method="%{#report.favorite ? 'unfavorite' : 'favorite'}" var="report_favorite_url">
    <s:param name="reportId">${report.id}</s:param>
</s:url>

<s:url action="ManageReports" method="access" var="report_access_url">
    <s:param name="reportId">${report.id}</s:param>
</s:url>

<s:url action="ManageReports" method="deleteReport" var="report_delete_url">
    <s:param name="reportId">${report.id}</s:param>
</s:url>

<s:url action="ManageReports" method="%{#report.private ? 'unprivatize' : 'privatize'}" var="report_private_url">
    <s:param name="reportId">${report.id}</s:param>
</s:url>

<%-- Text --%>
<s:set var="favorite_text" value="%{#report.favorite ? 'Unfavorite' : 'Favorite'}" />
<s:set var="private_text" value="%{#report.private ? 'Make Public' : 'Make Private'}" />

<%-- Class --%>
<s:set var="favorite_class" value="%{#report.favorite ? 'unfavorite' : 'favorite'}" />
<s:set var="private_class" value="%{#report.private ? 'public' : 'private'}" />

<div class="report-options btn-group pull-right">
    <button class="dropdown-toggle btn btn-link" data-toggle="dropdown">
        Options
    </button>

    <ul class="dropdown-menu">
        <li>
            <a href="${report_favorite_url}" class="${favorite_class}" data-report-id="${report.id}">${favorite_text}</a>
        </li>
        
        <li>
            <a href="${report_access_url}">Share&hellip;</a>
        </li>
        
        <li>
            <a href="${report_private_url}" class="${private_class}" data-report-id="${report.id}">${private_text}</a>
        </li>
        
        <li>
            <a href="${report_access_url}">Transfer Ownership&hellip;</a>
        </li>
        
        <li class="divider"></li>
        
        <li>
            <a href="${report_delete_url}" class="delete" data-report-id="${report.id}">Delete&hellip;</a>
        </li>
    </ul>
</div>

<div class="icons pull-right">
    <s:if test="#report.private">
        <i class="icon-eye-close icon-large"></i>
    </s:if>
</div>