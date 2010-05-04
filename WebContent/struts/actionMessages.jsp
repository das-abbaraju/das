<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="actionMessages.size > 0">
	<div class="info">
	<s:iterator value="actionMessages">
		<s:property escape="false" /><br />
	</s:iterator>
	</div>
	<s:property value="actionMessages = null"/>
</s:if>

<s:if test="actionErrors.size > 0">
	<div class="error">
	<s:iterator value="actionErrors">
		<s:property escape="false" /><br />
	</s:iterator>
	</div>
	<s:property value="actionErrors = null"/>
</s:if>
