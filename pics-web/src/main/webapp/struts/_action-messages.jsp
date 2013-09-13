<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page import="com.picsauditing.actions.PicsActionSupport" %>

<s:if test="hasActionMessages()">
    <div class="alert alert-info">
        <button type="button" class="close" data-dismiss="alert">×</button>
    	<h4 class="alert-heading">
            <s:if test="getActionMessageHeader() != null">
                <s:property value="getActionMessageHeader()"/>
            </s:if>
            <s:else>
                <s:text name="global.Information" />
            </s:else>
        </h4>
		<s:iterator value="actionMessages">
            <p><s:property escape="false"/></p>
		</s:iterator>
	</div>
</s:if>

<s:if test="hasAlertMessages()">
    <div class="alert alert-block">
        <button type="button" class="close" data-dismiss="alert">×</button>
    	<h4 class="alert-heading">
            <s:if test="getAlertMessageHeader() != null">
                <s:property value="getAlertMessageHeader()"/>
            </s:if>
            <s:else>
                <s:text name="global.Warning" />
            </s:else>
        </h4>
		<s:iterator value="alertMessages">
            <p><s:property escape="false"/></p>
		</s:iterator>
	</div>
</s:if>

<s:if test="hasActionErrors()">
    <div class="alert alert-error">
        <button type="button" class="close" data-dismiss="alert">×</button>
    	<h4 class="alert-heading">
            <s:if test="getActionErrorHeader() != null">
                <s:property value="getActionErrorHeader()"/>
            </s:if>
            <s:else>
                <s:text name="global.Error" />
            </s:else>
        </h4>
		<s:iterator value="actionErrors">
            <p><s:property escape="false"/></p>
		</s:iterator>
	</div>
</s:if>

<s:property value="clearMessages()" />
