<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="groupAccessList.size() > 0">
    <div class="${column_size}">
        <s:include value="/struts/report/manage-report/_access-groups.jsp" />
    </div>
</s:if>

<s:if test="userAccessList.size() > 0">
    <div class="${column_size}">
        <s:include value="/struts/report/manage-report/_access-users.jsp" />
    </div>
</s:if>