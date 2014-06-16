<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:text name="%{contractorCountry.i18nKey}" var="phone_country" />

<s:url action="Login?button=logout" var="logout_url" />
<s:url action="Login" var="login_url" />


<%--<s:if test="permissions.loggedIn">--%>
    <%--<s:a action="Login?button=logout"><s:text name="Header.Logout" /></s:a>--%>
<%--</s:if>--%>
<%--<s:else>--%>
    <%--<s:a action="Login"><s:text name="Header.Login" /></s:a>--%>
<%--</s:else>--%>

<img src="images/logo_sm.png" alt="Home" class="logo" />
<ul class="header-components pull-right">
    <li>
        <i class="icon-comment icon-large"></i>
        <s:include value="/struts/layout/chat.jsp" />
    </li>
    <li>
        <i class="icon-phone icon-large"></i>
        <span class="phone pics_phone_number" title="${phone_country}">${salesPhoneNumber}</span>
    </li>
    <li>
        <s:if test="permissions.loggedIn">
            <span class="welcome-message"><s:text name="Header.Welcome" />, ${permissions.name}</span>
        </s:if>
    </li>
    <li>
        <s:if test="permissions.loggedIn">
            <a href="${logout_url}"><s:text name="Header.Logout" /></a>
        </s:if>
        <s:else>
            <a href="${login_url}"><s:text name="Header.Login" /></a>
        </s:else>
    </li>
</ul>