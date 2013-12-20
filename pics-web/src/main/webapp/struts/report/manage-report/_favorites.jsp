<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Url --%>
<s:url action="Reference" method="dynamicReport" var="dr_walkthrough_url" />

<s:set var="new_to_dynamic_reports" value="true" />

<s:if test="#new_to_dynamic_reports">
    <div class="row">
        <div class="span6 offset3">
            <div class="alert alert-info alert-block">
                <button type="button" class="close" data-dismiss="alert">×</button>

                <h4>We've Added Popular (userType) Reports To Your Favorites</h4>

                <p>Customize this list by searching for new reports and clicking the star to favorite or unfavorite  a report.</p>

                <p>Take a look at our <strong><a href="${dr_walkthrough_url}" target="_blank">quick walkthrough</a></strong> of Dynamic Reports.</p>
            </div>
        </div>
    </div>
</s:if>

<s:if test="!reportFavoriteList.isEmpty()">
    <s:set var="has_overflow_list" value="%{reportFavoriteListOverflow != null && reportFavoriteListOverflow.size > 0 ? true : false}" />

    <section id="favorite_reports">
        <%-- cannot pass list as a include : param - bypass via setter --%>
        <s:set var="reports" value="reportFavoriteList" />
        <s:include value="/struts/report/manage-report/_report-list.jsp">
            <s:param name="actions_path">/struts/report/manage-report/_favorites-actions.jsp</s:param>
        </s:include>
    </section>
    
    <s:if test="#has_overflow_list">
        <section id="favorite_reports_overflow">
            <h1><s:text name="ManageReports.favorites.NotIncluded" /></h1>
            
            <%-- cannot pass list as a include : param - bypass via setter --%>
            <s:set var="reports" value="reportFavoriteListOverflow" />
            <s:include value="/struts/report/manage-report/_report-list.jsp">
                <s:param name="actions_path">/struts/report/manage-report/_favorites-actions.jsp</s:param>
            </s:include>
        </section>
    </s:if>
</s:if>
<s:else>
    <div class="row">
        <div class="span6 offset3">
            <div class="alert alert-info alert-block">
                <button type="button" class="close" data-dismiss="alert">×</button>
                
                <h4><s:text name="ManageReports.favorites.noResults.noFavoritesTitle" /></h4>
                <p>
                    <s:text name="ManageReports.favorites.noResults.noFavoritesMessage" />
                </p>
            </div>
        </div>
    </div>
</s:else>