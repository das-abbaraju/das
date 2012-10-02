<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%-- URL --%>
<s:url action="Login.action" var="loginform" />

<s:form cssClass="form-login well" action="%{#loginform}" name="login_form" id="login_form">
    <input type="hidden" name="button" value="Login" />
    
<!-- grab the switchToUser parameters if its passed to this page when switch servers.-->             
    <s:if test="switchToUser>0">                    
        <s:hidden id="switchServerToUser" name="switchServerToUser" value="%{switchToUser}" />
    </s:if>

    <h2></h2>

    <input id="login_modal_title" type="hidden" value="<s:text name="Login.h1" />" />

    <fieldset>
        <s:if test="configEnvironment || i18nReady">
            <input id="current_locale" type="hidden" value="<s:text name="locale" />" />
        </s:if>
        
        <div class="form-horizontal">
            <div class="control-group" id="update_language">
                <label class="control-label" for="supported_locales"><i class="icon-globe"></i></label>
                <div class="controls">
                    <s:include value="_supported-locales-list.jsp" />
                </div>
            </div>
        </div>
        
        <div id="user_fields">        
            <label for="username"><s:text name="global.Username" /></label>
            <s:textfield id="username" name="username" cssClass="" tabindex="1" />                
            <label for="password"><s:text name="global.Password" /></label>
            <s:password name="password" tabindex="2" cssClass="password" />                    
        </div>

        <div id="forgotPassword">
            <a href="AccountRecovery.action" tabindex="3"><s:text name="Login.Forgot" /></a>
        </div>
        
        <div id="remember_me">
            <s:checkbox name="rememberMe" value="false"></s:checkbox>
            <label for="rememberMe"><s:text name="Login.RememberMe" /></label>
        </div>
        
        <div class="form-actions">
            <button id="cancel_btn" type="button" class="btn" name="cancel"><s:text name="button.Cancel" /></button>
            <button type="submit" class="btn btn-primary"><s:text name="global.Login" /></button>
        </div>
    </fieldset>
</s:form>