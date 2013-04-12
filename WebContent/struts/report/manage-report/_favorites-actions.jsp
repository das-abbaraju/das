<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<% if (request.getParameter("enable_move_up") != null) { %>
    <s:set var="enable_move_up">${param.enable_move_up}</s:set>
<% } else { %>
    <s:set var="enable_move_up">true</s:set>
<% } %>

<% if (request.getParameter("enable_move_down") != null) { %>
    <s:set var="enable_move_down">${param.enable_move_down}</s:set>
<% } else { %>
    <s:set var="enable_move_down">true</s:set>
<% } %>

<%-- Url --%>
<s:url action="ManageReports" method="unfavorite" var="report_favorite_url">
    <s:param name="reportId">${report.id}</s:param>
</s:url>

<s:url action="ManageReports" method="moveFavoriteUp" var="report_favorite_move_up_url">
    <s:param name="reportId">${report.id}</s:param>
</s:url>

<s:url action="ManageReports" method="moveFavoriteDown" var="report_favorite_move_down_url">
    <s:param name="reportId">${report.id}</s:param>
</s:url>

<s:if test="#reports.size > 1 || #enable_move_up == 'true'">
    <div class="btn-group pull-right">
        <button class="dropdown-toggle btn btn-link" data-toggle="dropdown">
            <s:text name="ManageReports.myReports.Options" />
        </button>

        <ul class="dropdown-menu">
            <li>
                <a href="${report_favorite_url}" class="favorite-action unfavorite" data-id="${report.id}">Unfavorite</a>
            </li>
            
            <li class="divider"></li>
            
            <li>
                <a href="">Pin Position</a>
            </li>
            
            <s:if test="!#rowstatus.first || (#rowstatus.first && #enable_move_up == 'true')">
                <li>
                    <a href="${report_favorite_move_up_url}" class="move-up">
                        <s:text name="ManageReports.myReports.MoveUp" />
                    </a>
                </li>
            </s:if>
            
            <s:if test="!#rowstatus.last || (#rowstatus.last && #enable_move_down == 'true')">
                <li>
                    <a href="${report_favorite_move_down_url}" class="move-down">
                        <s:text name="ManageReports.myReports.MoveDown" />
                    </a>
                </li>
            </s:if>
        </ul>
    </div>
</s:if>

<div class="icons pull-right">
    <if test="true">
        <i class="icon-pushpin icon-large"></i>
    </if>
</div>