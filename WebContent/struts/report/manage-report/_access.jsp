<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Container configuration --%>
<s:if test="userAccessList.size() > 0 && groupAccessList.size() > 0">
    <%-- has both access group and access user - display span6 --%>
    <s:set var="column_size">span6</s:set>
</s:if>
<s:else>
    <%-- has an empty access group - display span12 --%>
    <s:set var="column_size">span12</s:set>
</s:else>

<s:if test="groupAccessList.size() > 0">
    <div id="group_access_container" class="${column_size}">
        <s:set var="groups" value="groupAccessList" />
        <s:include value="/struts/report/manage-report/_access-groups.jsp" />
    </div>
</s:if>

<s:if test="userAccessList.size() > 0">
    <div id="user_access_container" class="${column_size}">
        <s:set var="persons" value="userAccessList" />
        <s:include value="/struts/report/manage-report/_access-users.jsp" />
    </div>
</s:if>