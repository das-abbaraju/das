<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%@ page import="com.picsauditing.report.ReportUtil" %>

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title"><s:text name="ManageReports.title" /></s:param>
    <s:param name="subtitle"><s:text name="ManageReports.subtitle" /></s:param>
</s:include>

<s:include value="/struts/report/manage-report/_menu.jsp" />

<h4 class="report-subtitle"><s:text name="ManageReports.myReports.subtitle" /></h4>

<s:if test="!reportUsers.isEmpty()">
    <s:include value="/struts/report/manage-report/_filter.jsp">
        <s:param name="method">myReports</s:param>
    </s:include>
</s:if>

<div id="report_my_reports">
    <s:include value="/struts/report/manage-report/_my-reports-list.jsp" />
</div>