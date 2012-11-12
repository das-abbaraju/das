<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="!reportUserFavorites.isEmpty()">
    <s:if test="reportUsersFavoritesOverflow">
        <s:set var="has_overflow_list" value="true" />
    </s:if>
    <s:else>
        <s:set var="has_overflow_list" value="false" />
    </s:else>
    
    <%-- cannot pass list as a include : param - bypass via setter --%>
    <s:set var="reports" value="reportUserFavorites" />
    <s:include value="/struts/report/manage-report/_report-list.jsp">
        <s:param name="list_id">report_favorites_list</s:param>
        <s:param name="list_class">report-list</s:param>
        <s:param name="enable_sort" value="true" />
        <s:param name="enable_move_up" value="false" />
        <s:param name="enable_move_down" value="#has_overflow_list"/>
    </s:include>
    
    <s:if test="reportUsersFavoritesOverflow.size > 0">
        <h3><s:text name="ManageReports.favorites.NotIncluded" /></h3>
    </s:if>
    
    <%-- cannot pass list as a include : param - bypass via setter --%>
    <s:set var="reports" value="reportUsersFavoritesOverflow" />
    <s:include value="/struts/report/manage-report/_report-list.jsp">
        <s:param name="list_id">report_favorites_overflow_list</s:param>
        <s:param name="list_class">report-list</s:param>
        <s:param name="enable_sort" value="true" />
        <s:param name="enable_move_up" value="true" />
        <s:param name="enable_move_down" value="false"/>
    </s:include>
</s:if>
<s:else>
    <div class="row">
        <div class="span6 offset3">
            <div class="alert alert-info alert-block">
                <button type="button" class="close" data-dismiss="alert">×</button>
                
                <h4>You don't have any favorite reports.</h4>
                <p>
                    For quick access to the reports you use most, select the star icon or choose "Favorite" in the report options in My Reports.
                </p>
            </div>
        </div>
    </div>
</s:else>