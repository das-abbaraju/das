<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
		
<s:text name="%{contractorCountry.i18nKey}" var="phone_country" />

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
	
	<span id="pics_phone_number" class="phone" title="${phone_country}">${picsPhoneNumber}</span>
	
	<s:include value="/struts/layout/chat.jsp" />
</header>