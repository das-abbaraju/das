<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ page import="com.picsauditing.toggle.FeatureToggle" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<div id="user_fields">        
    <label for="username"><s:text name="global.Username" /></label>
    <s:textfield id="username" name="username" tabindex="2" />
                    
    <label for="password"><s:text name="global.Password" /></label>
    <s:password name="password" tabindex="3" cssClass="password" />                    
</div>

<div id="forgot_password">
    <small>
        <a href="AccountRecovery.action" tabindex="4"><s:text name="Login.Forgot" /></a>
    </small>
</div>


<div id="remember_me" class="form-inline">
    <label class="checkbox">
        <s:checkbox name="rememberMe" value="false" tabindex="5" />
        <s:text name="Login.RememberMe" />
    </label>
</div>


<div class="form-actions">
    <button type="submit" class="btn btn-primary" tabindex="6"><s:text name="global.Login" /></button>
</div>