<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="access_search_term_placeholder" value="%{'Share with people and groups'}" />
<s:url action="ManageReports" method="share" var="manage_report_access_search_url" />

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title">Report Access</s:param>
    <s:param name="subtitle">Share your report and modify access permissions</s:param>
</s:include>

<s:include value="/struts/report/manage-report/_menu.jsp" />

<h4 class="report-subtitle">Look ma I am a subtitle.</h4>

<div id="report_access_search">
    <form id="report_access_search_form" class="form-inline" action="${manage_report_access_search_url}">
        <input type="text" name="searchTerm" placeholder="${access_search_term_placeholder}" class="search-query span4" />
        <i class="icon-search"></i>
    </form>
</div>

<h1></h1>

<div class="row">

    
    <%-- has an empty access group - display span12 --%>
    
    <%-- has both access group - display span6 --%>

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
                            <li class="edit">
                                <a href="#"><i class="icon-edit"></i> Can Edit</a>
                            </li>
                            <li class="view">
                                <a href="#"><i class="icon-eye-open"></i> Can View</a>
                            </li>
                            <li class="divider"></li>
                            <li class="remove">
                                <a href="#"><i class="icon-remove"></i> Remove</a>
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
                <li class="user owner clearfix">
                    <div class="is-owner pull-right">
                        <i class="icon-key"></i> Owner
                    </div>
                    
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
                            <i class="icon-edit"></i> Can Edit <span class="caret"></span>
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
                    
                    <div class="summary">
                        <a href="#" class="name">Don Couch</a>
                        <p class="description">Ancon Marine</p>
                    </div>
                </li>
                <li class="user view clearfix">
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
                    
                    <div class="summary">
                        <a href="#" class="name">Don Couch</a>
                        <p class="description">Ancon Marine</p>
                    </div>
                </li>
            </ul>
        </section>
    </div>
</div>