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
    
    <s:if test="getQueryString() != ''">
        <s:set var="query_string" value="'?' + getQueryString()" />
    </s:if>
    <s:else>
        <s:set var="query_string" value="''" />
    </s:else>
    
    <s:set var="live_url" value="'http://www.picsorganizer.com' + getServletPath() + #query_string" />
    <s:set var="localhost_url" value="'http://localhost:8080' + getServletPath() + #query_string" />
    <s:set var="alpha_url" value="'http://alpha.picsorganizer.com' + getServletPath() + #query_string" />
    <s:set var="config_url" value="'http://config.picsorganizer.com' + getServletPath() + #query_string" />
    
    <div class="environment ${environment}">
        <span class="database">DB@<s:property value="@com.picsauditing.search.Database@getDatabaseName()"/></span>
        <a href="${localhost_url}" class="${is_localhost}" target="_blank">Localhost</a>
        <a href="${alpha_url}" class="${is_alpha}" target="_blank">Alpha</a>
        <a href="${config_url}" class="${is_config}" target="_blank">Config</a>
        <a href="${live_url}" target="_blank">Live</a>
    </div>
</s:if>