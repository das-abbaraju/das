<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title"><s:text name="ManageReports.title" /></s:param>
    <s:param name="subtitle"><s:text name="ManageReports.subtitle" /></s:param>
</s:include>

<s:include value="/struts/report/manage-report/_report-menu.jsp" />

<h3><s:text name="ManageReports.favorites.subtitle" /></h3>

<div id="report_favorites">
    <s:include value="/struts/_action-messages.jsp" />
    
    <s:include value="/struts/report/manage-report/_favorites-list.jsp" />
</div>