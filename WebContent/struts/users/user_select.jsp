<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:iterator value="newUsers">
<s:property value="name" /><s:if test="permissions.admin || permissions.corporate"> (<pics:permission perm="DevelopmentEnvironment"><s:property value="id" /> </pics:permission><s:property value="account.name" />)</s:if>
</s:iterator>