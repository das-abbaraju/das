<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="!isLiveEnvironment()">
    <s:set var="environment" value="" />
    <s:set var="is_localhost" value="" />
    <s:set var="is_alpha" value="" />
    <s:set var="is_config" value="" />
    
    <s:if test="isLocalhostEnvironment()">
        <s:set var="environment" value="%{'localhost'}" />
        <s:set var="is_localhost" value="%{'active'}" />
    </s:if>
    <s:elseif test="isAlphaEnvironment()">
        <s:set var="environment" value="%{'alpha'}" />
        <s:set var="is_alpha" value="%{'active'}" />
    </s:elseif>
    <s:elseif test="isConfigurationEnvironment()">
        <s:set var="environment" value="%{'config'}" />
        <s:set var="is_config" value="%{'active'}" />
    </s:elseif>
        
    <div class="environment ${environment}">
        <span class="database">DB@<s:property value="@com.picsauditing.search.Database@getDatabaseName()"/></span>
        <span class="${is_localhost}">Localhost</span>
        <span class="${is_alpha}">Alpha</span>
        <span class="${is_config}">Config</span>
    </div>
</s:if>