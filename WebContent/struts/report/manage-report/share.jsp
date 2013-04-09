<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="share_search_term_placeholder" value="%{'Share with people and groups'}" />
<s:url action="ManageReports" method="share" var="manage_report_share_search_url" />

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title">Share</s:param>
    <s:param name="subtitle">You are sharing this report with the selected people and groups</s:param>
</s:include>

<div id="report_share_search">
    <form id="report_share_search_form" class="form-inline" action="${manage_report_share_search_url}">
        <input type="text" name="searchTerm" placeholder="${share_search_term_placeholder}" class="search-query span4" />
        <i class="icon-search"></i>
    </form>
</div>

<div class="well">
    Contractor List
    <p>Default Contractor List for PICS Employees</p>
</div>

<ul class="user-list unstyled">
    <li class="user clearfix">
        <div class="icons pull-right">
            <a href="#"><i class="icon-edit icon-large"></i></a>
            <a href="#"><i class="icon-minus-sign icon-large"></i></a>
        </div>
        
        <div class="summary">
            <a href="#" class="name">Ancon Marine</a>
            <p class="description">Signal Hill, CA</p>
        </div>
    </li>
</ul>