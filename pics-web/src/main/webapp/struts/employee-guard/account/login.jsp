<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="sign-up" var="employee_guard_account_create_url">
    <s:param name="hashCode">${hashCode}</s:param>
</s:url>
<s:url action="welcome/submit" var="employee_guard_account_login_url" />
<s:url action="AccountRecovery.action" namespace="/" var="account_recovery_url" />

<div class="row" id="login_row">
    <div id="login-container" class="col-md-4 col-md-offset-4">
        <img src="/v7/img/logo/logo-large.png" class="logo img-responsive">

        <p class="text-center">
            <strong>
                <s:text name="EMPLOYEEGUARD.WELCOME.MESSAGE">
                    <s:param>${profile.firstName}</s:param>
                    <s:param>${companyName}</s:param>
                </s:text>
            </strong>
        </p>
        <tw:form formName="employee_guard_login" action="${employee_guard_account_login_url}" method="post" class="form-horizontal login-form js-validation" role="form">
            <tw:input inputName="hashCode" type="hidden" value="${hashCode}" />
            <div class="form-group">
                <label for="supported_locales" class="col-xs-1 col-md-1 control-label locale-label"><i class="icon-globe icon-large"></i></label>
                <div class="col-xs-11 col-md-11 locale-select">
                    <select id="supported_locales" name="request_locale" class="form-control select2Min" tabindex="1">
                        <s:iterator value="supportedLanguages.visibleLanguagesSansDialect" var="language">
                            <option value="${language.key}">${language.value}</option>
                        </s:iterator>
                    </select>
                </div>
            </div>

            <div class="form-group">
                <tw:label labelName="username"><s:text name="EMPLOYEEGUARD.WELCOME.USERNAME"/></tw:label>
                <tw:input class="form-control" inputName="username" type="text" tabindex="2" />
                <tw:error errorName="username" />
            </div>

            <div class="form-group">
                <tw:label labelName="password"><s:text name="EMPLOYEEGUARD.WELCOME.PASSWORD"/></tw:label>
                <tw:input class="form-control" inputName="password" type="password" tabindex="3" />
                <tw:error errorName="password" />

                <small><a href="${account_recovery_url}" tabindex="4" target="_blank"><s:text name="EMPLOYEEGUARD.WELCOME.FORGOT_YOUR_LOGIN_INFORMATION_LINK"/></a></small>
            </div>

            <div class="form-group form-actions">
                <tw:button buttonName="Login" type="submit" class="btn btn-primary btn-block"><s:text name="EMPLOYEEGUARD.WELCOME.LOGIN.BUTTON"/></tw:button>
                <a href="${employee_guard_account_create_url}" class="btn btn-default btn-block"><s:text name="EMPLOYEEGUARD.WELCOME.LOGIN.CREATE_AN_ACCOUNT.BUTTON"/></a>
            </div>
        </tw:form>
    </div>
</div>