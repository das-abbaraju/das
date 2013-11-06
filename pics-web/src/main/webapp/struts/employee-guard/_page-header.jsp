<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<% if (request.getParameter("title") != null) { %>
<s:set var="title">${param.title}</s:set>
<% } else { %>
<s:set var="title">Title</s:set>
<% } %>

<% if (request.getParameter("subtitle") != null) { %>
<s:set var="subtitle">${param.subtitle}</s:set>
<% } else { %>
<s:set var="subtitle" value="''" />
<% } %>

<% if (request.getParameter("actions") != null) { %>
<s:set var="actions">${param.actions}</s:set>
<% } else { %>
<s:set var="actions" value="%{''}" />
<% } %>

<% if (request.getParameter("breadcrumbs") != null) { %>
<s:set var="breadcrumbs">${param.breadcrumbs}</s:set>
<% } %>

<% if (request.getParameter("breadcrumb_id") != null) { %>
<s:set var="breadcrumb_id">${param.breadcrumb_id}</s:set>
<% } %>

<% if (request.getParameter("breadcrumb_name") != null) { %>
<s:set var="breadcrumb_name">${param.breadcrumb_name}</s:set>
<% } %>

<title>${param.title}</title>

<div class="page-header pics">
    <h1 class="title">${title}</h1>
    <p class="subtitle">${subtitle}</p>
</div>

<div class="page-subheader">
    <s:if test="#actions != ''">
        <div class="row">
            <div class="col-md-8">
                <%-- Breadcrumb --%>
                <s:if test="#breadcrumbs != 'false'">
                    <s:include value="/struts/employee-guard/_breadcrumb.jsp" />
                </s:if>
            </div>
            <div class="col-md-4">
                <div class="page-actions">
                        ${actions}
                </div>
            </div>
        </div>
    </s:if>
    <s:else>
        <%-- Breadcrumb --%>
        <s:if test="#breadcrumbs != 'false'">
            <s:include value="/struts/employee-guard/_breadcrumb.jsp" />
        </s:if>
    </s:else>
</div>