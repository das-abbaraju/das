<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<% if (request.getParameter("title") != null) { %>
    <s:set name="title">${param.title}</s:set>
<% } else { %>
    <s:set name="title">Title</s:set>
<% } %>

<% if (request.getParameter("subtitle") != null) { %>
    <s:set name="subtitle">${param.subtitle}</s:set>
<% } else { %>
    <s:set name="subtitle" value="''" />
<% } %>

<title>${param.title}</title>

<div class="page-header">
    <h1 class="title">${title}</h1>
    <p class="subtitle">
        ${subtitle}
    </p>
</div>