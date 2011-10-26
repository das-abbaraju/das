<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
		
<s:set name="pageIsSecure" value="false" />

<s:if test="request.isSecure() || request.getLocalPort() == 443 || request.getLocalPort() == 81">
	<s:set name="pageIsSecure" value="true" />
</s:if>

<s:if test="request.isSecure()">
	<s:set name="protocol" value="https" />
</s:if>
<s:else>
	<s:set name="protocol" value="http" />
</s:else>

<header>
	<s:url action="Home" var="home_url" />
	<a href="${home_url}" class="logo"><img src="images/logo_sm.png" alt="Home" /></a>
	
	<div class="active">
		<s:include value="/struts/layout/chat.jsp" />
		
		<span class="phone">
			<img src="images/phone-icon.png" alt="Call Us" />${@com.picsauditing.util.Strings@getPicsPhone(permissions.country)}
		</span>
		
		<ul class="header-menu">
			<li>
				<span class="welcome-message"><s:text name="Header.Welcome" /></span>
			</li>
			<li>
				<s:a action="Login"><s:text name="Header.Login" /></s:a>
			</li>
		</ul>
	</div>
</header>