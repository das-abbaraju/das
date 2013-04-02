<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="search_term_placeholder" value="%{getText('ManageReports.search.searchReports')}" />
<s:url action="ManageReports" method="search" var="manage_report_search_url" />

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title"><s:text name="ManageReports.title" /></s:param>
    <s:param name="subtitle"><s:text name="ManageReports.subtitle" /></s:param>
</s:include>

<s:include value="/struts/report/manage-report/_menu.jsp" />

<h4 class="report-subtitle"><s:text name="ManageReports.search.subtitle" /></h4>

<div id="report_search">
    <form id="report_search_form" class="form-inline" action="${manage_report_search_url}">
        <i class="icon-search icon-large"></i>

        <input type="text" name="searchTerm" value="${searchTerm}" placeholder="${search_term_placeholder}" class="search-query span4" />
    </form>
</div>

<div id="search_reports_container">
    <s:include value="/struts/report/manage-report/_search.jsp" />
</div>