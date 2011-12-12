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
	<s:url action="Home" var="home_url" />
	<a href="${home_url}" class="logo"><img src="images/logo_sm.png" alt="Home" /></a>
	
	<div class="active">
		<ul class="locale">
			<li>
				<s:a>English</s:a>
			</li>
			<li>
				<s:a>Francais</s:a>
			</li>
			<li>
				<s:a>Spanish</s:a>
			</li>
		</ul>
		
		<s:include value="/struts/layout/chat.jsp" />
		
		<span class="phone">
			<img src="images/phone-icon.png" alt="Call Us" />${Strings.picsPhone(permissions.country.isoCode)}
		</span>
		
		<s:if test="permissions.loggedIn">
			<span class="welcome-message"><s:text name="Header.Welcome" />, ${permissions.name}</span>
		</s:if>
		
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
	</div>
</header>