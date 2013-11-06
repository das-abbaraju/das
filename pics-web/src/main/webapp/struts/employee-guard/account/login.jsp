<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="sign-up" var="employee_guard_account_create_url">
    <s:param name="hashCode">%{hashCode}</s:param>
</s:url>
<s:url action="welcome/submit" var="employee_guard_account_login_url" />
<s:url action="AccountRecovery.action" namespace="/" var="account_recovery_url" />

<div class="row">
    <div id="login-container" class="col-md-4 col-md-offset-4">
        <img src="/v7/img/logo/logo-large.png" class="logo img-responsive">

        <p class="text-center">
            <strong>${profile.firstName}, ${companyName} has requested that you join them.</strong>
        </p>

        <tw:form formName="employee_guard_login" action="${employee_guard_account_login_url}" method="post" class="form-horizontal login-form js-validation" role="form">
            <div class="form-group">
                <tw:label labelName="locale" class="col-xs-1 col-md-1 control-label locale-label"><i class="icon-globe icon-large"></i></tw:label>
                <div class="col-xs-11 col-md-11 locale-select">
                    <tw:select selectName="locale" tabindex="1" class="form-control select2">
                        <tw:option value="EN">English</tw:option>
                        <tw:option value="DK">Dansk</tw:option>
                    </tw:select>
                    <tw:error errorName="locale" />
                </div>
            </div>

            <div class="form-group">
                <tw:label labelName="username">Username</tw:label>
                <tw:input class="form-control" inputName="username" type="text" tabindex="2" />
                <tw:error errorName="username" />
            </div>

            <div class="form-group">
                <tw:label labelName="password">Password</tw:label>
                <tw:input class="form-control" inputName="password" type="password" tabindex="3" />
                <tw:error errorName="password" />

                <small><a href="${account_recovery_url}" tabindex="4" target="_blank">Forgot your login information?</a></small>
            </div>

            <div class="form-group form-actions">
                <tw:button buttonName="Login" type="submit" class="btn btn-primary btn-block">Login</tw:button>
                <a href="${employee_guard_account_create_url}" class="btn btn-default btn-block">Create an account</a>
            </div>
        </tw:form>
    </div>
</div>