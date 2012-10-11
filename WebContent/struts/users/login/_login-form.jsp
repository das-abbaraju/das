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
                <% String currentLanguage = LocaleController.getValidLocale(TranslationActionSupport.getLocaleStatic()).getLanguage(); %>
                <input id="current_locale" type="hidden" value="<%= currentLanguage %>" />
            </s:if>

            <div class="form-inline">
                <label for="supported_locales">
                    <i class="icon-globe"></i>
                    <s:include value="_supported-locales-list.jsp" />
                </label>
            </div>

            <s:include value="_login-form-fields.jsp"/>

        </fieldset>
    </s:form>
</div>

<a id="registration_link" href="Registration.action"><s:text name="Login.Register" /></a>
