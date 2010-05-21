<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<s:iterator value="newUsers">
<s:property value="[0].name" /><s:if test="permissions.admin || permissions.corporate"> (<s:property value="[0].account.name" />)</s:if>
</s:iterator>