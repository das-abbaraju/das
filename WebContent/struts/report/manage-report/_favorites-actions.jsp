<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Url --%>
<s:url action="ManageReports" method="unfavorite" var="report_favorite_url">
    <s:param name="reportId">${report.id}</s:param>
</s:url>

<s:url action="ManageReports" method="%{#report.pinned ? 'unpinFavorite' : 'pinFavorite'}" var="report_pin_url">
    <s:param name="reportId">${report.id}</s:param>
    <s:param name="pinnedIndex">${rowstatus.index}</s:param>
</s:url>

<s:url action="ManageReports" method="moveFavoriteUp" var="report_favorite_move_up_url">
    <s:param name="reportId">${report.id}</s:param>
</s:url>

<s:url action="ManageReports" method="moveFavoriteDown" var="report_favorite_move_down_url">
    <s:param name="reportId">${report.id}</s:param>
</s:url>

<%-- Class --%>
<s:set var="report_pin_class" value="%{#report.pinned ? 'unpin' : 'pin'}" />

<%-- Text --%>
<s:set var="report_pin_text" value="%{#report.pinned ? 'Unpin Position' : 'Pin Position'}" />

<s:if test="#reports.size">
    <div class="report-options btn-group pull-right">
        <button class="dropdown-toggle btn btn-link" data-toggle="dropdown">
            <s:text name="ManageReports.myReports.Options" />
        </button>

        <ul class="dropdown-menu">
            <li>
                <a href="${report_favorite_url}" class="unfavorite" data-report-id="${report.id}">Unfavorite</a>
            </li>
            
            <li class="divider"></li>
            
            <li>
                <a href="${report_pin_url}" class="${report_pin_class}" data-report-id="${report.id}" data-pinned-index="${rowstatus.index}">${report_pin_text}</a>
            </li>
            
            <s:if test="#report.canMoveUp()">
                <li>
                    <a href="${report_favorite_move_up_url}" class="move-up">
                        <s:text name="ManageReports.myReports.MoveUp" />
                    </a>
                </li>
            </s:if>
            
            <s:if test="#report.canMoveDown()">
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
    <s:if test="#report.pinned">
        <i class="icon-pushpin icon-large"></i>
    </s:if>
</div>