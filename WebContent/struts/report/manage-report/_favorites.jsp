<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="!reportUsers.isEmpty()">
    <s:set var="has_overflow_list" value="%{reportUserOverflow != null && reportUserOverflow.size > 0 ? true : false}" />
    
    <section id="favorite_reports">
        <%-- cannot pass list as a include : param - bypass via setter --%>
        <s:set var="reports" value="reportUsers" />
        <s:include value="/struts/report/manage-report/_favorites-report-list.jsp">
            <s:param name="enable_move_up" value="false" />
            <s:param name="enable_move_down" value="#has_overflow_list"/>
        </s:include>
    </section>
    
    <s:if test="#has_overflow_list">
        <section id="favorite_reports_overflow">
            <h1><s:text name="ManageReports.favorites.NotIncluded" /></h1>
            
            <%-- cannot pass list as a include : param - bypass via setter --%>
            <s:set var="reports" value="reportUserOverflow" />
            <s:include value="/struts/report/manage-report/_favorites-report-list.jsp">
                <s:param name="enable_move_up" value="true" />
                <s:param name="enable_move_down" value="false"/>
            </s:include>
        </section>
    </s:if>
</s:if>
<s:else>
    <div class="row">
        <div class="span6 offset3">
            <div class="alert alert-info alert-block">
                <button type="button" class="close" data-dismiss="alert">×</button>
                
                <h4><s:text name="ManageReports.NoFavorites.Info" /></h4>
                <p>
                    <s:text name="ManageReports.NoFavorites.Message" />
                </p>
            </div>
        </div>
    </div>
</s:else>