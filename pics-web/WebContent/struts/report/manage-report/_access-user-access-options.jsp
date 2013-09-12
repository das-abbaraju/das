<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:url action="ManageReports" method="transferOwnership" var="transfer_ownership_url">
    <s:param name="reportId">${reportId}</s:param>
    <s:param name="shareId">${person.id}</s:param>
</s:url>

<s:url action="ManageReports" method="shareWithEditPermission" var="share_edit_permission_url">
    <s:param name="reportId">${reportId}</s:param>
    <s:param name="shareId">${person.id}</s:param>
</s:url>

<s:url action="ManageReports" method="shareWithViewPermission" var="share_view_permission_url">
    <s:param name="reportId">${reportId}</s:param>
    <s:param name="shareId">${person.id}</s:param>
</s:url>

<s:url action="ManageReports" method="unshare" var="unshare_url">
    <s:param name="reportId">${reportId}</s:param>
    <s:param name="shareId">${person.id}</s:param>
</s:url>

<div class="access-options btn-group pull-right">
    <button class="btn dropdown-toggle" data-toggle="dropdown" href="#">
        <s:if test="#person.editable">
            <i class="icon-edit"></i> <s:text name="ManageReports.access.dropDown.userCanEdit" /> <span class="caret"></span>
        </s:if>
        <s:else>
            <i class="icon-eye-open"></i> <s:text name="ManageReports.access.dropDown.userCanView" /> <span class="caret"></span>
        </s:else>
    </button>
    
    <ul class="dropdown-menu">
        <s:if test="canTransferOwnership">
        
            <li class="owner">
                <a href="${transfer_ownership_url}" data-report-id="${reportId}"><i class="icon-key"></i> <s:text name="ManageReports.access.dropDown.owner" /></a>
            </li>
            
        </s:if>
        
        <li class="edit">
            <a href="${share_edit_permission_url}" data-report-id="${reportId}"><i class="icon-edit"></i> <s:text name="ManageReports.access.dropDown.userCanEdit" /></a>
        </li>
        
        <li class="view">
            <a href="${share_view_permission_url}" data-report-id="${reportId}"><i class="icon-eye-open"></i> <s:text name="ManageReports.access.dropDown.userCanView" /></a>
        </li>
        
        <li class="divider"></li>
        
        <li class="remove">
            <a href="${unshare_url}" data-report-id="${reportId}"><i class="icon-remove"></i> <s:text name="ManageReports.access.dropDown.userRemove" /></a>
        </li>
    </ul>
</div>