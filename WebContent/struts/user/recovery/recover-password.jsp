<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%-- URL --%>
<s:url action="AccountRecovery" method="recoverUsername" var="username" />
<s:url action="AccountRecovery" method="resetPassword" var="reset_password" />
<s:url action="Login" var="login_page" />

<title><s:text name="AccountRecovery.title" /></title>

<div class="notice">
    <!--[if lte IE 8]><img class="logo" src="/v7/img/logo/logo-small.png"><!--<![endif]-->
    <!--[if gt IE 8]><!--><img class="logo" src="/v7/img/logo/logo-small.svg"><!--<![endif]-->

    <h1><s:text name="AccountRecovery.RecoverPassword" /></h1>

    <s:include value="/struts/_action-messages.jsp" />
    
    <form action="${reset_password}" method="post">
        <label for="username"><s:text name="global.Username" /></label>
        <input type="text" name="username" id="username" />
    
        <a href="${username}"><s:text name="AccountRecovery.ForgotName" /></a>
    
        <div class="form-actions">
            <a href="${login_page}" class="btn"><s:text name="button.Back" /></a>
            <button type="submit" class="btn btn-primary" tabindex="6"><s:text name="AccountRecovery.button.ResetPassword" /></button>
        </div>
    </form>
</div>
