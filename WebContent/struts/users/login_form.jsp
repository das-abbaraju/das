<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<div id="loginMessages">
<s:include value="../actionMessages.jsp"></s:include>
</div>

<div class="loginForm">
	<s:form id="login">
	<!-- grab the switchToUser parameters if its passed to this page when switch servers.-->				
		<s:if test="switchToUser>0">					
			<s:hidden id="switchServerToUser" name="switchServerToUser" value="%{switchToUser}" />
		</s:if>
		<fieldset class="form">
		<h2 class="formLegend"><s:text name="Login.h1" /></h2>
		<ol>
			<li>
				<label><s:text name="User.username" />:</label>
				<s:textfield id="username" name="username" cssClass="login" tabindex="1" />
			</li>
			<li>
				<label><s:text name="global.Password" />:</label>
				<s:password name="password" cssClass="login" tabindex="2" autocomplete="off" />
			</li>
		</ol>
		</fieldset>
		<fieldset class="form submit">
			<input type="submit" class="picsbutton positive" name="button" value="<s:text name="global.Login" />" tabindex="3"/>
		</fieldset>
	</s:form>

	<div id="forgot-contractorAsk" class="other">
		<p><s:text name="Login.Forgot" /> <a href="AccountRecovery.action"><s:text name="Login.ClickToRecover" /></a>.</p>
		<p><s:text name="Login.ContractorAsk" /> <a href="Registration.action"><s:text name="Login.ClickToRegister" /></a>.</p>
	</div>
</div>