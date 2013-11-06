<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:action name="Breadcrumb" executeResult="true">
	<s:param name="action">${actionName}</s:param>
	<s:param name="method">${methodName}</s:param>
	<s:param name="id">${breadcrumb_id}</s:param>
	<s:param name="displayName">${breadcrumb_name}</s:param>
</s:action>