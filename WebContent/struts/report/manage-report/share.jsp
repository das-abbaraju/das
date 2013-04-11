<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="share_search_term_placeholder" value="%{'Share with people and groups'}" />
<s:url action="ManageReports" method="share" var="manage_report_share_search_url" />

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title">Share: Contractor List</s:param>
    <s:param name="subtitle">You are sharing this report with the selected people and groups</s:param>
</s:include>

<div id="report_share_search">
    <form id="report_share_search_form" class="form-inline" action="${manage_report_share_search_url}">
        <input type="text" name="searchTerm" placeholder="${share_search_term_placeholder}" class="search-query span4" />
        <i class="icon-search"></i>
    </form>
</div>

<h1></h1>

<div class="row">
    <div class="span6">
        <section id="group_access">
            <h1>Groups with access</h1>
            
            <ul class="group-list unstyled">
                <li class="group clearfix">
                    <div class="access-options btn-group pull-right">
                        <button class="btn dropdown-toggle" data-toggle="dropdown" href="#">
                            <i class="icon-eye-open"></i> Can View <span class="caret"></span>
                        </button>
                        
                        <ul class="dropdown-menu">
                            <li>
                                <a href="#" class="permission edit"><i class="icon-edit"></i> Can Edit</a>
                            </li>
                            <li>
                                <a href="#" class="permission view"><i class="icon-eye-open"></i> Can View</a>
                            </li>
                            <li class="divider"></li>
                            <li>
                                <a href="#" class="permission remove"><i class="icon-remove"></i> Remove</a>
                            </li>
                        </ul>
                    </div>
                    
                    <div class="summary">
                        <a href="#" class="name">Ancon Marine</a>
                        <p class="description">Signal Hill, CA</p>
                    </div>
                </li>
            </ul>
        </section>
    </div>
    
    <div class="span6">
        <section id="user_access">
            <h1>People with access</h1>
            
            <ul class="user-list unstyled">
                <li class="user clearfix">
                    <s:url action="ManageReports" method="transferOwnership" var="transfer_ownership_url">
                        <s:param name="reportId">1</s:param>
                        <s:param name="toUser">1</s:param>
                    </s:url>
                    
                    <s:url action="ManageReports" method="shareWithEditPermission" var="share_edit_permission_url">
                        <s:param name="reportId">1</s:param>
                        <s:param name="toUser">1</s:param>
                    </s:url>
                    
                    <s:url action="ManageReports" method="shareWithViewPermission" var="share_view_permission_url">
                        <s:param name="reportId">1</s:param>
                        <s:param name="toUser">1</s:param>
                    </s:url>
                    
                    <s:url action="ManageReports" method="unshare" var="unshare_url">
                        <s:param name="reportId">1</s:param>
                        <s:param name="toUser">1</s:param>
                    </s:url>
                
                    <div class="access-options btn-group pull-right">
                        <button class="btn dropdown-toggle" data-toggle="dropdown" href="#">
                            <i class="icon-eye-open"></i> Can View <span class="caret"></span>
                        </button>
                        
                        <ul class="dropdown-menu">
                            <li>
                                <a href="${transfer_ownership_url}" class="permission owner"><i class="icon-key"></i> Owner</a>
                            </li>
                            <li>
                                <a href="${share_edit_permission_url}" class="permission edit"><i class="icon-edit"></i> Can Edit</a>
                            </li>
                            <li>
                                <a href="${share_view_permission_url}" class="permission view"><i class="icon-eye-open"></i> Can View</a>
                            </li>
                            <li class="divider"></li>
                            <li>
                                <a href="${unshare_url}" class="permission remove"><i class="icon-remove"></i> Remove</a>
                            </li>
                        </ul>
                    </div>
                    
                    <div class="summary">
                        <a href="#" class="name">Don Couch</a>
                        <p class="description">Ancon Marine</p>
                    </div>
                </li>
            </ul>
        </section>
    </div>
</div>