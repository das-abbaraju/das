<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<% if (request.getParameter("method") != null) { %>
    <s:set name="method">${param.method}</s:set>
<% } else { %>
    <s:set name="method" value="%{''}" />
<% } %>

<s:set var="alpha_sort_key">alpha</s:set>
<s:set var="date_sort_key">dateAdded</s:set>
<s:set var="last_viewed_sort_key">lastViewed</s:set>

<s:set var="is_alpha_filter_active_class" value="%{sort == null || #alpha_sort_key.equals(sort) ? 'active' : ''}" />
<s:set var="is_date_filter_active_class" value="%{#date_sort_key.equals(sort) ? 'active' : ''}" />
<s:set var="is_last_viewed_filter_active_class" value="%{#last_viewed_sort_key.equals(sort) ? 'active' : ''}" />

<s:url action="ManageReports" method="%{method}" var="alpha_sort_url">
    <s:param name="sort">${alpha_sort_key}</s:param>
    <s:param name="direction">
        ${(sort == null && direction == null) || (sort == 'alpha' && direction == 'ASC') ? 'DESC' : 'ASC'}
    </s:param>
</s:url>

<s:url action="ManageReports" method="%{method}" var="date_added_sort_url">
    <s:param name="sort">${date_sort_key}</s:param>
    <s:param name="direction">
        ${sort == date_sort_key && direction == 'ASC' ? 'DESC' : 'ASC'}
    </s:param>
</s:url>

<s:url action="ManageReports" method="%{method}" var="last_viewed_sort_url">
    <s:param name="sort">${last_viewed_sort_key}</s:param>
    <s:param name="direction">
        ${sort == last_viewed_sort_key && direction == 'ASC' ? 'DESC' : 'ASC'}
    </s:param>
</s:url>

<div id="manage_report_filter" class="btn-group">
    <a href="${alpha_sort_url}" class="btn ${is_alpha_filter_active_class}">
        <s:text name="ManageReports.ACTION.filter.alphabetical" />
    </a>
    <a href="${date_added_sort_url}" class="btn ${is_date_filter_active_class}">
        <s:text name="ManageReports.ACTION.filter.dateAdded" />
    </a>
    <a href="${last_viewed_sort_url}" class="btn ${is_last_viewed_filter_active_class}">
        <s:text name="ManageReports.ACTION.filter.lastViewed" />
    </a>
</div>