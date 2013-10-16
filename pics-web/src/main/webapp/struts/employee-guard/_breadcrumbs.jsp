<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<ol class="breadcrumb">
	<s:iterator value="breadcrumbs" var="breadcrumb" status="status">
        <s:if test="#status.last == false">
            <li><a href="${breadcrumb.uri(id)}">${breadcrumb.name}</a></li>
        </s:if>
        <s:else>
            <li class="active">${breadcrumb.name(displayName)}</li>
        </s:else>
	</s:iterator>
</ol>