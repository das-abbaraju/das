<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="sign-up" var="employee_guard_account_create_url" method="create" />
<s:url action="welcome" var="employee_guard_account_login_url" includeParams="get">
    <s:param name="hashCode">${hashCode}</s:param>
</s:url>

<%-- Terms of Service Modal --%>
<s:include value="/struts/employee-guard/account/_terms-of-service.jsp" />

<div class="row" id="login_row">
    <div id="login-container" class="col-md-4 col-md-offset-4">
        <img src="/v7/img/logo/logo-large.png" class="logo img-responsive">

        <tw:form formName="employee_guard_create_account" action="${employee_guard_account_create_url}" method="post" class="form-horizontal login-form js-validation" role="form">
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
                <tw:label labelName="firstName"><s:text name="EMPLOYEEGUARD.SIGNUP.FIRST_NAME"/></tw:label>
                <tw:input class="form-control" inputName="firstName" type="text" tabindex="4" value="${profile.firstName}" maxlength="100" />
                <tw:error errorName="firstName" />
            </div>

            <div class="form-group">
                <tw:label labelName="lastName"><s:text name="EMPLOYEEGUARD.SIGNUP.LAST_NAME"/></tw:label>
                <tw:input class="form-control" inputName="lastName" type="text" tabindex="5" value="${profile.lastName}" maxlength="100" />
                <tw:error errorName="lastName"/>
            </div>

            <div class="form-group">
                <tw:label labelName="email"><s:text name="EMPLOYEEGUARD.SIGNUP.EMAIL"/></tw:label>
                <tw:input class="form-control" inputName="email" type="email" tabindex="6" value="${profile.email}" maxlength="70" />
                <tw:error errorName="email"/>
            </div>

            <div class="form-group">
                <tw:label labelName="emailRetype"><s:text name="EMPLOYEEGUARD.SIGNUP.RE_ENTER_EMAIL"/></tw:label>
                <tw:input class="form-control" inputName="emailRetype" type="email" tabindex="7" maxlength="70" />
                <tw:error errorName="emailRetype" />
            </div>

            <div class="form-group">
                <tw:label labelName="password"><s:text name="EMPLOYEEGUARD.SIGNUP.PASSWORD"/></tw:label>
                <tw:input class="form-control" inputName="password" type="password" tabindex="8" maxlength="100"/>
                <tw:error errorName="password" />
            </div>

            <div class="form-group" id="terms_of_service">
                <div class="checkbox">
                    <tw:label labelName="tos" class="control-label">
                        <tw:input inputName="tos" class="required" type="checkbox" value="true"/> <s:text name="EMPLOYEEGUARD.SIGNUP.AGREE_TO_TOS.CHECKBOX"/>
                    </tw:label>
                </div>
            </div>

            <div class="form-group form-actions">
                <tw:button buttonName="Create" type="submit" class="btn btn-primary btn-block"><s:text name="EMPLOYEEGUARD.SIGNUP.CREATE_ACCOUNT.BUTTON"/></tw:button>
                <a href="${employee_guard_account_login_url}" class="btn btn-default btn-block"><s:text name="EMPLOYEEGUARD.SIGNUP.RETURN_TO_LOGIN.BUTTON"/></a>
            </div>
        </tw:form>
    </div>
</div>