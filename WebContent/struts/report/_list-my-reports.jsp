<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- cannot pass list as a include : param - bypass via setter --%>
<s:set var="reports" value="userReports" />
<s:include value="/struts/report/_report-list.jsp">
    <s:param name="list_id">report_my_reports_list</s:param>
    <s:param name="list_class">report-list</s:param>
    <s:param name="enable_sort" value="false" />
</s:include>