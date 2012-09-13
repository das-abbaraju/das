<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="report_search_list">
    <s:include value="../_action-messages.jsp" />

    <ul class="report-list">
        <s:iterator value="userReports" var="user_report">
            <s:set name="report" value="#user_report.report" />
            <s:set name="report_id" value="#report.id" />
    
            <%-- Url --%>
            <s:url action="Report" var="report_url">
                <s:param name="report">${report_id}</s:param>
            </s:url>
    
            <li class="report">
                <div class="summary">
                    <a href="${report_url}" class="name">
                        ${report.name}
                    </a>
    
                    <s:if test="#report.createdBy.id != permissions.userId">
                        <span class="created-by"><s:text name="ManageReports.report.createdBy" /> ${report.createdBy.name}</span>
                    </s:if>
    
                    <p class="description">${report.description}</p>
                </div>
    
                <span class="number-favorites">
                    ${report.numTimesFavorited} <s:text name="ManageReports.report.Favorites" />
                </span>
    
                <div class="clearfix"></div>
            </li>
        </s:iterator>
    </ul>
    
    <%-- if pagination --%>
    <div class="pagination pagination-centered">
        <ul>
            <%-- if previous page --%>
            <li>
                <a href="#"><i class="icon-caret-left"></i></a>
            </li>
            
            <%-- get pages, iterate over pages, 1 to 4 --%>
            <li>
                <a href="#">1</a>
            </li>
            <li>
                <a href="#">2</a>
            </li>
            <li>
                <a href="#">3</a>
            </li>
            <li>
                <a href="#">4</a>
            </li>
            
            <%-- if next page --%>
            <li>
                <a href="#"><i class="icon-caret-right"></i></a>
            </li>
        </ul>
    </div>
</div>