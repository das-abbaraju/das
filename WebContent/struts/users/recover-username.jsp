<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%-- URL --%>
<s:url action="AccountRecovery" var="recover_password" />
<s:url action="AccountRecovery" method="findName" var="recover_username" />
<s:url action="Login" var="login_page" />

<title><s:text name="AccountRecovery.title" /></title>

<div class="notice">
    <!--[if lte IE 8]><img class="logo" src="/v7/img/logo/logo-small.png"><!--<![endif]-->
    <!--[if gt IE 8]><!--><img class="logo" src="/v7/img/logo/logo-small.svg"><!--<![endif]-->

    <h1><s:text name="AccountRecovery.RecoverUsername" /></h1>

    <form action="${recover_username}" method="post">
        <label for="email"><s:text name="global.Email" /></label>
        <input type="text" name="email" id="email" />
    
        <a href="${recover_password}"><s:text name="AccountRecovery.ForgotPassword" /></a>
    
        <div class="form-actions">
            <a href="${login_page}" class="btn"><s:text name="button.Back" /></a>
            <button type="submit" class="btn btn-primary" tabindex="6"><s:text name="AccountRecovery.EmailMe" /></button>
        </div>
    </form>
</div>
