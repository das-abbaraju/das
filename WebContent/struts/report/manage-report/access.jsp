<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Url --%>
<s:url action="ManageReports" method="share" var="manage_report_access_search_url" />

<%-- Text --%>
<s:set var="access_search_term_placeholder" value="%{'Share with people and groups'}" />

<%-- Container configuration --%>
<s:if test="userAccessList.size() > 0 && groupAccessList.size() > 0">
    <%-- has both access group and access user - display span6 --%>
    <s:set var="column_size">span6</s:set>
</s:if>
<s:else>
    <%-- has an empty access group - display span12 --%>
    <s:set var="column_size">span12</s:set>
</s:else>

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title">Report Access</s:param>
    <s:param name="subtitle">Share your report and modify access permissions</s:param>
</s:include>

<s:include value="/struts/report/manage-report/_menu.jsp" />

<h4 class="report-subtitle">Look ma I am a subtitle.</h4>

<div id="report_access_search">
    <form id="report_access_search_form" class="form-inline" action="${manage_report_access_search_url}">
        <input type="text" name="searchTerm" placeholder="${access_search_term_placeholder}" class="search-query span4" />
        <i class="icon-search"></i>
    </form>
</div>

<h1></h1>

<div id="access_list" class="row">
    <s:if test="groupAccessList.size() > 0">
        <div class="${column_size}">
            <s:set var="groups" value="groupAccessList" />
            <s:include value="/struts/report/manage-report/_access-groups.jsp" />
        </div>
    </s:if>
    
    <s:if test="userAccessList.size() > 0">
        <div class="${column_size}">
            <s:set var="persons" value="userAccessList" />
            <s:include value="/struts/report/manage-report/_access-users.jsp" />
        </div>
    </s:if>
</div>