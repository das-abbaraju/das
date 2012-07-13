<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set name="title">${param.title}</s:set>
<s:set name="subtitle">${param.subtitle}</s:set>

<title>${param.title}</title>

<div class="page-header">
    <h1 class="title">${title}</h1>
    <p class="subtitle">
        ${subtitle}
    </p>
</div>