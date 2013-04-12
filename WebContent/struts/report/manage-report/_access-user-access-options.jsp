<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:url action="ManageReports" method="transferOwnership" var="transfer_ownership_url">
    <s:param name="reportId">${reportId}</s:param>
    <s:param name="toUser">${person.id}</s:param>
</s:url>

<s:url action="ManageReports" method="shareWithEditPermission" var="share_edit_permission_url">
    <s:param name="reportId">${reportId}</s:param>
    <s:param name="toUser">${person.id}</s:param>
</s:url>

<s:url action="ManageReports" method="shareWithViewPermission" var="share_view_permission_url">
    <s:param name="reportId">${reportId}</s:param>
    <s:param name="toUser">${person.id}</s:param>
</s:url>

<s:url action="ManageReports" method="unshare" var="unshare_url">
    <s:param name="reportId">${reportId}</s:param>
    <s:param name="toUser">${person.id}</s:param>
</s:url>

<div class="access-options btn-group pull-right">
    <button class="btn dropdown-toggle" data-toggle="dropdown" href="#">
        <s:if test="#person.editable">
            <i class="icon-edit"></i> Can Edit <span class="caret"></span>
        </s:if>
        <s:else>
            <i class="icon-eye-open"></i> Can View <span class="caret"></span>
        </s:else>
    </button>
    
    <ul class="dropdown-menu">
        <li class="owner">
            <a href="${transfer_ownership_url}"><i class="icon-key"></i> Owner</a>
        </li>
        
        <li class="edit">
            <a href="${share_edit_permission_url}"><i class="icon-edit"></i> Can Edit</a>
        </li>
        
        <li class="view">
            <a href="${share_view_permission_url}"><i class="icon-eye-open"></i> Can View</a>
        </li>
        
        <li class="divider"></li>
        
        <li class="remove">
            <a href="${unshare_url}"><i class="icon-remove"></i> Remove</a>
        </li>
    </ul>
</div>