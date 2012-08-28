<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="../actionMessages.jsp" />

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title"><s:text name="ManageReports.title" /></s:param>
    <s:param name="subtitle"><s:text name="ManageReports.subtitle" /></s:param>
</s:include>

<s:include value="/struts/report/_report-list-menu.jsp" />

<h3><s:text name="ManageReports.favorites.subtitle" /></h3>

<div id="report_favorites">
    <s:include value="/struts/report/_list-favorites.jsp" />
</div>