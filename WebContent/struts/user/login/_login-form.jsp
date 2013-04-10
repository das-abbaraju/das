<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<%-- URL --%>
<s:url action="Login.action" var="loginform" />

<s:form id="login_form" name="login_form" action="%{#loginform}" cssClass="form-login">
    <input type="hidden" name="button" value="Login" />

<%-- grab the switchToUser parameters if its passed to this page when switch servers.--%>
    <s:if test="switchToUser > 0">
        <s:hidden id="switchServerToUser" name="switchServerToUser" value="%{switchToUser}" />
    </s:if>

    <img src="/v7/img/logo/logo-large.png" class="logo" />

    <s:include value="/struts/_action-messages.jsp" />

    <fieldset>
        <s:if test="configEnvironment || i18nReady">
            <div class="form-inline">
                <label for="supported_locales">
                    <i class="icon-globe"></i>
                </label>

                <s:include value="/struts/user/login/_supported-locales-list.jsp" />
            </div>
        </s:if>
        <s:include value="/struts/user/login/_login-form-fields.jsp"/>
    </fieldset>
</s:form>

<small>
    <a href="Registration.action" class="register"><s:text name="Login.Register" /></a>
</small>