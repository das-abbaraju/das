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

<s:url action="ManageReports" method="%{#report.public ? 'unpublicize' : 'publicize'}" var="report_public_url">
    <s:param name="reportId">${report.id}</s:param>
</s:url>

<%-- Text --%>
<s:set var="favorite_text" value="%{#report.favorite ? getText('ManageReports.ownedBy.dropDown.unfavorite') : getText('ManageReports.ownedBy.dropDown.favorite')}" />
<s:set var="public_text" value="%{#report.public ? getText('ManageReports.ownedBy.dropDown.makePrivate') : getText('ManageReports.ownedBy.dropDown.makePublic') }" />

<%-- Class --%>
<s:set var="favorite_class" value="%{#report.favorite ? 'unfavorite' : 'favorite'}" />
<s:set var="public_class" value="%{#report.public ? 'private' : 'public'}" />

<div class="report-options btn-group pull-right">
    <button class="dropdown-toggle btn btn-link" data-toggle="dropdown">
        <s:text name="ManageReports.ownedBy.dropDown.options" />
    </button>

    <ul class="dropdown-menu">
        <li>
            <a href="${report_favorite_url}" id="manage-reports-owned-option-favorite" class="${favorite_class}" data-report-id="${report.id}">${favorite_text}</a>
        </li>
        
        <li>
            <a href="${report_access_url}" id="manage-reports-owned-option-access" >
                <s:text name="ManageReports.ownedBy.dropDown.share" />&hellip;
            </a>
        </li>
        
        <li>
            <a href="${report_public_url}" id="manage-reports-owned-option-public" class="${public_class}" data-report-id="${report.id}">${public_text}</a>
        </li>
        
        <li>
            <a href="${report_access_url}" id="manage-reports-owned-option-transfer" >
                <s:text name="ManageReports.ownedBy.dropDown.transferOwnership" />&hellip;
            </a>
        </li>
        
        <li class="divider"></li>
        
        <li>
            <a href="${report_delete_url}" id="manage-reports-owned-option-delete" class="delete" data-report-id="${report.id}">
                <s:text name="ManageReports.ownedBy.dropDown.delete" />&hellip;
            </a>
        </li>
    </ul>
</div>

<div class="icons pull-right">
    <s:if test="#report.public">
        <i class="icon-search"></i>
    </s:if>
</div>