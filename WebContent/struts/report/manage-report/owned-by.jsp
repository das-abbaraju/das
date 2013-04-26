<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title"><s:text name="ManageReports.ownedBy.pageHeader.title" /></s:param>
    <s:param name="subtitle"><s:text name="ManageReports.ownedBy.pageHeader.subtitle" /></s:param>
</s:include>

<s:include value="/struts/report/manage-report/_menu.jsp" />

<h4 class="report-subtitle"><s:text name="ManageReports.ownedBy.pageSubheader.subheader" /></h4>

<div id="owned_by_reports_container">
    <s:include value="/struts/report/manage-report/_owned-by.jsp" />
</div>