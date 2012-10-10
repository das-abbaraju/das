<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ page import="com.picsauditing.toggle.FeatureToggle" %>
<%@ page import="com.picsauditing.util.LocaleController" %>
<%@ page import="com.picsauditing.actions.TranslationActionSupport" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<%-- URL --%>
<s:url action="Login.action" var="loginform" />

<div id="user_login">
    <s:form cssClass="form-login well" action="%{#loginform}" name="login_form" id="login_form">
        <input type="hidden" name="button" value="Login" />

    <%-- grab the switchToUser parameters if its passed to this page when switch servers.--%>
        <s:if test="switchToUser > 0">
            <s:hidden id="switchServerToUser" name="switchServerToUser" value="%{switchToUser}" />
        </s:if>

        <h1></h1>

        <s:include value="../../_action-messages.jsp" />

        <fieldset>
            <s:if test="configEnvironment || i18nReady">
                <input id="current_locale" type="hidden" value="<s:text name="locale" />" />
            </s:if>

            <div class="form-inline">
                <label for="supported_locales">
                    <i class="icon-globe"></i>
                    <s:include value="_supported-locales-list.jsp" />
                </label>
            </div>

            <s:include value="login/_login-form-fields.jsp"/>

            <div id="user_fields">
                <label for="username"><s:text name="global.Username" /></label>
                <s:textfield id="username" name="username" cssClass="" tabindex="2" />
                <label for="password"><s:text name="global.Password" /></label>
                <s:password name="password" tabindex="3" cssClass="password" />
            </div>

            <div id="forgotPassword">
                <a href="AccountRecovery.action" tabindex="4"><s:text name="Login.Forgot" /></a>
            </div>

            <pics:toggle name="<%= FeatureToggle.TOGGLE_SESSION_COOKIE %>">
                <div class="form-inline" id="remember_me">
                    <label class="checkbox">
                        <s:checkbox name="rememberMe" value="false" tabindex="5"></s:checkbox>
                        <s:text name="Login.RememberMe" />
                    </label>
                </div>
            </pics:toggle>

            <div class="form-actions">
                <button id="cancel_btn" type="button" class="btn" name="cancel"><s:text name="button.Cancel" /></button>
                <button type="submit" class="btn btn-primary" tabindex="6"><s:text name="global.Login" /></button>
            </div>
        </fieldset>
    </s:form>
</div>

<a id="registration_link" href="Registration.action"><s:text name="Login.Register" /></a>
