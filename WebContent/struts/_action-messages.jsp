<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="hasActionMessages()">
    <div class="alert alert-info">
        <button type="button" class="close" data-dismiss="alert">×</button>
    	<h4 class="alert-heading">Information</h4>
		<s:iterator value="actionMessages">
            <p><s:property escape="false"/></p>
		</s:iterator>
	</div>
</s:if>

<s:if test="hasAlertMessages()">
    <div class="alert alert-block">
        <button type="button" class="close" data-dismiss="alert">×</button>
    	<h4 class="alert-heading">Warning</h4>
		<s:iterator value="alertMessages">
            <p><s:property escape="false"/></p>
		</s:iterator>
	</div>
</s:if>

<s:if test="hasActionErrors()">
    <div class="alert alert-error">
        <button type="button" class="close" data-dismiss="alert">×</button>
    	<h4 class="alert-heading">Error</h4>
		<s:iterator value="actionErrors">
            <p><s:property escape="false"/></p>
		</s:iterator>
	</div>
</s:if>

<s:property value="clearMessages()" />
