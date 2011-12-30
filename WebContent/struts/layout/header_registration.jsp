<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
	<img src="images/logo_sm.png" alt="Home" class="logo" />
	
	<ul class="header-menu">
		<li>
			<s:if test="permissions.loggedIn">
				<s:a action="Login?button=logout"><s:text name="Header.Logout" /></s:a>
			</s:if>
			<s:else>
				<s:a action="Login"><s:text name="Header.Login" /></s:a>
			</s:else>
		</li>
	</ul>
	
	<s:if test="permissions.loggedIn">
		<span class="welcome-message"><s:text name="Header.Welcome" />, ${permissions.name}</span>
	</s:if>
	
	<span class="phone">
		<img src="images/phone-icon.png" alt="Call Us" /><s:property value="%{@com.picsauditing.util.Strings@getPicsPhone(permissions.country)}" />
	</span>
	
	<s:include value="/struts/layout/chat.jsp" />
	
	<s:if test="actionName == 'Registration'">
		<s:form id="locale" cssClass="locale-form">
			<s:select
				label=""
				list="#{'en': 'English', 'fr': 'Français', 'es': 'Español'}"
				name="request_locale"
				value="locale"
			/>
		</s:form>
	</s:if>
</header>