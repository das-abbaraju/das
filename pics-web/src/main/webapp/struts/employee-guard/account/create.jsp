<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="sign-up" var="employee_guard_account_create_url" method="create" />
<s:url action="welcome" var="employee_guard_account_login_url" includeParams="get">
    <s:param name="hashCode">${hashCode}</s:param>
</s:url>

<div class="row">
    <div id="login-container" class="col-md-4 col-md-offset-4">
        <img src="/v7/img/logo/logo-large.png" class="logo img-responsive">

        <tw:form formName="employee_guard_create_account" action="${employee_guard_account_create_url}" method="post" class="form-horizontal login-form" role="form">
            <input type="hidden" name="hashCode" value="${hashCode}" />

            <div class="form-group">
                <tw:label labelName="locale" class="col-xs-1 col-md-1 control-label locale-label"><i class="icon-globe icon-large"></i></tw:label>
                <div class="col-xs-11 col-md-11 locale-select">
                    <tw:select selectName="locale" tabindex="1" class="form-control">
                        <tw:option value="EN">English</tw:option>
                        <tw:option value="DK">Dansk</tw:option>
                    </tw:select>
                    <tw:error errorName="locale" />
                </div>
            </div>

            <div class="form-group">
                <tw:label labelName="firstName">First Name</tw:label>
                <tw:input class="form-control" inputName="firstName" type="text" tabindex="4" value="${profile.firstName}" />
                <tw:error errorName="firstName" />
            </div>

            <div class="form-group">
                <tw:label labelName="lastName">Last Name</tw:label>
                <tw:input class="form-control" inputName="lastName" type="text" tabindex="5" value="${profile.lastName}" />
                <tw:error errorName="lastName" />
            </div>

            <div class="form-group">
                <tw:label labelName="email">Email</tw:label>
                <tw:input class="form-control" inputName="email" type="email" tabindex="6" value="${profile.email}" />
                <tw:error errorName="email" />
            </div>

            <div class="form-group">
                <tw:label labelName="emailRetype">Re-Enter Email</tw:label>
                <tw:input class="form-control" inputName="emailRetype" type="email" tabindex="7" />
                <tw:error errorName="emailRetype" />
            </div>

            <div class="form-group">
                <tw:label labelName="password">Password</tw:label>
                <tw:input class="form-control" inputName="password" type="password" tabindex="8" />
                <tw:error errorName="password" />

                <div class="checkbox">
                    <tw:label labelName="tos" class="control-label">
                        <tw:input inputName="tos" class="required" type="checkbox" value="false"/> I agree to the <a href="#">Terms of Service</a>
                    </tw:label>
                </div>
            </div>

            <div class="form-group form-actions">
                <tw:button buttonName="Create" type="submit" class="btn btn-primary btn-block">Create Account</tw:button>
                <a href="${employee_guard_account_login_url}" class="btn btn-default btn-block">Return to login</a>
            </div>
        </tw:form>
    </div>
</div>