<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="com.picsauditing.report.access.ReportUtil" %>

<s:include value="../actionMessages.jsp" />

<s:include value="/struts/layout/_page_header.jsp">
    <s:param name="title">Manage Reports</s:param>
    <s:param name="subtitle">Favorite, move, update, and search for new reports</s:param>
</s:include>

<s:include value="/struts/reports/_manage_report_menu.jsp" />

<h3>Search all your reports and find new ones based on your specific needs and what's popular.</h3>

<div id="report_search">
    <s:form id="report_search_form" method="post" cssClass="form-inline">
        <i class="icon-search icon-large"></i>
        
        <input type="text" name="search" placeholder="Search Reports" />
    </s:form>
</div>

<hr />

<ul id="report_search_list" class="report-list">
    <s:iterator value="userReports" var="user_report">
        <s:set name="report" value="#user_report.report" />
        <s:set name="report_id" value="#report.id" />

        <%-- Url --%>
        <s:url action="ReportDynamic" var="report_url">
            <s:param name="report">${report_id}</s:param>
        </s:url>

        <li class="report">
            <div class="summary">
                <a href="${report_url}" class="name">
                    ${report.name}
                </a>
    
                <s:if test="#report.createdBy.id != permissions.userId">
                    <span class="created-by">Created by ${report.createdBy.name}</span>
                </s:if>
                
                <p class="description">${report.description}</p>
            </div>

            <span class="number-favorites">
                24,389 Favorites
            </span>
            
            <div class="clearfix"></div>
        </li>
    </s:iterator>
</ul>