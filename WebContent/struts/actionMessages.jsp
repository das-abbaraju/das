<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="hasActionMessages()">
	<div class="info">
	<s:iterator value="actionMessages">
		<s:property escape="false" /><br />
	</s:iterator>
	</div>
	<s:property value="actionMessages = null"/>
</s:if>

<s:if test="hasActionErrors()">
	<div class="error">
	<s:iterator value="actionErrors">
		<s:property escape="false" /><br />
	</s:iterator>
	</div>
	<s:property value="actionErrors = null"/>
</s:if>

<s:if test="hasAlertMessages()">
	<div class="alert">
		<s:iterator value="alertMessages">
			<s:property escape="false"/>
		</s:iterator>
	</div>
	<s:property value="alertMessages = null" />
</s:if>