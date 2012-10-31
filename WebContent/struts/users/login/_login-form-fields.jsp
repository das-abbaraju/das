<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ page import="com.picsauditing.toggle.FeatureToggle" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib" %>

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