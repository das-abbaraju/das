<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="com.picsauditing.report.access.ReportUtil" %>

<s:include value="../_action-messages.jsp" />

<% if (request.getParameter("list_id") != null) { %>
    <s:set var="list_id">${param.list_id}</s:set>
<% } else { %>
    <s:set var="list_id">report_list</s:set>
<% } %>

<% if (request.getParameter("list_class") != null) { %>
    <s:set var="list_class">${param.list_class}</s:set>
<% } else { %>
    <s:set var="list_class">report-list</s:set>
<% } %>

<% if (request.getParameter("enable_sort") != null) { %>
    <s:set var="enable_sort">${param.enable_sort}</s:set>
<% } else { %>
    <s:set var="enable_sort" value="true" />
<% } %>

<% if (request.getParameter("enable_move_up") != null) { %>
    <s:set var="enable_move_up">${param.enable_move_up}</s:set>
<% } else { %>
    <s:set var="enable_move_up" value="true" />
<% } %>

<% if (request.getParameter("enable_move_down") != null) { %>
    <s:set var="enable_move_down">${param.enable_move_down}</s:set>
<% } else { %>
    <s:set var="enable_move_down" value="true" />
<% } %>

<s:if test="#reports">
    <ul id="${list_id}" class="${list_class}">
        <s:iterator value="#reports" var="user_report" status="rowstatus">
            <s:set name="report" value="#user_report.report" />
            <s:set name="report_id" value="#report.id" />
    
            <%-- Url --%>
            <s:url action="ManageReports" method="unfavorite" var="report_favorite_url">
                <s:param name="reportId">${report_id}</s:param>
            </s:url>
    
            <s:url action="Report" var="report_url">
                <s:param name="report">${report_id}</s:param>
            </s:url>
    
            <s:url action="ManageReports" method="deleteReport" var="delete_report_url">
                <s:param name="reportId">${report_id}</s:param>
            </s:url>
    
            <s:url action="ManageReports" method="removeUserReport" var="remove_report_url">
                <s:param name="reportId">${report_id}</s:param>
            </s:url>
    
            <%-- Icon --%>
            <s:set name="is_favorite_class" value="''" />
    
            <s:if test="favorite">
                <s:set name="is_favorite_class">selected</s:set>
            </s:if>
    
            <li class="report">
                <a href="${report_favorite_url}" class="favorite" data-id="${report_id}">
                    <i class="icon-star icon-large ${is_favorite_class}"></i>
                </a>
    
                <div class="summary">
                    <a href="${report_url}" class="name">
                        ${report.name}
                    </a>
    
                    <s:if test="#report.createdBy.id != permissions.userId">
                        <span class="created-by"><s:text name="ManageReports.report.createdBy" /> ${report.createdBy.name}</span>
                    </s:if>
                </div>
    
                <div class="btn-group options">
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                        <s:text name="ManageReports.myReports.Options" />
                    </a>
    
                    <ul class="dropdown-menu">
                        <li>
                            <s:if test="%{@com.picsauditing.model.ReportModel@canUserDelete(permissions.userId, report)}">
                                <a href="${delete_report_url}" class="delete">
                                    <s:text name="ManageReports.myReports.Delete" />
                                </a>
                            </s:if>
                            <s:else>
                                <a href="${remove_report_url}" class="remove">
                                    <s:text name="ManageReports.myReports.Remove" />
                                </a>
                            </s:else>
                        </li>
                        
                        <s:if test="#enable_sort">
                            <li class="divider"></li>
                            
                            <s:if test="!#rowstatus.first || (#rowstatus.first && #enable_move_up)">
                                <li>
                                    <a href="ManageReports!moveUp.action?reportId=${report_id}" class="move-up">
                                        <s:text name="ManageReports.myReports.MoveUp" />
                                    </a>
                                </li>    
                            </s:if>
                            
                            <s:if test="!#rowstatus.last || (#rowstatus.last && #enable_move_down)">
                                <li>
                                    <a href="ManageReports!moveDown.action?reportId=${report_id}" class="move-down">
                                        <s:text name="ManageReports.myReports.MoveDown" />
                                    </a>
                                </li>
                            </s:if>
                        </s:if>
                    </ul>
                </div>
    
                <div class="clearfix"></div>
            </li>
        </s:iterator>
    </ul>
</s:if>