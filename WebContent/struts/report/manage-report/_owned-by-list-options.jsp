<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Url --%>
<s:url action="ManageReports" method="%{#report.favorite ? 'unfavorite' : 'favorite'}" var="report_favorite_url">
    <s:param name="reportId">${report.id}</s:param>
</s:url>

<%-- Icon --%>
<s:set name="favorite_text" value="%{#report.favorite ? 'Unfavorite' : 'Favorite'}" />

${report.favorite}
${report.editable}

<div class="btn-group pull-right">
    <button class="dropdown-toggle btn btn-link" data-toggle="dropdown">
        Options
    </button>

    <ul class="dropdown-menu">
        <li>
            <a href="${report_favorite_url}">${favorite_text}</a>
        </li>
        <li>
            <a href="">Share...</a>
        </li>
        <li>
            <a href="">Make Private</a>
        </li>
        <li>
            <a href="">Transfer Ownership...</a>
        </li>
        <li>
            <a href="">Delete...</a>
        </li>
    </ul>
</div>