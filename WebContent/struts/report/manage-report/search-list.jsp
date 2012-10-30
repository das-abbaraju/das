<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set name="search_term_placeholder" value="%{getText('ManageReports.search.searchReports')}" />

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title"><s:text name="ManageReports.title" /></s:param>
    <s:param name="subtitle"><s:text name="ManageReports.subtitle" /></s:param>
</s:include>

<s:include value="/struts/report/manage-report/_report-menu.jsp" />

<h4 class="report-subtitle"><s:text name="ManageReports.search.subtitle" /></h4>

<div id="report_search">
    <s:form id="report_search_form" action="ManageReports!searchList.action" method="get" cssClass="form-inline">
        <i class="icon-search icon-large"></i>

        <input type="text" name="searchTerm" value="${searchTerm}" placeholder="${search_term_placeholder}" class="search-query" />
    </s:form>
</div>

<s:include value="/struts/report/manage-report/_search-list.jsp"></s:include>