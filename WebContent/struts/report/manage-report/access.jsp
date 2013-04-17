<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Url --%>
<s:url action="ManageReports" method="share" var="manage_report_access_search_url" />

<%-- Text --%>
<s:set var="access_search_term_placeholder" value="%{'Share with people and groups'}" />

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title">Report Access</s:param>
    <s:param name="subtitle">Share your report and modify access permissions</s:param>
</s:include>

<s:include value="/struts/report/manage-report/_menu.jsp" />

<h4 class="report-subtitle">Look ma I am a subtitle.</h4>

<div class="row">
    <div id="report_access_search" class="span4 offset4">
        <form id="report_access_search_form" class="form-inline" action="${manage_report_access_search_url}">
            <input type="text" name="searchTerm" placeholder="${access_search_term_placeholder}" class="search-query" data-report-id="${reportId}" />
            <i class="icon-search"></i>
        </form>
    </div>
</div>

<div id="report_access_container" class="row">
    <s:include value="/struts/report/manage-report/_access.jsp" />
</div>