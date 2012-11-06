<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="!isLiveEnvironment()">
    <s:set var="environment" value="%{getPicsEnvironment()}" />
    <s:set var="is_localhost" value="%{isLocalhostEnvironment() ? 'active' : ''}" />
    <s:set var="is_alpha" value="%{isAlphaEnvironment() ? 'active' : ''}" />
    <s:set var="is_config" value="%{isConfigurationEnvironment() ? 'active' : ''}" />
    <s:set var="is_beta" value="%{isBetaEnvironment() ? 'active' : ''}" />
    <s:set var="is_qastable" value="%{getPicsEnvironment() == 'qa-stable' ? 'active' : ''}" />
    <s:set var="is_qabeta" value="%{getPicsEnvironment() == 'qa-beta' ? 'active' : ''}" />
   
    <s:if test="getQueryString() != ''">
        <s:set var="query_string" value="'?' + getQueryString()" />
    </s:if>
    <s:else>
        <s:set var="query_string" value="''" />
    </s:else>
    
    <s:set var="localhost_url" value="'http://localhost:8080' + getServletPath() + #query_string" />
    <s:set var="alpha_url" value="'http://alpha.picsorganizer.com' + getServletPath() + #query_string" />
    <s:set var="config_url" value="'http://config.picsorganizer.com' + getServletPath() + #query_string" />
    <s:set var="beta_url" value="'http://beta.picsorganizer.com' + getServletPath() + #query_string" />
    <s:set var="stable_url" value="'http://stable.picsorganizer.com' + getServletPath() + #query_string" />
    
    <s:if test="isQaEnvironment()">
        <s:set var="qabeta_url" value="'http://qa-beta.picsorganizer.com' + getServletPath() + #query_string" />
        <s:set var="qastable_url" value="'http://qa-stable.picsorganizer.com' + getServletPath() + #query_string" />
    </s:if>
    
    <div id="environment" class="navbar navbar-fixed-bottom">
        <span class="database" title="Server: <%= java.net.InetAddress.getLocalHost().getHostName() %>">DB@<s:property value="@com.picsauditing.search.Database@getDatabaseName()"/></span>
        
        <a href="${localhost_url}" class="${is_localhost} localhost" target="_blank">Localhost</a>
        <a href="${alpha_url}" class="${is_alpha} alpha" target="_blank">Alpha</a>
        <a href="${config_url}" class="${is_config} config" target="_blank">Config</a>
        <a href="${beta_url}" class="${is_beta} beta" target="_blank">Beta</a>
        <a href="${stable_url}" class="stable" target="_blank">Stable</a>
        
	    <s:if test="isQaEnvironment()">
	        <a href="${qabeta_url}" class="${is_qabeta} qa-beta" target="_blank">QA-Beta</a>
	        <a href="${qastable_url}" class="${is_qastable} qa-stable" target="_blank">QA-Stable</a>
	    </s:if>
    </div>
</s:if>