<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="accessType == 'group'">
    <s:url action="ManageReports" method="shareWithGroupEditPermission" var="share_edit_permission_url">
        <s:param name="reportId">${reportId}</s:param>
        <s:param name="shareId">${group.id}</s:param>
    </s:url>
    
    <s:url action="ManageReports" method="shareWithGroupViewPermission" var="share_view_permission_url">
        <s:param name="reportId">${reportId}</s:param>
        <s:param name="shareId">${group.id}</s:param>
    </s:url>
    
    <s:url action="ManageReports" method="unshareGroup" var="unshare_url">
        <s:param name="reportId">${reportId}</s:param>
        <s:param name="shareId">${group.id}</s:param>
    </s:url>
</s:if>
<s:else>
    <s:url action="ManageReports" method="shareWithAccountEditPermission" var="share_view_permission_url">
        <s:param name="reportId">${reportId}</s:param>
        <s:param name="shareId">${group.id}</s:param>
    </s:url>
    
    <s:url action="ManageReports" method="shareWithAccountViewPermission" var="share_view_permission_url">
        <s:param name="reportId">${reportId}</s:param>
        <s:param name="shareId">${group.id}</s:param>
    </s:url>
    
    <s:url action="ManageReports" method="unshareAccount" var="unshare_url">
        <s:param name="reportId">${reportId}</s:param>
        <s:param name="shareId">${group.id}</s:param>
    </s:url>
</s:else>

<div class="access-options btn-group pull-right">
    <button class="btn dropdown-toggle" data-toggle="dropdown" href="#">
        <s:if test="#group.editable">
            <i class="icon-edit"></i> Can Edit <span class="caret"></span>
        </s:if>
        <s:else>
            <i class="icon-eye-open"></i> Can View <span class="caret"></span>
        </s:else>
    </button>
    
    <ul class="dropdown-menu">
        <li class="edit">
            <a href="${share_edit_permission_url}" data-report-id="${reportId}"><i class="icon-edit"></i> Can Edit</a>
        </li>
        
        <li class="view">
            <a href="${share_view_permission_url}" data-report-id="${reportId}"><i class="icon-eye-open"></i> Can View</a>
        </li>
        
        <li class="divider"></li>
        
        <li class="remove">
            <a href="${unshare_url}" data-report-id="${reportId}"><i class="icon-remove"></i> Remove</a>
        </li>
    </ul>
</div>