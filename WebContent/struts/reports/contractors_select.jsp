<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<s:iterator value="data">
<s:property value="[0].get('name')" escape="false" /><s:if test="permissions.assessment || permissions.admin">|<s:property value="[0].get('id')" escape="false" /></s:if>
</s:iterator>