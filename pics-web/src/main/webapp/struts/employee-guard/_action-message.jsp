<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<% if (request.getParameter("type") != null) { %>
    <s:set var="type">${param.type}</s:set>
<% } else { %>
    <s:set var="type">info</s:set>
<% } %>

<% if (request.getParameter("message") != null) { %>
    <s:set var="message">${param.message}</s:set>
<% } else { %>
    <s:set var="message" value="%{''}" />
<% } %>

<s:if test="#message != ''">
    <s:if test="#type == 'error'">
        <s:set var="alert_class">alert-error</s:set>
    </s:if>
    <s:elseif test="#type == 'warning'">
        <s:set var="alert_class">alert-warning</s:set>
    </s:elseif>
    <s:elseif test="#type == 'danger'">
        <s:set var="alert_class">alert-danger</s:set>
    </s:elseif>
    <s:else>
        <s:set var="alert_class">alert-info</s:set>
    </s:else>

    <div class="alert ${alert_class}">
        <%-- <button type="button" class="close" data-dismiss="alert">&times;</button> --%>
        ${message}
    </div>
</s:if>
