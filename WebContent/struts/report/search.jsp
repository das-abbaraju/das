<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%-- <%@ page import="com.picsauditing.report.access.ReportUtil" %> --%>

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title"><s:text name="ManageReports.title" /></s:param>
    <s:param name="subtitle"><s:text name="ManageReports.subtitle" /></s:param>
</s:include>

<s:include value="/struts/report/_report-list-menu.jsp" />

<h3><s:text name="ManageReports.search.subtitle" /></h3>

<div id="report_search">
    <s:form id="report_search_form" action="ManageReports!searchList.action" method="get" cssClass="form-inline">
        <i class="icon-search icon-large"></i>

        <input type="text" name="searchTerm" value="${searchTerm}" placeholder="<s:text name="ManageReports.search.searchReports" />" />
    </s:form>
</div>

<s:include value="/struts/report/_search-list.jsp"></s:include>