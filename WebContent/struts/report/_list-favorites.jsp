<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="../_action-messages.jsp" />

<s:if test="reportUsersFavoritesOverflow">
    <s:set var="has_overflow_list" value="true" />
</s:if>
<s:else>
    <s:set var="has_overflow_list" value="false" />
</s:else>

<%-- cannot pass list as a include : param - bypass via setter --%>
<s:set var="reports" value="reportUserFavorites" />
<s:include value="/struts/report/_report-list.jsp">
    <s:param name="list_id">report_favorites_list</s:param>
    <s:param name="list_class">report-list</s:param>
    <s:param name="enable_sort" value="true" />
    <s:param name="enable_move_up" value="false" />
    <s:param name="enable_move_down" value="#has_overflow_list"/>
</s:include>

<s:if test="reportUsersFavoritesOverflow.size > 0">
    <div id="report_favorites_list_excluded">
        <h1><s:text name="ManageReports.favorites.NotIncluded" /></h1>
    </div>
</s:if>

<%-- cannot pass list as a include : param - bypass via setter --%>
<s:set var="reports" value="reportUsersFavoritesOverflow" />
<s:include value="/struts/report/_report-list.jsp">
    <s:param name="list_id">report_favorites_overflow_list</s:param>
    <s:param name="list_class">report-list</s:param>
    <s:param name="enable_sort" value="true" />
    <s:param name="enable_move_up" value="true" />
    <s:param name="enable_move_down" value="false"/>
</s:include>