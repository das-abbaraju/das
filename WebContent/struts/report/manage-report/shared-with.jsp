<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title"><s:text name="ManageReports.sharedWith.pageHeader.title" /></s:param>
    <s:param name="subtitle"><s:text name="ManageReports.sharedWith.pageHeader.subtitle" /></s:param>
</s:include>

<s:include value="/struts/report/manage-report/_menu.jsp" />

<h4 class="report-subtitle"><s:text name="ManageReports.sharedWith.pageSubheader.subheader" /></h4>

<div id="shared_with_reports_container">
    <s:include value="/struts/report/manage-report/_shared-with.jsp" />
</div>